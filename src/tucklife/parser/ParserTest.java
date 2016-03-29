package tucklife.parser;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

public class ParserTest {
	
	Parser p;
	ProtoTask pt;

	@Before
	public void setUp() throws Exception {
		p = new Parser();
	}

	@Test
	public void testAdd() {
		// 0 parameters
		assertTrue(p.parse("add").isError());
		
		// No task description
		assertTrue(p.parse("add !high").isError());
		
		// Task description
		assertEquals("Command type: add\nParameters:\nTask description: work\n",
					 p.parse("add work").toString());
		assertEquals("Command type: add\nParameters:\nTask description: finish all my work!\n",
					 p.parse("add finish all my work!").toString());
		assertEquals("Command type: add\nParameters:\nTask description: 123\n",
					 p.parse("add 123").toString());
		
		// Location - @
		assertEquals("Command type: add\nParameters:\nTask description: work\nLocation: office\n",
					 p.parse("add work @office").toString());
		assertEquals("Command type: add\nParameters:\nTask description: dinner\nLocation: atas restaurant\n",
					 p.parse("add dinner @atas restaurant").toString());
		
		// Priority - !
		assertEquals("Command type: add\nParameters:\nTask description: very important task\nPriority: 1\n",
					 p.parse("add very important task !high").toString());
		assertEquals("Command type: add\nParameters:\nTask description: normal task\nPriority: 2\n",
				 	 p.parse("add normal task !medium").toString());
		assertEquals("Command type: add\nParameters:\nTask description: optional task\nPriority: 3\n",
				 	 p.parse("add optional task !low").toString());
		
		// Invalid priority
		assertTrue(p.parse("add invalid priority !extremely important").isError());
		
		// Category - #
		assertEquals("Command type: add\nParameters:\nTask description: unit testing\nCategory: CS2103T\n",
					 p.parse("add unit testing #CS2103T").toString());
		assertEquals("Command type: add\nParameters:\nTask description: do report\nCategory: top secret project\n",
					 p.parse("add do report #top secret project").toString());
		
		// Additional information - &
		assertEquals("Command type: add\nParameters:\nTask description: dinner with mum\n"
					 + "Additional information: sushi! :D\n",
					 p.parse("add dinner with mum &sushi! :D").toString());
		assertEquals("Command type: add\nParameters:\nTask description: meeting\n"
					 + "Additional information: about sales performance. Prepare to present.\n",
					 p.parse("add meeting &about sales performance. Prepare to present.").toString());
		
		// Date - $
		assertEquals("Command type: add\nParameters:\nTask description: job interview\n"
					 + "End date: Fri, 25 Mar 2016, 23:59\n",
					 p.parse("add job interview $25/3/16").toString());
		
		// Invalid dates
		assertTrue(p.parse("add my birthday $30 feb").isError());
		assertTrue(p.parse("add task $not a date").isError());
		
		// Time - +
		// Getting the correct date based on current date
		Calendar c = Calendar.getInstance();
		if (c.get(Calendar.HOUR_OF_DAY) > 16) {
			c.add(Calendar.DATE, 1);
		}
		c.set(Calendar.HOUR_OF_DAY, 17);
		c.set(Calendar.MINUTE, 0);
		
		assertEquals("Command type: add\nParameters:\nTask description: evening run\nEnd date: "
					 + new SimpleDateFormat("EEE, dd MMM yyyy, HH:mm").format(c.getTime()) + "\n",
					 p.parse("add evening run +5pm").toString());
		
		// Invalid times
		assertTrue(p.parse("add 13 o'clock +13pm").isError());
		assertTrue(p.parse("add task +not a time").isError());
		
		// All parameters
		assertEquals("Command type: add\nParameters:\nTask description: meeting with boss\n"
					 + "Location: boss's office\nCategory: top secret project\nAdditional information: "
					 + "BIG BOSS COMING!!!\nPriority: 1\nEnd date: Mon, 28 Mar 2016, 10:00\n",
					 p.parse("add meeting with boss !high +10am $28-march-2016 @boss's office"
							 + " &BIG BOSS COMING!!! #top secret project").toString());
		
		// Some parameters
		assertEquals("Command type: add\nParameters:\nTask description: buy office supplies\n"
					 + "Category: misc\nPriority: 2\nEnd date: Thu, 24 Mar 2016, 23:59\n",
					 p.parse("add buy office supplies $24 mar 16 #misc !medium").toString());
	}
	
