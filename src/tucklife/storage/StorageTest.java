//@@author A0111101N
package tucklife.storage;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;

import tucklife.UI.FlowController;
import tucklife.parser.Parser;
import tucklife.parser.ProtoTask;

public class StorageTest {
	
	Parser p;
	FlowController fc;
	
	@Before
	public void setUp() throws Exception {
		p = new Parser();
		fc = new FlowController();
		Hashtable<String,String> ht = new Hashtable<String,String>();
		p.loadCommands(ht);
	}
	ArrayList<ArrayList<TaskList>> al = new ArrayList<ArrayList<TaskList>>();
	@Test
	public void testal() {
		assertEquals(al.size(), 0);
	}
	
	@Test
	public void testSortLocation() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("add meeting @as4-mr3");
		ProtoTask pt2 = p.parse("add staff retreat @sentosa");
		ProtoTask pt3 = p.parse("add interview intern");
		
		ProtoTask pt4 = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		assertEquals("not sorted",fc.parseCommand(pt4),"3. interview intern\n1. meeting | Location: as4-mr3\n2. staff retreat | Location: sentosa\n");
		
		ProtoTask pt5 = p.parse("display +@");
		assertEquals("sorted ascending",fc.parseCommand(pt5),"1. meeting | Location: as4-mr3\n2. staff retreat | Location: sentosa\n3. interview intern\n");
		ProtoTask pt6 = p.parse("display -@");
		assertEquals("sorted descending",fc.parseCommand(pt6),"3. interview intern\n2. staff retreat | Location: sentosa\n1. meeting | Location: as4-mr3\n");
	}
	
	@Test
	public void testOverload() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7 $16/05 +1400");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $16/05 +1300");
		
		ProtoTask pt4 = p.parse("setlimit 4");
		
		ProtoTask pt5 = p.parse("add 4th task $16/05 +2359");
		ProtoTask pt6 = p.parse("add overload $16/05 +2300");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);

		assertEquals("able to add",fc.parseCommand(pt5),"{4. 4th task | By: Mon, 16 May 2016 23:59} has been added to TuckLife's to-do list!");
		assertEquals("unable to add, hit limit",fc.parseCommand(pt6),"That day has been filled with 4 tasks! It hit the limit! You should reschedule the task to another day. Alternatively, you can either change the overload limit or turn it off.");
	}
	
	@Test
	public void testOverload2() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7 $16/05 +1400");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $16/05 +1300");
		
		ProtoTask pt4 = p.parse("setlimit 4");
		
		ProtoTask pt5 = p.parse("add 4th task $16/05 +2359");
		ProtoTask pt6 = p.parse("add overload $17/05 +2300");
		ProtoTask pt7 = p.parse("edit 5 $16/05");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);

		assertEquals("able to add",fc.parseCommand(pt5),"{4. 4th task | By: Mon, 16 May 2016 23:59} has been added to TuckLife's to-do list!");
		assertEquals("able to add",fc.parseCommand(pt6),"{5. overload | By: Tue, 17 May 2016 23:00} has been added to TuckLife's to-do list!");
		assertEquals("unable to add, hit limit",fc.parseCommand(pt7),"That day has been filled with 4 tasks! It hit the limit! You should reschedule the task to another day. Alternatively, you can either change the overload limit or turn it off.");
	}
	
	@Test
	public void testOverload3() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7 $16/05 +1400");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $16/05 +1300");
		
		ProtoTask pt4 = p.parse("setlimit 4");
		
		ProtoTask pt5 = p.parse("add 4th task $16/05 +2359");
		ProtoTask pt6 = p.parse("add redundant $17/05 +2300");
		ProtoTask pt7 = p.parse("add overload $16/05 +2300");
		
		ProtoTask pt8 = p.parse("add safety awareness week $09/05 to 16/05 #gg");
		ProtoTask pt9 = p.parse("add holiday $10/05 to 16/05 &to siam stupid stuff");
		ProtoTask pt10 = p.parse("add meeting $16/05 +2pm to 3pm");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		
		fc.parseCommand(pt9);

		assertEquals("able to add",fc.parseCommand(pt5),"{5. 4th task | By: Mon, 16 May 2016 23:59} has been added to TuckLife's to-do list!");
		assertEquals("able to add",fc.parseCommand(pt6),"{6. redundant | By: Tue, 17 May 2016 23:00} has been added to TuckLife's to-do list!");
		assertEquals("unable to add, hit limit",fc.parseCommand(pt7),"That day has been filled with 4 tasks! It hit the limit! You should reschedule the task to another day. Alternatively, you can either change the overload limit or turn it off.");
		assertEquals("able to add, hit limit",fc.parseCommand(pt8),"{8. safety awareness week | From: Mon, 9 May 2016 00:00 To: Mon, 16 May 2016 23:59 | Category: gg} has been added to TuckLife's to-do list!");
		assertEquals("unable to add, hit limit",fc.parseCommand(pt10),"{9. meeting | From: Mon, 16 May 2016 14:00 To: Mon, 16 May 2016 15:00} has been added to TuckLife's to-do list!");
		
	}
	
	@Test
	public void testSearch() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500 &meet at 4am");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16 &buy teammeet beer");
		ProtoTask pt7 = p.parse("add email boss $15/05 #siammeeting");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("added correctly", 7, td.size());
		
		ProtoTask search = p.parse("display meeting");
		fc.parseCommand(search);
		assertEquals("search correctly", "Exact Match\n5. client meeting | By: Mon, 9 May 2016 23:59\n1. meeting | Location: meeting room 7\n\nPartial Match\n7. email boss | By: Sun, 15 May 2016 23:59 | Category: siammeeting\n",fc.parseCommand(search));
	}
	
	@Test
	public void testSearch2() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500 &meet at 4am");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16 &buy teammeet beer");
		ProtoTask pt7 = p.parse("add email boss $15/05 #siammeeting");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("added correctly", 7, td.size());
		
		ProtoTask search = p.parse("display meet");
		fc.parseCommand(search);
		assertEquals("search correctly", "Exact Match\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens | Additional: meet at 4am\n\nPartial Match\n6. payday | By: Thu, 5 May 2016 23:59 | Additional: buy teammeet beer\n5. client meeting | By: Mon, 9 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59 | Category: siammeeting\n1. meeting | Location: meeting room 7\n", fc.parseCommand(search));
	}
	
	@Test
	public void testSearch3() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500 &Meet at 4am");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client Meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16 &buy teammeet beer");
		ProtoTask pt7 = p.parse("add email boss $15/05 #siammeeting");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("added correctly", 7, td.size());
		
		ProtoTask search = p.parse("display meet");
		fc.parseCommand(search);
		assertEquals("search correctly", "Exact Match\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens | Additional: Meet at 4am\n\nPartial Match\n6. payday | By: Thu, 5 May 2016 23:59 | Additional: buy teammeet beer\n5. client Meeting | By: Mon, 9 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59 | Category: siammeeting\n1. meeting | Location: meeting room 7\n", fc.parseCommand(search));
	}
	
	@Test
	public void testUncomplete() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500 &Meet at 4am");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client Meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16 &buy teammeet beer");
		ProtoTask pt7 = p.parse("add email boss $15/05 #siammeeting");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		td = Storage.getTD();
		assertEquals("added correctly", 7, td.size());
		
		ProtoTask complete = p.parse("complete 4");
		fc.parseCommand(complete);
		
		td = Storage.getTD();
		assertEquals("uncompelete correctly", 6, td.size());
		
		ProtoTask uncomplete = p.parse("uncomplete 4");
		fc.parseCommand(uncomplete);
		td = Storage.getTD();
		assertEquals("uncompelete correctly", 7, td.size());
	}
	
	@Test
	public void testUndo() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		
		ProtoTask ptDisplay = p.parse("display ++");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		
		assertEquals("check display",fc.parseCommand(ptDisplay),"2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		ProtoTask pt5 = p.parse("undo");	
		fc.parseCommand(pt5);
		assertEquals("check display change",fc.parseCommand(ptDisplay),"2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n1. meeting | Location: meeting room 7\n");
	}
	
	@Test
	public void testUndo2() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("added correctly", 7, td.size());
		
		ProtoTask undo = p.parse("undo");
		fc.parseCommand(undo);
		td = Storage.getTD();
		assertEquals("able to undo once", 6, td.size());
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(undo);
		td = Storage.getTD();
		assertEquals("able to handle non-consecutive undos", 8, td.size());
	}
	
	@Test
	public void testMultipleUndo() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("added correctly", 7, td.size());
		
		ProtoTask undo = p.parse("undo");
		fc.parseCommand(undo);
		td = Storage.getTD();
		assertEquals("able to undo once", 6, td.size());
		fc.parseCommand(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 5, td.size());
		fc.parseCommand(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 4, td.size());
	}
	
	@Test
	public void testUndoForQueue() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		
		assertEquals("check display",fc.parseCommand(ptDisplay),"2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		ProtoTask pt4 = p.parse("queue 1");
		fc.parseCommand(pt4);
		assertEquals("task is queued",fc.parseCommand(ptDisplay),"Queue:\n1. meeting | Location: meeting room 7\n\nOther Tasks:\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n");
		ProtoTask pt5 = p.parse("undo");	
		fc.parseCommand(pt5);
		assertEquals("task is unqueued",fc.parseCommand(ptDisplay),"2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
	}
	
	@Test
	public void testRedo() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		
		ProtoTask ptDisplay = p.parse("display ++");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);

		assertEquals("check display",fc.parseCommand(ptDisplay),"2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
		ProtoTask ptDO = p.parse("undo");	
		fc.parseCommand(ptDO);
		assertEquals("check display change",fc.parseCommand(ptDisplay),"2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n1. meeting | Location: meeting room 7\n");
		
		ptDO = p.parse("redo");
		fc.parseCommand(ptDO);
		assertEquals("check display",fc.parseCommand(ptDisplay),"2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
	}
	
	@Test
	public void testMultipleRedo() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("added correctly", 7, td.size());
		
		ProtoTask undo = p.parse("undo");
		fc.parseCommand(undo);
		td = Storage.getTD();
		assertEquals("able to undo once", 6, td.size());
		fc.parseCommand(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 5, td.size());
		fc.parseCommand(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 4, td.size());
		ProtoTask redo = p.parse("redo");
		fc.parseCommand(redo);
		td = Storage.getTD();
		assertEquals("able to undo once", 5, td.size());
		fc.parseCommand(redo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 6, td.size());
		fc.parseCommand(redo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 7, td.size());
	}
	
	@Test
	public void testRedo2() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("added correctly", 7, td.size());
		
		ProtoTask undo = p.parse("undo");
		fc.parseCommand(undo);
		td = Storage.getTD();
		assertEquals("able to undo once", 6, td.size());
		fc.parseCommand(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 5, td.size());
		fc.parseCommand(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 4, td.size());
		fc.parseCommand(pt5);
		ProtoTask redo = p.parse("redo");
		assertEquals("previous command not undo", fc.parseCommand(redo), "There is no previous action to redo!");
	}
	
	@Test
	public void testQueue1() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("check normal display without queue",fc.parseCommand(ptDisplay),"4. financial report | By: Sun, 1 May 2016 23:59\n6. payday | By: Thu, 5 May 2016 23:59\n5. client meeting | By: Mon, 9 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
		ProtoTask pt8 = p.parse("queue 4");
		ProtoTask pt9 = p.parse("queue 6");
		ProtoTask pt10 = p.parse("queue 5");
		fc.parseCommand(pt8);
		fc.parseCommand(pt9);
		fc.parseCommand(pt10);
		t = td.get(5);
		assertEquals("task is added to back of queue", 3, t.getQueueID());
	}
	
	@Test
	public void testQueue11() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("check normal display without queue",fc.parseCommand(ptDisplay),"4. financial report | By: Sun, 1 May 2016 23:59\n6. payday | By: Thu, 5 May 2016 23:59\n5. client meeting | By: Mon, 9 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
		ProtoTask pt8 = p.parse("queue 4");
		ProtoTask pt9 = p.parse("queue 6");
		ProtoTask pt10 = p.parse("queue 5");
		ProtoTask pt11 = p.parse("queue 7 1");
		ProtoTask pt12 = p.parse("queue 2 4");
		ProtoTask pt13 = p.parse("queue 1 100");
		fc.parseCommand(pt8);
		t = td.get(4);
		assertEquals("queue id is updated", 1, t.getQueueID());
		fc.parseCommand(pt9);
		fc.parseCommand(pt10);
		fc.parseCommand(pt11);
		assertEquals("queue id is updated", 2, t.getQueueID());
	}
	
	@Test
	public void testQueue2() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("check normal display without queue",fc.parseCommand(ptDisplay),"4. financial report | By: Sun, 1 May 2016 23:59\n6. payday | By: Thu, 5 May 2016 23:59\n5. client meeting | By: Mon, 9 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
		ProtoTask pt8 = p.parse("queue 4");
		ProtoTask pt9 = p.parse("queue 6");
		ProtoTask pt10 = p.parse("queue 5");
		fc.parseCommand(pt8);
		fc.parseCommand(pt9);
		fc.parseCommand(pt10);
		t = td.get(5);
		assertEquals("task is added to back of queue", 3, t.getQueueID());
		ProtoTask pt11 = p.parse("queue 7 1");
		fc.parseCommand(pt11);
		t = td.get(7);
		assertEquals("task is added to head of queue", 1, t.getQueueID());
	}
	
	@Test
	public void testQueue3() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("check normal display without queue",fc.parseCommand(ptDisplay),"4. financial report | By: Sun, 1 May 2016 23:59\n6. payday | By: Thu, 5 May 2016 23:59\n5. client meeting | By: Mon, 9 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
		ProtoTask pt8 = p.parse("queue 4");
		ProtoTask pt9 = p.parse("queue 6");
		ProtoTask pt10 = p.parse("queue 5");
		ProtoTask pt11 = p.parse("queue 7 1");
		ProtoTask pt12 = p.parse("queue 2 4");
		fc.parseCommand(pt8);
		fc.parseCommand(pt9);
		fc.parseCommand(pt10);
		fc.parseCommand(pt11);
		fc.parseCommand(pt12);
		t = td.get(2);
		assertEquals("task is added to middle of queue", 4, t.getQueueID());
	}
	
	@Test
	public void testQueue4() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("check normal display without queue",fc.parseCommand(ptDisplay),"4. financial report | By: Sun, 1 May 2016 23:59\n6. payday | By: Thu, 5 May 2016 23:59\n5. client meeting | By: Mon, 9 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
		ProtoTask pt8 = p.parse("queue 4");
		ProtoTask pt9 = p.parse("queue 6");
		ProtoTask pt10 = p.parse("queue 5");
		ProtoTask pt11 = p.parse("queue 7 1");
		ProtoTask pt12 = p.parse("queue 2 4");
		ProtoTask pt13 = p.parse("queue 1 100");
		fc.parseCommand(pt8);
		fc.parseCommand(pt9);
		fc.parseCommand(pt10);
		fc.parseCommand(pt11);
		fc.parseCommand(pt12);
		fc.parseCommand(pt13);
		t = td.get(1);
		assertEquals("task is added to end of queue when pos > max queue", 6, t.getQueueID());
	}
	
	@Test
	public void testQueue5() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("check normal display without queue",fc.parseCommand(ptDisplay),"4. financial report | By: Sun, 1 May 2016 23:59\n6. payday | By: Thu, 5 May 2016 23:59\n5. client meeting | By: Mon, 9 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
		ProtoTask pt8 = p.parse("queue 4");
		ProtoTask pt9 = p.parse("queue 6");
		ProtoTask pt10 = p.parse("queue 5");
		ProtoTask pt11 = p.parse("queue 7 1");
		ProtoTask pt12 = p.parse("queue 2 4");
		ProtoTask pt13 = p.parse("queue 1 100");
		fc.parseCommand(pt8);
		fc.parseCommand(pt9);
		fc.parseCommand(pt10);
		fc.parseCommand(pt11);
		fc.parseCommand(pt12);
		fc.parseCommand(pt13);
		ProtoTask pt14 = p.parse("delete 2");
		fc.parseCommand(pt14);
		t = td.get(5);
		assertEquals("queue id is update after deletion", 4, t.getQueueID());
	}
	
	@Test
	public void testQueue6() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("check normal display without queue",fc.parseCommand(ptDisplay),"4. financial report | By: Sun, 1 May 2016 23:59\n6. payday | By: Thu, 5 May 2016 23:59\n5. client meeting | By: Mon, 9 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
		ProtoTask pt8 = p.parse("queue 4");
		ProtoTask pt9 = p.parse("queue 6");
		ProtoTask pt10 = p.parse("queue 5");
		ProtoTask pt11 = p.parse("queue 7 1");
		ProtoTask pt12 = p.parse("queue 2 4");
		ProtoTask pt13 = p.parse("queue 1 100");
		fc.parseCommand(pt8);
		fc.parseCommand(pt9);
		fc.parseCommand(pt10);
		fc.parseCommand(pt11);
		fc.parseCommand(pt12);
		fc.parseCommand(pt13);
		ProtoTask pt14 = p.parse("complete 2");
		fc.parseCommand(pt14);
		t = td.get(5);
		assertEquals("queue id is updated after completion", 4, t.getQueueID());
	}
	
	@Test
	public void testQueue7() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("check normal display without queue",fc.parseCommand(ptDisplay),"4. financial report | By: Sun, 1 May 2016 23:59\n6. payday | By: Thu, 5 May 2016 23:59\n5. client meeting | By: Mon, 9 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
		ProtoTask pt8 = p.parse("queue 4");
		ProtoTask pt9 = p.parse("queue 6");
		ProtoTask pt10 = p.parse("queue 5");
		ProtoTask pt11 = p.parse("queue 7 1");
		ProtoTask pt12 = p.parse("queue 2 4");
		ProtoTask pt13 = p.parse("queue 1 100");
		fc.parseCommand(pt8);
		fc.parseCommand(pt9);
		fc.parseCommand(pt10);
		fc.parseCommand(pt11);
		fc.parseCommand(pt12);
		fc.parseCommand(pt13);
		t = td.get(2);
		ProtoTask pt14 = p.parse("complete 2");
		fc.parseCommand(pt14);
		assertEquals("queue id of completed is updated to default", -1, t.getQueueID());
	}
	
	@Test
	public void testQueueDisplay() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("check normal display without queue",fc.parseCommand(ptDisplay),"4. financial report | By: Sun, 1 May 2016 23:59\n6. payday | By: Thu, 5 May 2016 23:59\n5. client meeting | By: Mon, 9 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
		ProtoTask pt8 = p.parse("queue 4");
		ProtoTask pt9 = p.parse("queue 6");
		ProtoTask pt10 = p.parse("queue 5");
		ProtoTask pt11 = p.parse("queue 7 1");
		ProtoTask pt12 = p.parse("queue 2 4");
		ProtoTask pt13 = p.parse("queue 1 100");
		fc.parseCommand(pt8);
		fc.parseCommand(pt9);
		fc.parseCommand(pt10);
		fc.parseCommand(pt11);
		fc.parseCommand(pt12);
		fc.parseCommand(pt13);
		
		assertEquals("check normal display without queue","Queue:\n7. email boss | By: Sun, 15 May 2016 23:59\n4. financial report | By: Sun, 1 May 2016 23:59\n6. payday | By: Thu, 5 May 2016 23:59\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n5. client meeting | By: Mon, 9 May 2016 23:59\n1. meeting | Location: meeting room 7\n\nOther Tasks:\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n",fc.parseCommand(ptDisplay));
	}
	
	
	@Test
	public void testQueueWithUndo() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		fc.parseCommand(pt1);
		fc.parseCommand(pt2);
		fc.parseCommand(pt3);
		fc.parseCommand(pt4);
		fc.parseCommand(pt5);
		fc.parseCommand(pt6);
		fc.parseCommand(pt7);
		
		assertEquals("check normal display without queue",fc.parseCommand(ptDisplay),"4. financial report | By: Sun, 1 May 2016 23:59\n6. payday | By: Thu, 5 May 2016 23:59\n5. client meeting | By: Mon, 9 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
		ProtoTask pt8 = p.parse("queue 4");
		ProtoTask pt9 = p.parse("queue 6");
		ProtoTask pt10 = p.parse("queue 5");
		ProtoTask pt11 = p.parse("queue 7 1");
		ProtoTask pt12 = p.parse("queue 2 2");
		ProtoTask ptUndo = p.parse("undo");
		fc.parseCommand(pt8);
		fc.parseCommand(pt9);
		fc.parseCommand(pt10);
		fc.parseCommand(pt11);
		fc.parseCommand(pt12);
		t = td.get(4);
		assertEquals("correct id", 3, t.getQueueID());
		fc.parseCommand(ptUndo);
		td = Storage.getTD();
		t = td.get(6);
		assertEquals("correct id", 3, t.getQueueID());
	}

	
}
