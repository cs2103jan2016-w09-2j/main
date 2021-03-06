//@@author A0111101N
package tucklife.storage.internal;

import static org.junit.Assert.*;

import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;

import tucklife.UI.FlowController;
import tucklife.parser.Parser;
import tucklife.storage.Task;
import tucklife.storage.TaskList;

public class StorageTest {

	Parser p;
	FlowController fc;
	Storage s;
	String status;

	@Before
	public void setUp() throws Exception {
		p = new Parser();
		fc = new FlowController();
		Hashtable<String, String> ht = new Hashtable<String, String>();
		p.loadCommands(ht);
	}

	@Test
	public void testSortLocation() {
		Storage.clear();
		s = FlowController.getStorage();

		String command1 = "add meeting @as4-mr3";
		String command2 = "add staff retreat @sentosa";
		String command3 = "add interview intern";

		String command4 = "display";

		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);

		status = s.getStatus() + "\n\n";
		assertEquals("not sorted", status + "3. interview in...\n1. meeting        \n2. staff retreat  \n",
				fc.execute(command4));

		String command5 = "display +@";

		status = s.getStatus() + "\n\n";
		assertEquals("sorted ascending", status + "1. meeting        \n2. staff retreat  \n3. interview in...\n",
				fc.execute(command5));

		String command6 = "display -@";