	@Test
	public void testComplete() {
		// 0 parameters
		assertTrue(p.parse("complete").isError());

		// 1 parameter

		// ID not a number
		assertTrue(p.parse("complete abc").isError());

		// ID < 1
		assertTrue(p.parse("complete -1").isError());

		// ID >= 1
		assertEquals("Command type: complete\nParameters:\nID: 5\n", p.parse("complete 5").toString());

		// Boundary
		assertTrue(p.parse("complete 0").isError());
		assertEquals("Command type: complete\nParameters:\nID: 1\n", p.parse("complete 1").toString());
		assertEquals("Command type: complete\nParameters:\nID: 2\n", p.parse("complete 2").toString());

		// > 1 parameter
		assertTrue(p.parse("complete 1 1").isError());
	}

	@Test
	public void testDelete() {
		// 0 parameters
		assertTrue(p.parse("delete").isError());

		// 1 parameter

		// ID not a number
		assertTrue(p.parse("delete abc").isError());

		// ID < 1
		assertTrue(p.parse("delete -1").isError());

		// ID >= 1
		assertEquals("Command type: delete\nParameters:\nID: 5\n", p.parse("delete 5").toString());

		// Boundary
		assertTrue(p.parse("delete 0").isError());
		assertEquals("Command type: delete\nParameters:\nID: 1\n", p.parse("delete 1").toString());
		assertEquals("Command type: delete\nParameters:\nID: 2\n", p.parse("delete 2").toString());

		// > 1 parameter
		assertTrue(p.parse("delete 1 1").isError());
	}
	
	@Test
	public void testDemo() {
		// 0 parameters
		assertTrue(p.parse("demo").isError());
		
		// 1 parameter
		
		// Invalid command
		assertTrue(p.parse("demo notacommand").isError());
		
		// Valid command
		assertEquals("Command type: demo\nParameters:\nDemo command: add\n", p.parse("demo add").toString());
		assertEquals("Command type: demo\nParameters:\nDemo command: help\n", p.parse("demo help").toString());
		assertEquals("Command type: demo\nParameters:\nDemo command: undo\n", p.parse("demo undo").toString());
		
		// > 1 parameter
		assertTrue(p.parse("demo demo demo").isError());
	}
	
	@Test
	public void testDisplay() {
		// 0 parameters
		assertEquals("Command type: display\nParameters:\n", p.parse("display").toString());
		
		//TODO
	}
	
	@Test
	public void testDisplaydone() {
		// 0 parameters
		assertEquals("Command type: displaydone\nParameters:\n", p.parse("displaydone").toString());
		
		//TODO
	}
	
