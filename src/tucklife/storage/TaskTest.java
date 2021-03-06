//@@author A0111101N
package tucklife.storage;

import static org.junit.Assert.*;

import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;

import tucklife.parser.Parser;
import tucklife.parser.ProtoTask;
import tucklife.storage.internal.StorageExceptions.InvalidDateException;

public class TaskTest {

	private Task t;
	private int id;
	private Parser p;
	private ProtoTask pt;

	@Before
	public void setUp() throws Exception {
		p = new Parser();
		Hashtable<String, String> ht = new Hashtable<String, String>();
		p.loadCommands(ht);
		Task.resetGlobalId();
	}

	@Test
	public void testTask() throws InvalidDateException {
		pt = p.parse("add meeting");
		Task t = new Task(pt);
		assertEquals("fail to create task", "meeting", t.getName());
		assertEquals("unable to get correct location", null, t.getLocation());
		pt = p.parse("add meeting aylward @mr3");
		t = new Task(pt);
		assertEquals("unable to get correct location", "mr3", t.getLocation());
	}

	@Test
	public void testDisplay() throws InvalidDateException {
		pt = p.parse("add meeting");
		t = new Task(pt);
		id = t.getId();
		String taskDisplay = String.format("%1$s. meeting        ", id);
		assertEquals("fail to display", taskDisplay, t.display());
		pt = p.parse("add meeting aylward @mr3 +1200 $16/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		taskDisplay = String.format("%1$s. meeting aylward | By: Mon, 16 May 2016 12:00", id);
		assertEquals("fail to display properly", taskDisplay, t.display());
		taskDisplay = String.format(
				"%1$s. meeting aylward | By: Mon, 16 May 2016 12:00 | Location: mr3 | Category: important | Additional: bring all documents",
				id);
		assertEquals("fail to display all properly", taskDisplay, t.displayAll());
	}

	@Test
	public void testEditRemoveParam() throws InvalidDateException {
		pt = p.parse("add meeting aylward @mr3 +1200 $16/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		String taskDisplay = String.format("%1$s. meeting aylward | By: Mon, 16 May 2016 12:00 | Location: mr3", id);
		pt = p.parse(String.format("edit %1$s @ +1300 $17/05 &", id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. meeting aylward | By: Tue, 17 May 2016 13:00", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
		taskDisplay = String.format("%1$s. meeting aylward | By: Tue, 17 May 2016 13:00 | Category: important", id);
		assertEquals("fail to display all properly", taskDisplay, t.displayAll());
	}

	@Test
	public void testEditRemoveParamFloating() throws InvalidDateException {
		pt = p.parse("add meeting aylward @mr3 +1200 $16/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		String taskDisplay = String.format("%1$s. meeting aylward | By: Mon, 16 May 2016 12:00 | Location: mr3", id);
		pt = p.parse(String.format("edit %1$s @mr4 &bring yourself +", id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. meeting aylward", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
		taskDisplay = String
				.format("%1$s. meeting aylward | Location: mr4 | Category: important | Additional: bring yourself", id);
		assertEquals("fail to display all properly", taskDisplay, t.displayAll());
	}

	@Test
	public void testEditDisplayAll() throws InvalidDateException {
		pt = p.parse("add meeting aylward @mr3 +1200 $16/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		String taskDisplay = String.format("%1$s. meeting aylward | By: Mon, 16 May 2016 12:00 | Location: mr3", id);
		pt = p.parse(String.format("edit %1$s @mr4 +1300 &bring yourself", id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. meeting aylward | By: Mon, 16 May 2016 13:00", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
		taskDisplay = String.format(
				"%1$s. meeting aylward | By: Mon, 16 May 2016 13:00 | Location: mr4 | Category: important | Additional: bring yourself",
				id);
		assertEquals("fail to display all properly", taskDisplay, t.displayAll());
	}

	@Test
	public void testEditEventToEventWithoutDate2() throws InvalidDateException {
		pt = p.parse("add meeting aylward @mr3 +1200 to 1300 $16/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 +1100 to 1200", id));
		t.edit(pt);
		String taskDisplayEdit = String
				.format("%1$s. meeting aylward | From: Mon, 16 May 2016 11:00 To: Mon, 16 May 2016 12:00", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
	}

	@Test
	public void testEditEventToEventWithoutDate() throws InvalidDateException {
		pt = p.parse("add meeting aylward @mr3 +1200 to 1300 $16/05 to 17/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 +0900 to 1000", id));
		t.edit(pt);
		String taskDisplayEdit = String
				.format("%1$s. meeting aylward | From: Mon, 16 May 2016 09:00 To: Tue, 17 May 2016 10:00", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
	}

	@Test
	public void testEditEventToDeadline() throws InvalidDateException {
		pt = p.parse("add meeting aylward @mr3 +1200 to 1300 $16/05 to 17/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 +0900 $16/05", id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. meeting aylward | By: Mon, 16 May 2016 09:00", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
	}

	@Test
	public void testEditEventToEventWithoutTime() throws InvalidDateException {
		pt = p.parse("add company trip with boss @mr3 +0900 to 1000 $19/05 to 20/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 $16/05 to 17/05", id));
		t.edit(pt);
		String taskDisplayEdit = String
				.format("%1$s. company trip... | From: Mon, 16 May 2016 09:00 To: Tue, 17 May 2016 10:00", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
	}

	@Test
	public void testEditDeadlineToDeadlineWithoutTime() throws InvalidDateException {
		pt = p.parse("add company trip with boss $17/06 +1500");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 $16/05", id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. company trip... | By: Mon, 16 May 2016 15:00", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
	}

	@Test
	public void testEditFloatToDeadline() throws InvalidDateException {
		pt = p.parse("add company trip with boss");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 $16/05 +1500", id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. company trip... | By: Mon, 16 May 2016 15:00", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
	}

	@Test
	public void testEditFloatToDeadlineWithoutTime() throws InvalidDateException {
		pt = p.parse("add company trip with boss");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 $16/05", id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. company trip... | By: Mon, 16 May 2016 23:59", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
	}

	@Test
	public void testEditFloatToEventWithoutTime() throws InvalidDateException {
		pt = p.parse("add company trip with boss");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 $16/05 to 17/05", id));
		t.edit(pt);
		String taskDisplayEdit = String
				.format("%1$s. company trip... | From: Mon, 16 May 2016 00:00 To: Tue, 17 May 2016 23:59", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
	}

	@Test
	public void testEditEventToDeadlineWithoutTime() throws InvalidDateException {
		pt = p.parse("add meeting aylward @mr3 +1200 to 1300 $16/05 to 17/05 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 $16/05", id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. meeting aylward | By: Mon, 16 May 2016 23:59", id);
		assertEquals(pt.getStartDate(), null);
		assertEquals(pt.getStartTime(), null);
		assertEquals(pt.getEndTime(), null);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
	}

	@Test
	public void testEditDeadlineToDeadlineWithoutDate() throws InvalidDateException {
		pt = p.parse("add meeting aylward @mr3 $16/05 +2100 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 +0900", id));
		t.edit(pt);
		String taskDisplayEdit = String.format("%1$s. meeting aylward | By: Mon, 16 May 2016 09:00", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
	}

	@Test
	public void testEditDeadlineToEventWithoutDate() throws InvalidDateException {
		pt = p.parse("add meeting aylward @mr3 $16/05 +2100 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 +0900 to 1700", id));
		t.edit(pt);
		String taskDisplayEdit = String
				.format("%1$s. meeting aylward | From: Mon, 16 May 2016 09:00 To: Mon, 16 May 2016 17:00", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
	}

	@Test
	public void testEditDeadlineToEventWithoutDate2() throws InvalidDateException {
		pt = p.parse("add meeting aylward @mr3 $16/05 +2100 #important &bring all documents");
		t = new Task(pt);
		id = t.getId();
		pt = p.parse(String.format("edit %1$s @mr4 +2200 to 1000", id));
		t.edit(pt);
		String taskDisplayEdit = String
				.format("%1$s. meeting aylward | From: Mon, 16 May 2016 22:00 To: Tue, 17 May 2016 10:00", id);
		assertEquals("fail to display properly", taskDisplayEdit, t.display());
	}

	// @@author A0124274L
	@Test
	public void testSearch1() throws InvalidDateException {
		ProtoTask pt1 = p.parse("add go to gap @mr3 +1200 $16/05 #important &bring all documents");
		t = new Task(pt1);
		assertEquals(true, t.containsExact("gAp"));
	}

	@Test
	public void testSearch2() throws InvalidDateException {
		ProtoTask pt2 = p.parse("add travelling @Singapore");
		t = new Task(pt2);
		assertEquals(true, t.containsPartial("gap"));
	}
}