		status = s.getStatus() + "\n\n";
		assertEquals("sorted descending", status + "3. interview in...\n2. staff retreat  \n1. meeting        \n",
				fc.execute(command6));
	}

	@Test
	public void testOverloadAdd() {
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
		fc.execute(command5);

		TaskList td = Storage.getTD();
		assertEquals("able to add", 4, td.size());

		fc.execute(command6);
		td = Storage.getTD();
		assertEquals("unable to add", 4, td.size());
	}

	@Test
	public void testOverloadEdit() {
		Storage.clear();
		s = FlowController.getStorage();

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
		fc.execute(command5);

		TaskList td = Storage.getTD();
		assertEquals("able to add", 4, td.size());

		fc.execute(command6);
		td = Storage.getTD();
		assertEquals("able to add", 5, td.size());

		fc.execute(command7);
		td = Storage.getTD();
		assertEquals("unable to add", 5, td.size());

	}

	@Test
	public void testOverloadMultipleAdds() {
		Storage.clear();
		s = FlowController.getStorage();

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
		fc.execute(command5);

		TaskList td = Storage.getTD();
		assertEquals("able to add", 5, td.size());

		fc.execute(command6);
		td = Storage.getTD();
		assertEquals("able to add", 6, td.size());

		fc.execute(command7);
		td = Storage.getTD();
		assertEquals("unable to add", 6, td.size());

		fc.execute(command8);
		td = Storage.getTD();
		assertEquals("able to add", 7, td.size());

		fc.execute(command10);
		td = Storage.getTD();
		assertEquals("able to add", 8, td.size());
	}

	@Test
	public void testSearch() {
		Storage.clear();
		s = FlowController.getStorage();

		TaskList td = Storage.getTD();

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
		status = s.getStatus() + "\n\n";
		assertEquals("search correctly",
				status + "Exact Match"
						+ "\n5. client meeting | By: Mon, 09 May 2016 23:59"
						+ "\n1. meeting | Location: meeting room 7"
						+ "\n\nPartial Match"
						+ "\n7. email boss | By: Sun, 15 May 2016 23:59 | Category: siammeeting\n",
				fc.execute(search));
	}

	@Test
	public void testSearchPartial() {
		Storage.clear();
		s = FlowController.getStorage();

		TaskList td = Storage.getTD();

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
		status = s.getStatus() + "\n\n";
		assertEquals("search correctly",
				status + "Exact Match"
						+ "\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens | Additional: meet at 4am"
						+ "\n\nPartial Match"
						+ "\n6. payday | By: Thu, 05 May 2016 23:59 | Additional: buy teammeet beer"
						+ "\n5. client meeting | By: Mon, 09 May 2016 23:59"
						+ "\n7. email boss | By: Sun, 15 May 2016 23:59 | Category: siammeeting"
						+ "\n1. meeting | Location: meeting room 7\n",
				fc.execute(search));
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
		s = FlowController.getStorage();

		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";

		String commandDisplay = "display ++";

		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		status = s.getStatus() + "\n\n";
		assertEquals("check display",
				status + "2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));
		String command5 = "undo";
		fc.execute(command5);
		status = s.getStatus() + "\n\n";
		assertEquals("check display change",
				status + "2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));
	}

	@Test
	public void testNonConsecutiveUndos() {
		Storage.clear();

		TaskList td = Storage.getTD();

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
	public void testMultipleConsecutiveUndos() {
		Storage.clear();

		TaskList td = Storage.getTD();

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
		s = FlowController.getStorage();

		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";

		String commandDisplay = "display";

		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);

		status = s.getStatus() + "\n\n";
		assertEquals("check display",
				status + "2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));
		String command4 = "queue 1";
		fc.execute(command4);
		status = s.getStatus() + "\n\n";
		assertEquals("task is queued",
				status + "Queue:"
						+ "\n1. meeting        "
						+ "\n\nOther Tasks:"
						+ "\n2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59\n",
				fc.execute(commandDisplay));
		String command5 = "undo";
		fc.execute(command5);
		status = s.getStatus() + "\n\n";
		assertEquals("task is unqueued",
				status + "2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));
	}

	@Test
	public void testRedo() {
		Storage.clear();
		s = FlowController.getStorage();

		String command1 = "add meeting @meeting room 7";
		String command2 = "add staff retreat @botanic gardens $16/05 +0500";
		String command3 = "add interview intern @mr5 $13/12/16";

		String commandDisplay = "display ++";

		fc.execute(command1);
		fc.execute(command2);
		fc.execute(command3);
		status = s.getStatus() + "\n\n";
		assertEquals("check display",
				status + "2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));

		String commandDO = "undo";
		fc.execute(commandDO);
		status = s.getStatus() + "\n\n";
		assertEquals("check display change",
				status + "2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));

		commandDO = "redo";
		fc.execute(commandDO);
		status = s.getStatus() + "\n\n";
		assertEquals("check display",
				status + "2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));

	}

	@Test
	public void testMultipleRedo() {
		Storage.clear();

		TaskList td = Storage.getTD();

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
	public void testRedoAfterNonUndo() {
		Storage.clear();
		s = FlowController.getStorage();

		TaskList td = Storage.getTD();

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
		fc.execute(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 5, td.size());
		fc.execute(undo);
		td = Storage.getTD();
		assertEquals("able to undo multiple times", 4, td.size());
		fc.execute(command5);
		String redo = "redo";
		status = s.getStatus() + "\n\n";
		assertEquals("previous command not undo", status + "There is no previous action to redo!", fc.execute(redo));
	}

	@Test
	public void testAddToBackOfQueue() {
		Storage.clear();
		s = FlowController.getStorage();

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

		status = s.getStatus() + "\n\n";
		assertEquals("check normal display without queue",
				status + "4. financial re... | By: Sun, 01 May 2016 23:59"
						+ "\n6. payday          | By: Thu, 05 May 2016 23:59"
						+ "\n5. client meeting  | By: Mon, 09 May 2016 23:59"
						+ "\n7. email boss      | By: Sun, 15 May 2016 23:59"
						+ "\n2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));

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
	public void testQueueIDUpdated() {
		Storage.clear();
		s = FlowController.getStorage();

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

		status = s.getStatus() + "\n\n";
		assertEquals("check normal display without queue",
				status + "4. financial re... | By: Sun, 01 May 2016 23:59"
						+ "\n6. payday          | By: Thu, 05 May 2016 23:59"
						+ "\n5. client meeting  | By: Mon, 09 May 2016 23:59"
						+ "\n7. email boss      | By: Sun, 15 May 2016 23:59"
						+ "\n2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));

		String command8 = "queue 4";
		String command9 = "queue 6";
		String command10 = "queue 5";
		String command11 = "queue 7 1";
		fc.execute(command8);
		t = td.get(4);
		assertEquals("queue id is updated", 1, t.getQueueID());
		fc.execute(command9);
		fc.execute(command10);
		fc.execute(command11);
		assertEquals("queue id is updated", 2, t.getQueueID());
	}

	// @@author A0124274L
	@Test
	public void testQueuePositionHead() {
		Storage.clear();
		s = FlowController.getStorage();

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

		status = s.getStatus() + "\n\n";
		assertEquals("check normal display without queue",
				status + "4. financial re... | By: Sun, 01 May 2016 23:59"
						+ "\n6. payday          | By: Thu, 05 May 2016 23:59"
						+ "\n5. client meeting  | By: Mon, 09 May 2016 23:59"
						+ "\n7. email boss      | By: Sun, 15 May 2016 23:59"
						+ "\n2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));

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
	public void testQueuePositionMiddle() {
		Storage.clear();
		s = FlowController.getStorage();

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

		status = s.getStatus() + "\n\n";
		assertEquals("check normal display without queue",
				status + "4. financial re... | By: Sun, 01 May 2016 23:59"
						+ "\n6. payday          | By: Thu, 05 May 2016 23:59"
						+ "\n5. client meeting  | By: Mon, 09 May 2016 23:59"
						+ "\n7. email boss      | By: Sun, 15 May 2016 23:59"
						+ "\n2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));

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
	public void testQueuePositionEnd() {
		// testing when the pos index is too large
		Storage.clear();
		s = FlowController.getStorage();

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

		status = s.getStatus() + "\n\n";
		assertEquals("check normal display without queue",
				status + "4. financial re... | By: Sun, 01 May 2016 23:59"
						+ "\n6. payday          | By: Thu, 05 May 2016 23:59"
						+ "\n5. client meeting  | By: Mon, 09 May 2016 23:59"
						+ "\n7. email boss      | By: Sun, 15 May 2016 23:59"
						+ "\n2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));

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

	// @@author A0111101N
	@Test
	public void testQueueIDAfterDeletion() {
		Storage.clear();
		s = FlowController.getStorage();

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

		status = s.getStatus() + "\n\n";
		assertEquals("check normal display without queue",
				status + "4. financial re... | By: Sun, 01 May 2016 23:59"
						+ "\n6. payday          | By: Thu, 05 May 2016 23:59"
						+ "\n5. client meeting  | By: Mon, 09 May 2016 23:59"
						+ "\n7. email boss      | By: Sun, 15 May 2016 23:59"
						+ "\n2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));

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
		assertEquals("queue id is updated after deletion", 4, t.getQueueID());
	}

	@Test
	public void testQueueIDAfterCompletion() {
		Storage.clear();
		s = FlowController.getStorage();

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

		status = s.getStatus() + "\n\n";
		assertEquals("check normal display without queue",
				status + "4. financial re... | By: Sun, 01 May 2016 23:59"
						+ "\n6. payday          | By: Thu, 05 May 2016 23:59"
						+ "\n5. client meeting  | By: Mon, 09 May 2016 23:59"
						+ "\n7. email boss      | By: Sun, 15 May 2016 23:59"
						+ "\n2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));

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
	public void testCompletedTaskQueueID() {
		Storage.clear();
		s = FlowController.getStorage();

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

		status = s.getStatus() + "\n\n";
		assertEquals("check normal display without queue",
				status + "4. financial re... | By: Sun, 01 May 2016 23:59"
						+ "\n6. payday          | By: Thu, 05 May 2016 23:59"
						+ "\n5. client meeting  | By: Mon, 09 May 2016 23:59"
						+ "\n7. email boss      | By: Sun, 15 May 2016 23:59"
						+ "\n2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));

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
		t = td.get(6);
		String command15 = "complete 6";
		fc.execute(command15);
		assertEquals("queue id of completed is updated to default", -2, t.getQueueID());
	}

	@Test
	public void testQueueDisplay() {
		Storage.clear();
		s = FlowController.getStorage();

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

		status = s.getStatus() + "\n\n";
		assertEquals("check normal display without queue",
				status + "4. financial re... | By: Sun, 01 May 2016 23:59"
						+ "\n6. payday          | By: Thu, 05 May 2016 23:59"
						+ "\n5. client meeting  | By: Mon, 09 May 2016 23:59"
						+ "\n7. email boss      | By: Sun, 15 May 2016 23:59"
						+ "\n2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));

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

		status = s.getStatus() + "\n\n";
		assertEquals("check normal display without queue",
				status + "Queue:\n7. email boss      | By: Sun, 15 May 2016 23:59"
						+ "\n4. financial re... | By: Sun, 01 May 2016 23:59"
						+ "\n6. payday          | By: Thu, 05 May 2016 23:59"
						+ "\n2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n5. client meeting  | By: Mon, 09 May 2016 23:59"
						+ "\n1. meeting        "
						+ "\n\nOther Tasks:"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59\n",
				fc.execute(commandDisplay));
	}

	@Test
	public void testQueueWithUndo() {
		Storage.clear();
		s = FlowController.getStorage();

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

		status = s.getStatus() + "\n\n";
		assertEquals("check normal display without queue",
				status + "4. financial re... | By: Sun, 01 May 2016 23:59"
						+ "\n6. payday          | By: Thu, 05 May 2016 23:59"
						+ "\n5. client meeting  | By: Mon, 09 May 2016 23:59"
						+ "\n7. email boss      | By: Sun, 15 May 2016 23:59"
						+ "\n2. staff retreat   | By: Mon, 16 May 2016 05:00"
						+ "\n3. interview in... | By: Tue, 13 Dec 2016 23:59"
						+ "\n1. meeting        \n",
				fc.execute(commandDisplay));

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