	@Test
	public void testEdit() {
		// 0 parameters
		assertTrue(p.parse("edit").isError());

		// 1 parameter
		assertTrue(p.parse("edit !high").isError());
		assertTrue(p.parse("edit 1").isError());
		
		// > 1 parameter
		
		// ID is not a number
		assertTrue(p.parse("edit something here").isError());
		
		// ID < 1
		assertTrue(p.parse("edit -1 new task name").isError());
		
		// ID >= 1

		// Task description
		assertEquals("Command type: edit\nParameters:\nTask description: work\nID: 1\n",
					 p.parse("edit 1 work").toString());
		assertEquals("Command type: edit\nParameters:\nTask description: finish all my work!\nID: 1\n",
					 p.parse("edit 1 finish all my work!").toString());
		assertEquals("Command type: edit\nParameters:\nTask description: 123\nID: 1\n",
				 	 p.parse("edit 1 123").toString());

		// Location - @
		assertEquals("Command type: edit\nParameters:\nLocation: office\nID: 1\n",
				p.parse("edit 1 @office").toString());
		assertEquals("Command type: edit\nParameters:\nLocation: atas restaurant\nID: 1\n",
				p.parse("edit 1 @atas restaurant").toString());

		// Priority - !
		assertEquals("Command type: edit\nParameters:\nPriority: 1\nID: 1\n",
				p.parse("edit 1 !high").toString());
		assertEquals("Command type: edit\nParameters:\nPriority: 2\nID: 1\n",
				p.parse("edit 1 !medium").toString());
		assertEquals("Command type: edit\nParameters:\nPriority: 3\nID: 1\n",
				p.parse("edit 1 !low").toString());

		// Invalid priority
		assertTrue(p.parse("edit 1 !extremely important").isError());

		// Category - #
		assertEquals("Command type: edit\nParameters:\nCategory: CS2103T\nID: 1\n",
				p.parse("edit 1 #CS2103T").toString());
		assertEquals("Command type: edit\nParameters:\nCategory: top secret project\nID: 1\n",
				p.parse("edit 1 #top secret project").toString());

		// Additional information - &
		assertEquals("Command type: edit\nParameters:\nAdditional information: sushi! :D\nID: 15\n",
				p.parse("edit 15 &sushi! :D").toString());
		assertEquals("Command type: edit\nParameters:\nAdditional information: about sales performance. "
					 + "Prepare to present.\nID: 15\n",
				p.parse("edit 15 &about sales performance. Prepare to present.").toString());

		// Date - $
		assertEquals("Command type: edit\nParameters:\nID: 15\nEnd date: Fri, 25 Mar 2016, 23:59\n",
				p.parse("edit 15 $25/3/16").toString());

		// Invalid dates
		assertTrue(p.parse("edit 15 $30 feb").isError());
		assertTrue(p.parse("edit 15 $not a date").isError());

		// Time - +
		// Getting the correct date based on current date
		Calendar c = Calendar.getInstance();
		if (c.get(Calendar.HOUR_OF_DAY) > 16) {
			c.add(Calendar.DATE, 1);
		}
		c.set(Calendar.HOUR_OF_DAY, 17);
		c.set(Calendar.MINUTE, 0);
				
		assertEquals("Command type: edit\nParameters:\nID: 15\nEnd date: "
					 + new SimpleDateFormat("EEE, dd MMM yyyy, HH:mm").format(c.getTime()) + "\n",
				p.parse("edit 15 +5pm").toString());

		// Invalid times
		assertTrue(p.parse("edit 15 +13pm").isError());
		assertTrue(p.parse("edit 15 +not a time").isError());

		// All parameters
		assertEquals("Command type: edit\nParameters:\nTask description: meeting with boss\n"
				+ "Location: boss's office\nCategory: top secret project\nAdditional information: "
				+ "BIG BOSS COMING!!!\nPriority: 1\nID: 15\nEnd date: Mon, 28 Mar 2016, 10:00\n",
				p.parse("edit 15 meeting with boss !high +10am $28-march-2016 @boss's office"
						+ " &BIG BOSS COMING!!! #top secret project").toString());

		// Some parameters
		assertEquals("Command type: edit\nParameters:\nCategory: misc\n"
				+ "Priority: 2\nID: 15\nEnd date: Thu, 24 Mar 2016, 23:59\n",
				p.parse("edit 15 $24 mar 16 #misc !medium").toString());
	}
	
	@Test
	public void testExit() {
		// 0 parameters
		assertEquals("Command type: exit\nParameters:\n", p.parse("exit").toString());
		
		// Has parameters
		assertTrue(p.parse("exit 1").isError());
	}
	
	@Test
	public void testHelp() {
		// 0 parameters
		assertEquals("Command type: help\nParameters:\n", p.parse("help").toString());

		// Has parameters
		assertTrue(p.parse("help me").isError());
	}
	
