package tucklife.storage;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import tucklife.parser.Parser;
import tucklife.parser.ProtoTask;

public class StorageTester {

	private ProtoTaskStubForStorage ptDisplay;
	private ProtoTaskStubForStorage ptDisplayDone;
	private ProtoTaskStubForStorage ptDisplayID;
	private ProtoTaskStubForStorage ptEvent;
	private ProtoTaskStubForStorage ptEventComplete;
	private ProtoTaskStubForStorage ptEventCompleteFail;
	private ProtoTaskStubForStorage ptEventDelete;
	private ProtoTaskStubForStorage ptEventDeleteFail;
	private ProtoTaskStubForStorage ptEventEdit;
	private ProtoTaskStubForStorage ptEventEditID;
	private ProtoTaskStubForStorage ptDeadline;
	private Parser p;
	private ProtoTask pt;
	private Storage s;
	private Calendar tomorrow;
	private Calendar endTime;
	private SimpleDateFormat sdf;
	private static final String IDFail = "No task with id:500 in TuckLife's to-do list!";
	private static final int failID = 500;
	
	@Before
	public void setUp() throws Exception {
		
		p = new Parser();
		
		tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DATE, 1);
		
		Calendar endTime = Calendar.getInstance();
		sdf = new SimpleDateFormat("dd/M/yy");
		Date d = sdf.parse("31/12/16");
		endTime.setTime(d);
		
		//ProtoTaskStubForStorage pt = new ProtoTaskStubForStorage(String command, String taskDesc, String location, String category, String additional,
				//int id, Calendar startDate, Calendar endDate);
		ptEvent = new ProtoTaskStubForStorage("add", "walk the dog", "park", "pet", "i love my dog", -1, tomorrow, endTime);
		ptDeadline = new ProtoTaskStubForStorage("add", "walk the cat", "park", "pet", "i love my cat", -1, null, tomorrow);
		ptEventEdit = new ProtoTaskStubForStorage("edit", "walk the cat", null, null, "i love my cat", 2, null, tomorrow);
		ptEventEditID = new ProtoTaskStubForStorage("edit", "walk the cat", null, null, "i love my cat", failID, null, tomorrow);
		ptEventDelete = new ProtoTaskStubForStorage("delete", null, null, null, null, 2, null, null);
		ptEventDeleteFail = new ProtoTaskStubForStorage("delete", null, null, null, null, failID, null, null);
		ptEventComplete = new ProtoTaskStubForStorage("complete", null, null, null, null, 2, null, null);
		ptEventCompleteFail = new ProtoTaskStubForStorage("complete", null, null, null, null, failID, null, null);
		ptDisplay = new ProtoTaskStubForStorage("display", null, null, null, null, -1, null, null);
		ptDisplayDone = new ProtoTaskStubForStorage("displaydone", null, null, null, null, -1, null, null);
		ptDisplayID = new ProtoTaskStubForStorage("display", null, null, null, null, 2, null, null);
	}

	@Test
	public void testAdd() {
		s = new Storage();
		pt = p.parse("add meeting");
		String deadline = String.format("{1. meeting} has been added to TuckLife\'s to-do list!", sdf.format(tomorrow.getTime()));
		String event = String.format("{2. walk the dog | start: %1$s end:%2$s | location: park | category: dog} has been added to TuckLife\'s to-do list!", sdf.format(tomorrow.getTime()), sdf.format(endTime));
		assertEquals("fail to add Deadline",s.parseCommand(ptDeadline),deadline);
		assertEquals("fail to add Event",s.parseCommand(ptEvent),event);
	}
	
	@Test
	public void testEdit() {
		s = new Storage();
		String eventEdit = String.format("{2. walk the cat | start: %1$s end:%2$s | location: park | category: cat} has been added to TuckLife\'s to-do list!", sdf.format(tomorrow.getTime()), sdf.format(endTime));
		s.parseCommand(ptDeadline);
		s.parseCommand(ptEvent);
		assertEquals("fail to handle id does not exist",s.parseCommand(ptEventEditFail),IDFail);
		assertEquals("fail to edit Event",s.parseCommand(ptEventEdit),eventEdit);
	}
	
	//do display, displaydone, displayID
	
	@Test
	public void testDisplay() {
		s = new Storage();
		String deadline = String.format("1. walk the cat | deadline: %1$s | location: park | category: pet", sdf.format(tomorrow.getTime()));
		String event = String.format("2. walk the dog | start: %1$s end:%2$s | location: park | category: dog", sdf.format(tomorrow.getTime()), sdf.format(endTime));
		String displayString = deadline + "\n" + event;
		s.parseCommand(ptDeadline);
		s.parseCommand(ptEvent);
		assertEquals("fail to display list of events",s.parseCommand(ptDisplay), displayString);
	}
	
	@Test
	public void testDisplayDone() {
		s = new Storage();
		String deadline = String.format("1. walk the cat | deadline: %1$s | location: park | category: pet", sdf.format(tomorrow.getTime()));
		String event = String.format("2. walk the dog | start: %1$s end:%2$s | location: park | category: dog", sdf.format(tomorrow.getTime()), sdf.format(endTime));
		String displayString = event;
		s.parseCommand(ptDeadline);
		s.parseCommand(ptEvent);
		s.parseCommand(ptComplete);
		assertEquals("fail to displaydone list of completed",s.parseCommand(ptDisplayDone), displayString);
	}
	
	@Test
	public void testDisplayID() {
		s = new Storage();
		String deadline = String.format("1. walk the cat | deadline: %1$s | location: park | category: pet", sdf.format(tomorrow.getTime()));
		String event = String.format("2. walk the dog | start: %1$s end:%2$s | location: park | category: dog | additional: i love my dog", sdf.format(tomorrow.getTime()), sdf.format(endTime));
		String displayString = event;
		s.parseCommand(ptDeadline);
		s.parseCommand(ptEvent);
		assertEquals("fail to displayID",s.parseCommand(ptDisplayID), displayString);
	}
	
	@Test
	public void testDelete() {
		s = new Storage();
		String eventDelete = String.format("{2. walk the dog | start: %1$s end:%2$s | location: park | category: dog} has been deleted from TuckLife\'s to-do list!", sdf.format(tomorrow.getTime()), sdf.format(endTime));
		s.parseCommand(ptDeadline);
		s.parseCommand(ptEvent);
		assertEquals("fail to handle id does not exist",s.parseCommand(ptEventDeleteFail),IDFail);
		assertEquals("fail to delete Event",s.parseCommand(ptEventDelete),eventDelete);
	}
	
	@Test
	public void testComplete() {
		s = new Storage();
		String eventComplete = String.format("{2. walk the dog | start: %1$s end:%2$s | location: park | category: dog} has been moved to TuckLife\'s done list!", sdf.format(tomorrow.getTime()), sdf.format(endTime));
		s.parseCommand(ptDeadline);
		s.parseCommand(ptEvent);
		assertEquals("fail to handle id does not exist",s.parseCommand(ptEventCompleteFail),IDFail);
		assertEquals("fail to delete Event",s.parseCommand(ptEventComplete),eventComplete);
	}

}
