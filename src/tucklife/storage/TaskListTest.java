//A0111101N
package tucklife.storage;

import static org.junit.Assert.*;

import java.util.Hashtable;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import tucklife.parser.Parser;
import tucklife.parser.ProtoTask;

public class TaskListTest {
	
	private Task t;
	private int id;
	private Parser p;
	private ProtoTask pt;
	private TaskList tl;
	

	@Before
	public void setUp() throws Exception {
		p = new Parser();
		Hashtable<String,String> ht = new Hashtable<String,String>();
		p.loadCommands(ht);
	}
	
	@Test
	public void testAdd() throws invalidDateException {
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
	public void testContains() throws invalidDateException {
		tl = new TaskList();
		pt = p.parse("add meeting");
		t = new Task(pt);
		id = t.getId();
		tl.add(t);
		assertEquals("tl contains, but it doesnt show", tl.contains(id), true);
		assertEquals("contains does not always return true", tl.contains(id+2), false);
		t = new Task(pt);
		tl.add(t);
		t = new Task(pt);
		tl.add(t);
		assertEquals("tl contains does not work after adding multiple stuff", tl.contains(id), true);
	}
	
	@Test
	public void testDelete() throws invalidDateException {
		tl = new TaskList();
		pt = p.parse("add meeting");
		t = new Task(pt);
		id = t.getId();
		Task oldTask = t;
		tl.add(t);
		t = new Task(pt);
		tl.add(t);
		int size = tl.size();
		assertEquals("tasklist does not contain id. should return null.", tl.delete(id-1), null);
		assertEquals("tasklist size should not change", tl.size(), size);
		assertEquals("incorrect task deleted", tl.delete(id),oldTask);
		assertEquals("tasklist size should change", tl.size(), size - 1);
	}
	
	@Test
	public void testEdit() throws invalidDateException {
		tl = new TaskList();
		pt = p.parse("add meeting @mr4 $16/05 +1300 #intern");
		t = new Task(pt);
		tl.add(t);
		int size = tl.size();
		id = t.getId();
		Task oldTask = t;
		pt = p.parse(String.format("edit %s$1 @mr5 #management #intern",id));
		tl.edit(id,pt);
		assertEquals("tasklist size should not change", tl.size(), size);
		pt = p.parse("add hire interns");
		t = new Task(pt);
		tl.add(t);
		assertEquals("the task should not change", tl.get(id), oldTask);
	}
	
	@Test
	public void testSort() throws invalidDateException {
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
		tl.sort("@",true);
		assertEquals("location should be sorted", checkSorted(tl), true);
	}
	
	private boolean checkSorted(TaskList unsorted) {
		Iterator<Task> iter = unsorted.iterator();
		Task t = iter.next();
		String prev = t.getLocation();
		while(iter.hasNext()){
			t = iter.next();
			String curr = t.getLocation();
			if(curr.compareTo(prev)<0) {
				return false;
			}
			prev = curr;
		}
		return true;
	}
	

}