	@Test
	public void testQueue() {
		// 0 parameters
		assertTrue(p.parse("queue").isError());
		
		// 1 parameter
		
		// ID not a number
		assertTrue(p.parse("queue up").isError());
		
		// ID < 1
		assertTrue(p.parse("queue -1").isError());
		
		// ID >= 1
		assertEquals("Command type: queue\nParameters:\nID: 5\n", p.parse("queue 5").toString());
		
		// Boundary
		assertTrue(p.parse("queue 0").isError());
		assertEquals("Command type: queue\nParameters:\nID: 1\n", p.parse("queue 1").toString());
		assertEquals("Command type: queue\nParameters:\nID: 2\n", p.parse("queue 2").toString());
		
		// 2 parameters
		
		// Valid ID, invalid position
		assertTrue(p.parse("queue 5 position").isError());
		
		// Invalid ID, valid position
		assertTrue(p.parse("queue -1 2").isError());
		
		// Invalid ID, invalid position
		assertTrue(p.parse("queue up please").isError());
		
		// Position not a number
		assertTrue(p.parse("queue 1 abc").isError());
		
		// Position < 1
		assertTrue(p.parse("queue 1 -5").isError());
		
		// Position >= 1
		assertEquals("Command type: queue\nParameters:\nID: 1\nPosition: 5\n",
					 p.parse("queue 1 5").toString());
		
		// Boundary
		assertTrue(p.parse("queue 0 -1").isError());
		assertEquals("Command type: queue\nParameters:\nID: 1\nPosition: 1\n",
					 p.parse("queue 1 1").toString());
		assertEquals("Command type: queue\nParameters:\nID: 2\nPosition: 2\n",
				 	 p.parse("queue 2 2").toString());
		
		// > 2 parameters
		assertTrue(p.parse("queue 1 2 3").isError());
	}
	
	@Test
	public void testRedo() {
		// 0 parameters
		assertEquals("Command type: redo\nParameters:\n", p.parse("redo").toString());

		// Has parameters
		assertTrue(p.parse("redo 1").isError());
	}
	
	@Test
	public void testSave() {
		// 0 parameters
		assertEquals("Command type: save\nParameters:\n", p.parse("save").toString());

		// Has parameters
		assertTrue(p.parse("save me").isError());
	}
	
	@Test
	public void testSaveto() {
		// 0 parameters
		assertTrue(p.parse("saveto").isError());

		// Has parameters
		assertEquals("Command type: saveto\nParameters:\nPath: myfile.txt\n",
					 p.parse("saveto myfile.txt").toString());		
		assertEquals("Command type: saveto\nParameters:\nPath: C:/Documents/myfile.txt\n",
					 p.parse("saveto C:/Documents/myfile.txt").toString());
	}

	@Test
	public void testSetlimit() {
		// 0 parameters
		assertTrue(p.parse("setlimit").isError());

		// 1 parameter

		// ID not a number
		assertTrue(p.parse("setlimit abc").isError());

		// ID < 0
		assertTrue(p.parse("setlimit -1").isError());

		// ID >= 0
		assertEquals("Command type: delete\nParameters:\nID: 5\n", p.parse("delete 5").toString());

		// Boundary
		assertTrue(p.parse("delete 0").isError());
		assertEquals("Command type: delete\nParameters:\nID: 1\n", p.parse("delete 1").toString());
		assertEquals("Command type: delete\nParameters:\nID: 2\n", p.parse("delete 2").toString());

		// > 1 parameter
		assertTrue(p.parse("delete 1 1").isError());
	}
	
	@Test
	public void testSetdefault() {
		//TODO
		// Not yet implemented
	}
	
	@Test
	public void testUndo() {
		// 0 parameters
		assertEquals("Command type: undo\nParameters:\n", p.parse("undo").toString());

		// Has parameters
		assertTrue(p.parse("undo 1").isError());
	}
	
	@Test
	public void testInvalidCommand() {
		// Invalid command without parameters
		assertTrue(p.parse("notacommand").isError());
		assertTrue(p.parse("addd").isError());
		
		// Invalid command with parameters
		assertTrue(p.parse("not a command").isError());
	}
}