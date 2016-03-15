package tucklife.storage;

import tucklife.parser.ProtoTask;
import tucklife.parser.Parser;

public class TestDriverForStorage {
	public static void main(String[] args){
		testSortLocation();
		testSortTime();
	}

	private static void testSortLocation() {
		Storage s1 = new Storage();
		Storage.clear();
		Parser p = new Parser();
		
		ProtoTask pt1 = p.parse("add meeting @as4-mr3");
		ProtoTask pt2 = p.parse("add staff retreat @sentosa");
		ProtoTask pt3 = p.parse("add interview intern @office");
		
		ProtoTask pt4 = p.parse("display");
		
		s1.parseCommand(pt1);
		s1.parseCommand(pt2);
		s1.parseCommand(pt3);
		
		System.out.println(s1.parseCommand(pt4));
		
		ProtoTask pt5 = p.parse("display +@");
		pt5.setSortCrit("@");
		
		System.out.println(s1.parseCommand(pt5));
	}
	
	private static void testSortTime() {
		Storage s = new Storage();
		Storage.clear();
		Parser p = new Parser();
		
		ProtoTask pt1 = p.parse("add meeting @meeting room 7"); /*$13/12/16 to 14/12/16 +8am to 3pm*/
		ProtoTask pt2 = p.parse("add staff retreat @botanic gardens $tomorrow");
		ProtoTask pt3 = p.parse("add interview intern @mr5 $13/12/16");
		
		ProtoTask pt4 = p.parse("display ++");
		
		s.parseCommand(pt1);
		s.parseCommand(pt2);
		s.parseCommand(pt3);
		
		System.out.println(s.parseCommand(pt4));
		
		ProtoTask pt5 = p.parse("display +$");
		pt5.setSortCrit("$");
		
		System.out.println(s.parseCommand(pt5));
	}
}
