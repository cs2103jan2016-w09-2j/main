package tucklife.storage;

import static org.junit.Assert.*;

import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;

import tucklife.parser.Parser;
import tucklife.parser.ProtoTask;

public class TaskTest {


	private Task t;
	private int id;
	private Parser p;
	private ProtoTask pt;

	@Before
	public void setUp() throws Exception {
		p = new Parser();
		Hashtable<String,String> ht = new Hashtable<String,String>();
		p.loadCommands(ht);
	}
	
	@Test
	public void testTask() {
		pt = p.parse("add meeting");
		Task t = new Task(pt);
		assertEquals("fail to create task", t.getName(), "meeting");
		assertEquals("unable to get correct location", t.getLocation(), null);
		pt = p.parse("add meeting with boss @mr3");
		t = new Task(pt);
		assertEquals("unable to get correct location", t.getLocation(), "mr3");
	}
	
	@Test
	public void testDisplay() {
		pt = p.parse("add meeting");
		t = new Task(pt);
		id = t.getId();
		String taskDisplay = String.format("%1$s. meeting",id);
		assertEquals("fail to display", t.display(), taskDisplay);
		pt = p.parse("add meeting with boss @mr3 +1200 $16/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		taskDisplay = String.format("%1$s. meeting with boss | By: Mon, 16 May 2016 12:00 | Location: mr3",id);
		assertEquals("fail to display properly", t.display(), taskDisplay);
		taskDisplay = String.format("%1$s. meeting with boss | By: Mon, 16 May 2016 12:00 | Location: mr3 | Category: important | Additional: bring all documents",id);
		assertEquals("fail to display all properly", t.displayAll(), taskDisplay);
	}
	
	@Test
	public void testEdit() {
		pt = p.parse("add meeting with boss @mr3 +1200 $16/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		String taskDisplay = String.format("%1$s. meeting with boss | By: Mon, 16 May 2016 12:00 | Location: mr3",id);
		pt = p.parse(String.format("edit %1$s @mr4 +1300 $17/05",id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. meeting with boss | By: Tue, 17 May 2016 13:00 | Location: mr4",id);
		assertEquals("fail to display properly", t.display(), taskDisplayEdit);
		taskDisplay = String.format("%1$s. meeting with boss | By: Tue, 17 May 2016 13:00 | Location: mr4 | Category: important | Additional: bring all documents",id);
		assertEquals("fail to display all properly", t.displayAll(), taskDisplay);
	}
	
	@Test
	public void testSearch1() {
		ProtoTask pt1 = p.parse("add go to gap @mr3 +1200 $16/05 #important &bring all documents");
		t = new Task(pt1);
		t.containsExact("gAp");
	}
	
	@Test
	public void testSearch2() {
		ProtoTask pt2 = p.parse("add travelling @Singapore");
		t = new Task(pt2);
		t.containsPartial("gap");
	}
}

