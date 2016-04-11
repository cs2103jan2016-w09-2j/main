//@@author A0111101N
package tucklife.storage;

import static org.junit.Assert.*;

import java.util.Hashtable;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import tucklife.parser.Parser;
import tucklife.parser.ProtoTask;
import tucklife.storage.internal.StorageExceptions;
import tucklife.storage.internal.StorageExceptions.InvalidDateException;

public class TaskListTest {

	private Task t;
	private int id;
	private Parser p;
	private ProtoTask pt;
	private TaskList tl;

	@Before
	public void setUp() throws Exception {
		p = new Parser();
		Hashtable<String, String> ht = new Hashtable<String, String>();
		p.loadCommands(ht);
	}

	@Test
	public void testAdd() throws StorageExceptions.InvalidDateException {
		tl = new TaskList();
		pt = p.parse("add meeting");
		tl.add(pt);
		assertEquals("fail to add", tl.size(), 1);
		tl.add(pt);
		assertEquals("fail to add", tl.size(), 2);
		tl.add(pt);
		tl.add(pt);
		tl.add(pt);
		assertEquals("fail to add", tl.size(), 5);
	}

	@Test
	public void testContains() throws InvalidDateException {
		tl = new TaskList();
		pt = p.parse("add meeting");
		t = new Task(pt);
		id = t.getId();
		tl.add(t);
		assertEquals("tl contains, but it doesnt show", true, tl.contains(id));
		assertEquals("contains does not always return true", false, tl.contains(id + 2));
		t = new Task(pt);
		tl.add(t);
		t = new Task(pt);
		tl.add(t);
		assertEquals("tl contains does not work after adding multiple stuff", true, tl.contains(id));
	}

	@Test
	public void testDelete() throws InvalidDateException {
		tl = new TaskList();
		pt = p.parse("add meeting");
		t = new Task(pt);
		id = t.getId();
		Task oldTask = t;
		tl.add(t);
		t = new Task(pt);
		tl.add(t);
		int size = tl.size();
		assertEquals("tasklist does not contain id. should return null.", null, tl.delete(id - 1));
		assertEquals("tasklist size should not change", size, tl.size());
		assertEquals("incorrect task deleted", oldTask, tl.delete(id));
		assertEquals("tasklist size should change", size - 1, tl.size());
	}

	@Test
	public void testEdit() throws InvalidDateException {
		tl = new TaskList();
		pt = p.parse("add meeting @mr4 $16/05 +1300 #intern");
		t = new Task(pt);
		tl.add(t);
		int size = tl.size();
		id = t.getId();
		Task oldTask = t;
		pt = p.parse(String.format("edit %s$1 @mr5 #management #intern", id));
		tl.edit(id, pt);
		assertEquals("tasklist size should not change", size, tl.size());
		pt = p.parse("add hire interns");
		t = new Task(pt);
		tl.add(t);
		assertEquals("the task should not change", oldTask, tl.get(id));
	}

	@Test
	public void testSort() throws InvalidDateException {
		tl = new TaskList();
		pt = p.parse("add meeting @a");
		t = new Task(pt);
		tl.add(t);
		pt = p.parse("add meeting @c");
		t = new Task(pt);
		tl.add(t);
		pt = p.parse("add meeting @f");
		t = new Task(pt);
		tl.add(t);
		pt = p.parse("add meeting @z");
		t = new Task(pt);
		tl.add(t);
		pt = p.parse("add meeting @m");
		t = new Task(pt);
		tl.add(t);
		tl.sort("@", true);
		assertEquals("location should be sorted", true, checkSorted(tl));
	}

	private boolean checkSorted(TaskList unsorted) {
		Iterator<Task> iter = unsorted.iterator();
		Task t = iter.next();
		String prev = t.getLocation();
		while (iter.hasNext()) {
			t = iter.next();
			String curr = t.getLocation();
			if (curr.compareTo(prev) < 0) {
				return false;
			}
			prev = curr;
		}
		return true;
	}

}
