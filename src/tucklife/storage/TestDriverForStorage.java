package tucklife.storage;

import tucklife.parser.ProtoTask;
import tucklife.parser.Parser;

public class TestDriverForStorage {
	public static void main(String[] args){
		testSortLocation();
		/*
		testSortTime();
		testOverload();
		testOverload2();
		testUndo();
		testRedo();
		*/
		testQueue();
	}

	private static void testSortLocation() {
		System.out.println("testSortLocation");
		Storage s1 = new Storage();
		Storage.clear();
		Parser p = new Parser();
		
		ProtoTask pt1 = p.parse("add meeting @as4-mr3");
		ProtoTask pt2 = p.parse("add staff retreat @sentosa");
		ProtoTask pt3 = p.parse("add interview intern");
		
		ProtoTask pt4 = p.parse("display");
		
		s1.parseCommand(pt1);
		s1.parseCommand(pt2);
		s1.parseCommand(pt3);
		System.out.println("not sorted");
		System.out.println(s1.parseCommand(pt4));
		
		ProtoTask pt5 = p.parse("display +@");
		pt5.setSortCrit("@");
		System.out.println("sorted ascending");
		System.out.println(s1.parseCommand(pt5));
		ProtoTask pt6 = p.parse("display -@");
		System.out.println("sorted descending");
		System.out.println(s1.parseCommand(pt6));
		System.out.println();
	}
	
	private static void testSortTime() {
		System.out.println("testSortTime");
		Storage s = new Storage();
		Storage.clear();
		Parser p = new Parser();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7"); /*$13/12/16 to 14/12/16 +8am to 3pm*/
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $tomorrow +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		
		ProtoTask pt4 = p.parse("display ++");
		
		s.parseCommand(pt1);
		s.parseCommand(pt2);
		s.parseCommand(pt3);
		
		System.out.println(s.parseCommand(pt4));
		
		ProtoTask pt5 = p.parse("display +$");
		pt5.setSortCrit("$");
		
		System.out.println(s.parseCommand(pt5));
		System.out.println();
	}
	
	private static void testOverload() {
		System.out.println("testOverload");
		Storage s = new Storage();
		Storage.clear();
		Parser p = new Parser();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7 $today +1400"); /*$13/12/16 to 14/12/16 +8am to 3pm*/
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $today +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $today +1300");
		
		ProtoTask pt4 = p.parse("setlimit 4");
		
		ProtoTask pt5 = p.parse("add 4th task $today +2359");
		ProtoTask pt6 = p.parse("add overload $today +2300");
		
		s.parseCommand(pt1);
		s.parseCommand(pt2);
		s.parseCommand(pt3);
		s.parseCommand(pt4);
		System.out.println(s.parseCommand(pt5));
		System.out.println(s.parseCommand(pt6));
		System.out.println(s.parseCommand(pt6));
		System.out.println();
	}
	
	private static void testOverload2() {
		System.out.println("testOverload2");
		Storage s = new Storage();
		Storage.clear();
		Parser p = new Parser();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7 +1400"); /*$13/12/16 to 14/12/16 +8am to 3pm*/
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 +1300");
		
		ProtoTask pt4 = p.parse("setlimit 4");
		
		ProtoTask pt5 = p.parse("add 4th task +2359");
		ProtoTask pt6 = p.parse("add overload +2300");
		
		s.parseCommand(pt1);
		s.parseCommand(pt2);
		s.parseCommand(pt3);
		s.parseCommand(pt4);
		System.out.println(s.parseCommand(pt5));
		System.out.println(s.parseCommand(pt6));
		System.out.println(s.parseCommand(pt6));
		ProtoTask ptDisplay = p.parse("display");
		System.out.println(s.parseCommand(ptDisplay));
		System.out.println();
	}
	
	private static void testUndo() {
		System.out.println("testUndo");
		Storage s = new Storage();
		Storage.clear();
		Parser p = new Parser();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7"); /*$13/12/16 to 14/12/16 +8am to 3pm*/
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $tomorrow +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		
		ProtoTask pt4 = p.parse("display ++");
		
		s.parseCommand(pt1);
		s.parseCommand(pt2);
		s.parseCommand(pt3);
		
		System.out.println(s.parseCommand(pt4));
		ProtoTask pt5 = p.parse("undo");	
		System.out.println(s.parseCommand(pt5));
		System.out.println(s.parseCommand(pt4));
		System.out.println();
	}
	
	private static void testRedo() {
		System.out.println("testRedo");
		Storage s = new Storage();
		Storage.clear();
		Parser p = new Parser();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7"); /*$13/12/16 to 14/12/16 +8am to 3pm*/
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $tomorrow +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		
		ProtoTask ptDisplay = p.parse("display ++");
		
		s.parseCommand(pt1);
		s.parseCommand(pt2);
		s.parseCommand(pt3);
		
		System.out.println(s.parseCommand(ptDisplay));
		ProtoTask ptDO = p.parse("undo");	
		System.out.println(s.parseCommand(ptDO));
		System.out.println(s.parseCommand(ptDisplay));
		ptDO = p.parse("redo");
		System.out.println(s.parseCommand(ptDO));
		System.out.println(s.parseCommand(ptDisplay));
		System.out.println();
	}
	
	private static void testQueue() {
		System.out.println("testQueue");
		Storage s = new Storage();
		Storage.clear();
		Parser p = new Parser();
		ProtoTask ptlimit = p.parse("setlimit 100");
		s.parseCommand(ptlimit);
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7"); /*$13/12/16 to 14/12/16 +8am to 3pm*/
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $tomorrow +0500");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		ProtoTask pt4 = p.parse("add financial report $01/04/16");
		ProtoTask pt5 = p.parse("add client meeting $09/04/16");
		ProtoTask pt6 = p.parse("add payday $05/04/16");
		ProtoTask pt7 = p.parse("add email boss $today");
		
		ProtoTask ptDisplay = p.parse("display");
		
		s.parseCommand(pt1);
		s.parseCommand(pt2);
		s.parseCommand(pt3);
		s.parseCommand(pt4);
		s.parseCommand(pt5);
		s.parseCommand(pt6);
		s.parseCommand(pt7);
		
		System.out.println(s.parseCommand(ptDisplay));
		
		ProtoTask pt8 = p.parse("queue 4");
		ProtoTask pt9 = p.parse("queue 6");
		ProtoTask pt10 = p.parse("queue 7 1");
		ProtoTask pt11 = p.parse("queue 4 1");
		s.parseCommand(pt8);
		s.parseCommand(pt9);
		s.parseCommand(pt10);
		s.parseCommand(pt11);
		
		System.out.println(s.parseCommand(ptDisplay));
		System.out.println();
	}
}
