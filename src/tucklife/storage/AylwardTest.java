//@@author A0124274L

package tucklife.storage;

import static org.junit.Assert.*;

import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;

import tucklife.UI.FlowController;
import tucklife.parser.Parser;
import tucklife.parser.ProtoTask;

public class AylwardTest {

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
	//editing task with non-existent ID
	public void testEdit() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("edit 56 meeting @as4-mr3");
		assertEquals("should have no ID found",fc.parseCommand(pt1),"No task with id:56 in TuckLife's to-do list!");		
	}
	
	@Test
	//editing task with later to earlier date
	public void testEdit2() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("add meeting");
		fc.parseCommand(pt1);
		ProtoTask pt2 = p.parse("edit 1 $6jul to 6jun");
		assertEquals("date fail",fc.parseCommand(pt2),"{1. meeting | From: Wed, 6 Jul 2016 00:00 To: Tue, 6 Jun 2017 23:59} has been edited in TuckLife's to-do list!");
	}
	
	@Test
	//editing with later to earlier time
	//works on 8Apr unless you edit the date below accordingly
	public void testEdit3() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("add meeting");
		fc.parseCommand(pt1);
		ProtoTask pt2 = p.parse("edit 1 +6pm to 2am");
		assertEquals("exception error", fc.parseCommand(pt2),"{1. meeting | From: Fri, 8 Apr 2016 18:00 To: Sat, 9 Apr 2016 02:00} has been edited in TuckLife's to-do list!");
	}
	
	@Test
	//adding with later to earlier date
	public void testAdd1() {
		Storage.clear();
		
		ProtoTask pt1 = p.parse("add meeting $6jul to 6jun");
		assertEquals("success",fc.parseCommand(pt1),"{1. meeting | From: Wed, 6 Jul 2016 00:00 To: Tue, 6 Jun 2017 23:59} has been added to TuckLife's to-do list!");
	}
	
	

}
