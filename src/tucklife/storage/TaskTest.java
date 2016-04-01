package tucklife.storage;

import static org.junit.Assert.*;

import org.junit.Test;

import tucklife.parser.Parser;
import tucklife.parser.ProtoTask;

public class TaskTest {


	private Task t;
	private int id;
	private Parser p;
	private ProtoTask pt;

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
		taskDisplay = String.format("%1$s. meeting with boss | By: Mon, 16 May 2016 12:00 | Location: mr3 | Category: important",id);
		assertEquals("fail to display properly", t.display(), taskDisplay);
		taskDisplay = String.format("%1$s. meeting with boss | By: Mon, 16 May 2016 12:00 | Location: mr3 | Category: important | Additional: bring all documents",id);
		assertEquals("fail to display all properly", t.displayAll(), taskDisplay);
	}
	
	@Test
	public void testEdit() {
		pt = p.parse("add meeting with boss @mr3 +1200 $16/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		String taskDisplay = String.format("%1$s. meeting with boss | By: Mon, 16 May 2016 12:00 | Location: mr3 | Category: important",id);
		pt = p.parse(String.format("edit %1$s @mr4 +1300 $17/05",id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. meeting with boss | By: Tue, 17 May 2016 13:00 | Location: mr4 | Category: important",id);
		assertEquals("fail to display properly", t.display(), taskDisplayEdit);
		taskDisplay = String.format("%1$s. meeting with boss | By: Tue, 17 May 2016 13:00 | Location: mr4 | Category: important | Additional: bring all documents",id);
		assertEquals("fail to display all properly", t.displayAll(), taskDisplay);
	}
}
