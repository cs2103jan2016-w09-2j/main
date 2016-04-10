//@@author A0111101N
package tucklife.storage.internal;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;

import tucklife.UI.FlowController;
import tucklife.parser.Parser;
import tucklife.parser.String;
import tucklife.storage.Task;
import tucklife.storage.TaskList;

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
	
	@Test
	public void testSortLocation() {
		Storage.clear();
		
		String command1 = "add meeting @as4-mr3";
		String command2 = "add staff retreat @sentosa";
		String command3 = "add interview intern";
		
		String command4 = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		assertEquals("not sorted",fc.execute(command4),"3. interview in...\n1. meeting         | Location: as4-mr3\n2. staff retreat   | Location: sentosa\n");
		
		String command5 = "display +@";
		assertEquals("sorted ascending",fc.execute(command5),"1. meeting         | Location: as4-mr3\n2. staff retreat   | Location: sentosa\n3. interview in...\n");
		String command6 = "display -@";
		assertEquals("sorted descending",fc.execute(command6),"3. interview in...\n2. staff retreat   | Location: sentosa\n1. meeting         | Location: as4-mr3\n");
	}
	
	@Test
	public void testOverload() {
		Storage.clear();
		
		String command1 = "add meeting @meeting room 7 $16/05 +1400";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $16/05 +1300";
		
		String command4 = "setlimit 4";
		
		String command5 = "add 4th task $16/05 +2359";
		String command6 = "add overload $16/05 +2300";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);

		assertEquals("able to add",fc.execute(command5),"{4. 4th task | By: Mon, 16 May 2016 23:59} has been added to TuckLife's to-do list!");
		assertEquals("unable to add, hit limit",fc.execute(command6),"That day has been filled with 4 tasks! It hit the limit! You should reschedule the task to another day. Alternatively, you can either change the overload limit or turn it off.");
	}
	
	@Test
	public void testOverload2() {
		Storage.clear();
		
		String command1 = "add meeting @meeting room 7 $16/05 +1400";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $16/05 +1300";
		
		String command4 = "setlimit 4";
		
		String command5 = "add 4th task $16/05 +2359";
		String command6 = "add overload $17/05 +2300";
		String command7 = "edit 5 $16/05";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);

		assertEquals("able to add",fc.execute(command5),"{4. 4th task | By: Mon, 16 May 2016 23:59} has been added to TuckLife's to-do list!");
		assertEquals("able to add",fc.execute(command6),"{5. overload | By: Tue, 17 May 2016 23:00} has been added to TuckLife's to-do list!");
		assertEquals("unable to add, hit limit",fc.execute(command7),"That day has been filled with 4 tasks! It hit the limit! You should reschedule the task to another day. Alternatively, you can either change the overload limit or turn it off.");
	}
	
	@Test
	public void testOverload3() {
		Storage.clear();
		
		String command1 = "add meeting @meeting room 7 $16/05 +1400";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $16/05 +1300";
		
		String command4 = "setlimit 4";
		
		String command5 = "add 4th task $16/05 +2359";
		String command6 = "add redundant $17/05 +2300";
		String command7 = "add overload $16/05 +2300";
		
		String command8 = "add safety awareness week $09/05 to 16/05 #gg";
		String command9 = "add holiday $10/05 to 16/05 &to siam stupid stuff";
		String command10 = "add meeting $16/05 +2pm to 3pm";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		
		fc.execute(command9);

		assertEquals("able to add",fc.execute(command5),"{5. 4th task | By: Mon, 16 May 2016 23:59} has been added to TuckLife's to-do list!");
		assertEquals("able to add",fc.execute(command6),"{6. redundant | By: Tue, 17 May 2016 23:00} has been added to TuckLife's to-do list!");
		assertEquals("unable to add, hit limit",fc.execute(command7),"That day has been filled with 4 tasks! It hit the limit! You should reschedule the task to another day. Alternatively, you can either change the overload limit or turn it off.");
		assertEquals("able to add, hit limit",fc.execute(command8),"{8. safety awareness week | From: Mon, 09 May 2016 00:00 To: Mon, 16 May 2016 23:59 | Category: gg} has been added to TuckLife's to-do list!");
		assertEquals("unable to add, hit limit",fc.execute(command10),"{ 9. meeting | From: Mon, 16 May 2016 14:00 To: Mon, 16 May 2016 15:00} has been added to TuckLife's to-do list!");
		
	}
	
	@Test
	public void testSearch() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500 &meet at 4am";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16 &buy teammeet beer";
		String command7 = "add email boss $15/05 #siammeeting";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("added correctly", 7, td.size());
		
		String search = "display meeting";
		fc.execute(search);
		assertEquals("search correctly", "Exact Match\n5. client meeting | By: Mon, 09 May 2016 23:59\n1. meeting | Location: meeting room 7\n\nPartial Match\n7. email boss | By: Sun, 15 May 2016 23:59 | Category: siammeeting\n",fc.execute(search));
	}
	
	@Test
	public void testSearch2() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500 &meet at 4am";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16 &buy teammeet beer";
		String command7 = "add email boss $15/05 #siammeeting";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("added correctly", 7, td.size());
		
		String search = "display meet";
		fc.execute(search);
		assertEquals("search correctly", "Exact Match\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens | Additional: meet at 4am\n\nPartial Match\n6. payday | By: Thu, 05 May 2016 23:59 | Additional: buy teammeet beer\n5. client meeting | By: Mon, 09 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59 | Category: siammeeting\n1. meeting | Location: meeting room 7\n", fc.execute(search));
	}
	
	@Test
	public void testSearch3() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500 &Meet at 4am";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client Meeting $09/05/16";
		String command6 = "add payday $05/05/16 &buy teammeet beer";
		String command7 = "add email boss $15/05 #siammeeting";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("added correctly", 7, td.size());
		
		String search = "display meet";
		fc.execute(search);
		assertEquals("search correctly", "Exact Match\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens | Additional: Meet at 4am\n\nPartial Match\n6. payday | By: Thu, 05 May 2016 23:59 | Additional: buy teammeet beer\n5. client Meeting | By: Mon, 09 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59 | Category: siammeeting\n1. meeting | Location: meeting room 7\n", fc.execute(search));
	}
	
	@Test
	public void testUncomplete() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500 &Meet at 4am";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client Meeting $09/05/16";
		String command6 = "add payday $05/05/16 &buy teammeet beer";
		String command7 = "add email boss $15/05 #siammeeting";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		td = Storage.getTD();
		assertEquals("added correctly", 7, td.size());
		
		String complete = "complete 4";
		fc.execute(complete);
		
		td = Storage.getTD();
		assertEquals("uncompelete correctly", 6, td.size());
		
		String uncomplete = "uncomplete 4";
		fc.execute(uncomplete);
		td = Storage.getTD();
		assertEquals("uncompelete correctly", 7, td.size());
	}
	
	@Test
	public void testUndo() {
		Storage.clear();
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		
		String commandDisplay = "display ++";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		
		assertEquals("check display",fc.execute(commandDisplay),"2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		String command5 = "undo";	
		fc.execute(command5);
		assertEquals("check display change",fc.execute(commandDisplay),"2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n1. meeting         | Location: meeting room 7\n");
	}
	
	@Test
	public void testUndo2() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("added correctly", 7, td.size());
		
		String undo = "undo";
		fc.execute(undo);
		td = Storage.getTD();
		assertEquals("able to undo once", 6, td.size());
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(undo);
		td = Storage.getTD();
		assertEquals("able to handle non-consecutive undos", 8, td.size());
	}
	
	@Test
	public void testMultipleUndo() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("added correctly", 7, td.size());
		
		String undo = "undo";
		fc.execute(undo);
		td = Storage.getTD();
		assertEquals("able to undo once", 6, td.size());
		fc.execute(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 5, td.size());
		fc.execute(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 4, td.size());
	}
	
	@Test
	public void testUndoForQueue() {
		Storage.clear();
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		
		assertEquals("check display",fc.execute(commandDisplay),"2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		String command4 = "queue 1";
		fc.execute(command4);
		assertEquals("task is queued",fc.execute(commandDisplay),"Queue:\n1. meeting         | Location: meeting room 7\n\nOther Tasks:\n2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n");
		String command5 = "undo";	
		fc.execute(command5);
		assertEquals("task is unqueued",fc.execute(commandDisplay),"2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
	}
	
	@Test
	public void testRedo() {
		Storage.clear();
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		
		String commandDisplay = "display ++";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);

		assertEquals("check display",fc.execute(commandDisplay),"2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		
		String commandDO = "undo";	
		fc.execute(commandDO);
		assertEquals("check display change",fc.execute(commandDisplay),"2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n1. meeting         | Location: meeting room 7\n");
		
		commandDO = "redo";
		fc.execute(commandDO);
		assertEquals("check display",fc.execute(commandDisplay),"2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		
	}
	
	@Test
	public void testMultipleRedo() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("added correctly", 7, td.size());
		
		String undo = "undo";
		fc.execute(undo);
		td = Storage.getTD();
		assertEquals("able to undo once", 6, td.size());
		fc.execute(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 5, td.size());
		fc.execute(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 4, td.size());
		String redo = "redo";
		fc.execute(redo);
		td = Storage.getTD();
		assertEquals("able to undo once", 5, td.size());
		fc.execute(redo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 6, td.size());
		fc.execute(redo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 7, td.size());
	}
	
	@Test
	public void testRedo2() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("added correctly", 7, td.size());
		
		String undo = "undo";
		fc.execute(undo);
		td = Storage.getTD();
		assertEquals("able to undo once", 6, td.size());
		fc.execute(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 5, td.size());
		fc.execute(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 4, td.size());
		fc.execute(command5);
		String redo = "redo";
		assertEquals("previous command not undo", fc.execute(redo), "There is no previous action to redo!");
	}
	
	@Test
	public void testQueue1() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("check normal display without queue",fc.execute(commandDisplay),"4. financial re... | By: Sun, 01 May 2016 23:59\n6. payday          | By: Thu, 05 May 2016 23:59\n5. client meeting  | By: Mon, 09 May 2016 23:59\n7. email boss      | By: Sun, 15 May 2016 23:59\n2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		
		String command8 = "queue 4";
		String command9 = "queue 6";
		String command10 = "queue 5";
		fc.execute(command8);
		fc.execute(command9);
		fc.execute(command10);
		t = td.get(5);
		assertEquals("task is added to back of queue", 3, t.getQueueID());
	}
	
	@Test
	public void testQueue11() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("check normal display without queue",fc.execute(commandDisplay),"4. financial re... | By: Sun, 01 May 2016 23:59\n6. payday          | By: Thu, 05 May 2016 23:59\n5. client meeting  | By: Mon, 09 May 2016 23:59\n7. email boss      | By: Sun, 15 May 2016 23:59\n2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		
		String command8 = "queue 4";
		String command9 = "queue 6";
		String command10 = "queue 5";
		String command11 = "queue 7 1";
		String command12 = "queue 2 4";
		String command13 = "queue 1 100";
		fc.execute(command8);
		t = td.get(4);
		assertEquals("queue id is updated", 1, t.getQueueID());
		fc.execute(command9);
		fc.execute(command10);
		fc.execute(command11);
		assertEquals("queue id is updated", 2, t.getQueueID());
	}
	
	@Test
	public void testQueue2() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("check normal display without queue",fc.execute(commandDisplay),"4. financial re... | By: Sun, 01 May 2016 23:59\n6. payday          | By: Thu, 05 May 2016 23:59\n5. client meeting  | By: Mon, 09 May 2016 23:59\n7. email boss      | By: Sun, 15 May 2016 23:59\n2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		
		String command8 = "queue 4";
		String command9 = "queue 6";
		String command10 = "queue 5";
		fc.execute(command8);
		fc.execute(command9);
		fc.execute(command10);
		t = td.get(5);
		assertEquals("task is added to back of queue", 3, t.getQueueID());
		String command11 = "queue 7 1";
		fc.execute(command11);
		t = td.get(7);
		assertEquals("task is added to head of queue", 1, t.getQueueID());
	}
	
	@Test
	public void testQueue3() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("check normal display without queue",fc.execute(commandDisplay),"4. financial re... | By: Sun, 01 May 2016 23:59\n6. payday          | By: Thu, 05 May 2016 23:59\n5. client meeting  | By: Mon, 09 May 2016 23:59\n7. email boss      | By: Sun, 15 May 2016 23:59\n2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		
		String command8 = "queue 4";
		String command9 = "queue 6";
		String command10 = "queue 5";
		String command11 = "queue 7 1";
		String command12 = "queue 2 4";
		fc.execute(command8);
		fc.execute(command9);
		fc.execute(command10);
		fc.execute(command11);
		fc.execute(command12);
		t = td.get(2);
		assertEquals("task is added to middle of queue", 4, t.getQueueID());
	}
	
	@Test
	public void testQueue4() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("check normal display without queue",fc.execute(commandDisplay),"4. financial re... | By: Sun, 01 May 2016 23:59\n6. payday          | By: Thu, 05 May 2016 23:59\n5. client meeting  | By: Mon, 09 May 2016 23:59\n7. email boss      | By: Sun, 15 May 2016 23:59\n2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		
		String command8 = "queue 4";
		String command9 = "queue 6";
		String command10 = "queue 5";
		String command11 = "queue 7 1";
		String command12 = "queue 2 4";
		String command13 = "queue 1 100";
		fc.execute(command8);
		fc.execute(command9);
		fc.execute(command10);
		fc.execute(command11);
		fc.execute(command12);
		fc.execute(command13);
		t = td.get(1);
		assertEquals("task is added to end of queue when pos > max queue", 6, t.getQueueID());
	}
	
	@Test
	public void testQueue5() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("check normal display without queue",fc.execute(commandDisplay),"4. financial re... | By: Sun, 01 May 2016 23:59\n6. payday          | By: Thu, 05 May 2016 23:59\n5. client meeting  | By: Mon, 09 May 2016 23:59\n7. email boss      | By: Sun, 15 May 2016 23:59\n2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		
		String command8 = "queue 4";
		String command9 = "queue 6";
		String command10 = "queue 5";
		String command11 = "queue 7 1";
		String command12 = "queue 2 4";
		String command13 = "queue 1 100";
		fc.execute(command8);
		fc.execute(command9);
		fc.execute(command10);
		fc.execute(command11);
		fc.execute(command12);
		fc.execute(command13);
		String command14 = "delete 2";
		fc.execute(command14);
		t = td.get(5);
		assertEquals("queue id is update after deletion", 4, t.getQueueID());
	}
	
	@Test
	public void testQueue6() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500)";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("check normal display without queue",fc.execute(commandDisplay),"4. financial re... | By: Sun, 01 May 2016 23:59\n6. payday          | By: Thu, 05 May 2016 23:59\n5. client meeting  | By: Mon, 09 May 2016 23:59\n7. email boss      | By: Sun, 15 May 2016 23:59\n2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		
		String command8 = "queue 4";
		String command9 = "queue 6";
		String command10 = "queue 5";
		String command11 = "queue 7 1";
		String command12 = "queue 2 4";
		String command13 = "queue 1 100";
		fc.execute(command8);
		fc.execute(command9);
		fc.execute(command10);
		fc.execute(command11);
		fc.execute(command12);
		fc.execute(command13);
		String command14 = "complete 2";
		fc.execute(command14);
		t = td.get(5);
		assertEquals("queue id is updated after completion", 4, t.getQueueID());
	}
	
	@Test
	public void testQueue7() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("check normal display without queue",fc.execute(commandDisplay),"4. financial re... | By: Sun, 01 May 2016 23:59\n6. payday          | By: Thu, 05 May 2016 23:59\n5. client meeting  | By: Mon, 09 May 2016 23:59\n7. email boss      | By: Sun, 15 May 2016 23:59\n2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		
		String command8 = "queue 4";
		String command9 = "queue 6";
		String command10 = "queue 5";
		String command11 = "queue 7 1";
		String command12 = "queue 2 4";
		String command13 = "queue 1 100";
		fc.execute(command8);
		fc.execute(command9);
		fc.execute(command10);
		fc.execute(command11);
		fc.execute(command12);
		fc.execute(command13);
		t = td.get(2);
		String command14 = "complete 2";
		fc.execute(command14);
		assertEquals("queue id of completed is updated to default", -1, t.getQueueID());
	}
	
	@Test
	public void testQueueDisplay() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("check normal display without queue",fc.execute(commandDisplay),"4. financial re... | By: Sun, 01 May 2016 23:59\n6. payday          | By: Thu, 05 May 2016 23:59\n5. client meeting  | By: Mon, 09 May 2016 23:59\n7. email boss      | By: Sun, 15 May 2016 23:59\n2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		
		String command8 = "queue 4";
		String command9 = "queue 6";
		String command10 = "queue 5";
		String command11 = "queue 7 1";
		String command12 = "queue 2 4";
		String command13 = "queue 1 100";
		fc.execute(command8);
		fc.execute(command9);
		fc.execute(command10);
		fc.execute(command11);
		fc.execute(command12);
		fc.execute(command13);
		
		assertEquals("check normal display without queue","Queue:\n7. email boss      | By: Sun, 15 May 2016 23:59\n4. financial re... | By: Sun, 01 May 2016 23:59\n6. payday          | By: Thu, 05 May 2016 23:59\n2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n5. client meeting  | By: Mon, 09 May 2016 23:59\n1. meeting         | Location: meeting room 7\n\nOther Tasks:\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n",fc.execute(commandDisplay));
	}
	
	
	@Test
	public void testQueueWithUndo() {
		Storage.clear();
		
		TaskList td = Storage.getTD();
		Task t;
		
		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";
		String command4 = "add financial report $01/05/16";
		String command5 = "add client meeting $09/05/16";
		String command6 = "add payday $05/05/16";
		String command7 = "add email boss $15/05";
		
		String commandDisplay = "display";
		
		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		fc.execute(command4);
		fc.execute(command5);
		fc.execute(command6);
		fc.execute(command7);
		
		assertEquals("check normal display without queue",fc.execute(commandDisplay),"4. financial re... | By: Sun, 01 May 2016 23:59\n6. payday          | By: Thu, 05 May 2016 23:59\n5. client meeting  | By: Mon, 09 May 2016 23:59\n7. email boss      | By: Sun, 15 May 2016 23:59\n2. staff retreat   | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview in... | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting         | Location: meeting room 7\n");
		
		String command8 = "queue 4";
		String command9 = "queue 6";
		String command10 = "queue 5";
		String command11 = "queue 7 1";
		String command12 = "queue 2 2";
		String commandUndo = "undo";
		fc.execute(command8);
		fc.execute(command9);
		fc.execute(command10);
		fc.execute(command11);
		fc.execute(command12);
		t = td.get(4);
		assertEquals("correct id", 3, t.getQueueID());
		fc.execute(commandUndo);
		td = Storage.getTD();
		t = td.get(6);
		assertEquals("correct id", 3, t.getQueueID());
	}

	
}
