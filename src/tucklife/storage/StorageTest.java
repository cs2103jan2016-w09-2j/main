package tucklife.storage;

import static org.junit.Assert.*;

import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;

import tucklife.parser.Parser;
import tucklife.parser.ProtoTask;

public class StorageTest {
	
	Parser p;
	Storage s;
	
	@Before
	public void setUp() throws Exception {
		p = new Parser();
		s = new Storage();
		Hashtable<String,String> ht = new Hashtable<String,String>();
		p.loadCommands(ht);
	}
	
	@Test
	public void testSortLocation() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("add meeting @as4-mr3");
		ProtoTask pt2 = p.parse("add staff retreat @sentosa");
		ProtoTask pt3 = p.parse("add interview intern");
		
		ProtoTask pt4 = p.parse("display");
		
		s.parseCommand(pt1);
		s.parseCommand(pt2);
		s.parseCommand(pt3);
		assertEquals("not sorted",s.parseCommand(pt4),"3. interview intern\n1. meeting | Location: as4-mr3\n2. staff retreat | Location: sentosa\n");
		
		ProtoTask pt5 = p.parse("display +@");
		assertEquals("sorted ascending",s.parseCommand(pt5),"1. meeting | Location: as4-mr3\n2. staff retreat | Location: sentosa\n3. interview intern\n");
		ProtoTask pt6 = p.parse("display -@");
		assertEquals("sorted descending",s.parseCommand(pt6),"3. interview intern\n2. staff retreat | Location: sentosa\n1. meeting | Location: as4-mr3\n");
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
		
		s.parseCommand(pt1);
		s.parseCommand(pt2);
		s.parseCommand(pt3);
		s.parseCommand(pt4);

		assertEquals("able to add",s.parseCommand(pt5),"{4. 4th task | By: Mon, 16 May 2016 23:59} has been added to TuckLife's to-do list!");
		assertEquals("unable to add, hit limit",s.parseCommand(pt6),"That day has been filled with 4 tasks! It hit the limit! You should reschedule the task to another day. Alternatively, you can either change the overload limit or turn it off.");
	}
	
	@Test
	public void testUndo() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $tomorrow +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		
		ProtoTask ptDisplay = p.parse("display ++");
		
		s.parseCommand(pt1);
		s.parseCommand(pt2);
		s.parseCommand(pt3);
		
		assertEquals("check display",s.parseCommand(ptDisplay),"2. staff retreat | By: Thu, 31 Mar 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		ProtoTask pt5 = p.parse("undo");	
		s.parseCommand(pt5);
		assertEquals("check display change",s.parseCommand(ptDisplay),"2. staff retreat | By: Thu, 31 Mar 2016 05:00 | Location: botanic gardens\n1. meeting | Location: meeting room 7\n");
	}
	
	@Test
	public void testRedo() {
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $tomorrow +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		
		ProtoTask ptDisplay = p.parse("display ++");
		
		s.parseCommand(pt1);
		s.parseCommand(pt2);
		s.parseCommand(pt3);

		assertEquals("check display",s.parseCommand(ptDisplay),"2. staff retreat | By: Thu, 31 Mar 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
		ProtoTask ptDO = p.parse("undo");	
		s.parseCommand(ptDO);
		assertEquals("check display change",s.parseCommand(ptDisplay),"2. staff retreat | By: Thu, 31 Mar 2016 05:00 | Location: botanic gardens\n1. meeting | Location: meeting room 7\n");
		
		ptDO = p.parse("redo");
		s.parseCommand(ptDO);
		assertEquals("check display",s.parseCommand(ptDisplay),"2. staff retreat | By: Thu, 31 Mar 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
	}
	
	@Test
	public void testQueue() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7");
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $16/05 +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/05/16");
		ProtoTask pt5 = p.parse("add client meeting $09/05/16");
		ProtoTask pt6 = p.parse("add payday $05/05/16");
		ProtoTask pt7 = p.parse("add email boss $15/05");
		
		ProtoTask ptDisplay = p.parse("display");
		
		s.parseCommand(pt1);
		s.parseCommand(pt2);
		s.parseCommand(pt3);
		s.parseCommand(pt4);
		s.parseCommand(pt5);
		s.parseCommand(pt6);
		s.parseCommand(pt7);
		
		System.out.println(s.parseCommand(ptDisplay));
		assertEquals("check normal display without queue",s.parseCommand(ptDisplay),"4. financial report | By: Sun, 1 May 2016 23:59\n6. payday | By: Thu, 5 May 2016 23:59\n5. client meeting | By: Mon, 9 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
		
		ProtoTask pt8 = p.parse("queue 4");
		ProtoTask pt9 = p.parse("queue 6");
		ProtoTask pt10 = p.parse("queue 7 1");
		ProtoTask pt11 = p.parse("queue 4 1");
		s.parseCommand(pt8);
		s.parseCommand(pt9);
		s.parseCommand(pt10);
		s.parseCommand(pt11);
		
		assertEquals("check display with queue",s.parseCommand(ptDisplay),"4. financial report | By: Sun, 1 May 2016 23:59\n7. email boss | By: Sun, 15 May 2016 23:59\n6. payday | By: Thu, 5 May 2016 23:59\n5. client meeting | By: Mon, 9 May 2016 23:59\n2. staff retreat | By: Mon, 16 May 2016 05:00 | Location: botanic gardens\n3. interview intern | By: Tue, 13 Dec 2016 23:59 | Location: mr5\n1. meeting | Location: meeting room 7\n");
	}

	
}
