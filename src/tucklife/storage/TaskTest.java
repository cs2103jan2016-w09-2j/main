//@@author A0111101N
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
	public void testTask() throws invalidDateException {
		pt = p.parse("add meeting");
		Task t = new Task(pt);
		assertEquals("fail to create task", t.getName(), "meeting");
		assertEquals("unable to get correct location", t.getLocation(), null);
		pt = p.parse("add meeting with boss @mr3");
		t = new Task(pt);
		assertEquals("unable to get correct location", t.getLocation(), "mr3");
	}
	
	@Test
	public void testDisplay() throws invalidDateException {
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
		assertEquals("fail to display all properly", taskDisplay, t.displayAll());
	}
	
	@Test
	public void testEdit() throws invalidDateException {
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
	public void testEdit2() throws invalidDateException {
		pt = p.parse("add meeting with boss @mr3 +1200 $16/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		String taskDisplay = String.format("%1$s. meeting with boss | By: Mon, 16 May 2016 12:00 | Location: mr3",id);
		pt = p.parse(String.format("edit %1$s @mr4 +1300",id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. meeting with boss | By: Mon, 16 May 2016 13:00 | Location: mr4",id);
		assertEquals("fail to display properly", t.display(), taskDisplayEdit);
		taskDisplay = String.format("%1$s. meeting with boss | By: Mon, 16 May 2016 13:00 | Location: mr4 | Category: important | Additional: bring all documents",id);
		assertEquals("fail to display all properly", t.displayAll(), taskDisplay);
	}
	
	@Test
	public void testEdit3() throws invalidDateException {
		pt = p.parse("add meeting with boss @mr3 +1200 to 1300 $16/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 +1100 to 1200",id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. meeting with boss | From: Mon, 16 May 2016 11:00 To: Mon, 16 May 2016 12:00 | Location: mr4",id);
		assertEquals("fail to display properly", t.display(), taskDisplayEdit);
	}
	
	@Test
	public void testEdit4() throws invalidDateException {
		pt = p.parse("add meeting with boss @mr3 +1200 to 1300 $16/05 to 17/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 +0900 to 1000",id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. meeting with boss | From: Mon, 16 May 2016 09:00 To: Tue, 17 May 2016 10:00 | Location: mr4",id);
		assertEquals("fail to display properly", t.display(), taskDisplayEdit);
	}
	
	@Test
	public void testEdit5() throws invalidDateException {
		pt = p.parse("add meeting with boss @mr3 +1200 to 1300 $16/05 to 17/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 +0900 $16/05",id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. meeting with boss | By: Mon, 16 May 2016 09:00 | Location: mr4",id);
		assertEquals("fail to display properly", t.display(), taskDisplayEdit);
	}
	
	@Test
	public void testEdit6() throws invalidDateException {
		pt = p.parse("add company trip with boss @mr3 +0900 to 1000 $19/05 to 20/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 $16/05 to 17/05",id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. company trip with boss | From: Mon, 16 May 2016 09:00 To: Tue, 17 May 2016 10:00 | Location: mr4",id);
		assertEquals("fail to display properly", t.display(), taskDisplayEdit);
	}
	
	@Test
	public void testEdit7() throws invalidDateException {
		pt = p.parse("add company trip with boss @mr3 +1700 $19/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 $16/05",id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. company trip with boss | By: Mon, 16 May 2016 17:00 | Location: mr4",id);
		assertEquals("fail to display properly", t.display(), taskDisplayEdit);
	}
	
	@Test
	public void testEdit8() throws invalidDateException {
		pt = p.parse("add company trip with boss $17/06 +1500");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 $16/05",id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. company trip with boss | By: Mon, 16 May 2016 15:00 | Location: mr4",id);
		assertEquals("fail to display properly", t.display(), taskDisplayEdit);
	}
	
	@Test
	public void testEdit9() throws invalidDateException {
		pt = p.parse("add company trip with boss");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 $16/05 +1500",id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. company trip with boss | By: Mon, 16 May 2016 15:00 | Location: mr4",id);
		assertEquals("fail to display properly", t.display(), taskDisplayEdit);
	}
	
	@Test
	public void testEdit10() throws invalidDateException {
		pt = p.parse("add company trip with boss");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 $16/05",id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. company trip with boss | By: Mon, 16 May 2016 23:59 | Location: mr4",id);
		assertEquals("fail to display properly", t.display(), taskDisplayEdit);
	}
	
	@Test
	public void testEdit11() throws invalidDateException {
		pt = p.parse("add company trip with boss");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 $16/05 to 17/05",id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. company trip with boss | From: Mon, 16 May 2016 00:00 To: Tue, 17 May 2016 23:59 | Location: mr4",id);
		assertEquals("fail to display properly", t.display(), taskDisplayEdit);
	}
	
	@Test
	public void testEdit12() throws invalidDateException {
		pt = p.parse("add meeting with boss @mr3 +1200 to 1300 $16/05 to 17/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 $16/05",id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. meeting with boss | By: Mon, 16 May 2016 23:59 | Location: mr4",id);
		assertEquals(pt.getStartDate(),null);
		assertEquals(pt.getStartTime(),null);
		assertEquals(pt.getEndTime(),null);
		//assertEquals(pt.getEndDate(),null);
		assertEquals("fail to display properly", t.display(), taskDisplayEdit);
	}
	
	@Test
	public void testEdit13() throws invalidDateException {
		pt = p.parse("add meeting with boss @mr3 +1200 to 1300 $16/05 to 17/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 +0900",id));
		try {
			t.edit(pt);
		} catch (invalidDateException e) {
			 String errorMessage = "Date is invalid";
			 assertEquals("error thrown", e.getErrorMessage(), errorMessage);
		}
		
	}
	
	@Test
	public void testSearch1() throws invalidDateException {
		ProtoTask pt1 = p.parse("add go to gap @mr3 +1200 $16/05 #important &bring all documents");
		t = new Task(pt1);
		t.containsExact("gAp");
	}
	
	@Test
	public void testSearch2() throws invalidDateException {
		ProtoTask pt2 = p.parse("add travelling @Singapore");
		t = new Task(pt2);
		t.containsPartial("gap");
	}
}

