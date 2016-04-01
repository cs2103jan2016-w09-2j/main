package tucklife.storage;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class HelpStorageTest {
	
	private HelpStorage hs;

	@Before
	public void setUp() throws Exception {
		hs = new HelpStorage();
	}
	
	// test for file access/read
	@Test
	public void loadTest() {
		assertEquals(true, hs.load());
	}
	
	// Partition: functions with only demo command
	@Test
	public void singleDemoTest() {
		hs.load();
		String demo = hs.getDemo("save");
		assertEquals("Command: save\nResult: Saves any changes you have made in TuckLife\n", demo);
	}
	
	// Partition: functions with multiple demo commands
	@Test
	public void multipleDemoTest() {
		hs.load();
		String demo = hs.getDemo("add");
		assertEquals("Command: add board meeting\n"
				+ "Result: Adds the task \"board meeting\" to TuckLife\n"
				+ "\n"
				+ "Command: add board meeting $2 May\n"
				+ "Result: Adds the task \"read a book\" to TuckLife - By: 2 May\n"
				+ "\n"
				+ "Command: add board meeting !low #projectX +4pm\n"
				+ "Result: Adds the task \"board meeting\" to TuckLife - By: Today at 4:00pm | Priority: low | Category: projectX\n"
				+ "\n"
				+ "Command: add board meeting @meeting room 4 !low #projectX $11 May +4pm to 6pm &bring proposal\n"
				+ "Result: Adds the task \"board meeting\" to TuckLife - From: 11 May 4:00pm To: 11 May 6:00pm | Location: meeting room 4 | Priority: low | Category: projectX | additional: bring proposal\n"
				, demo);
	}
	
	// test for help file loading - visual inspection due to long file
	@Test
	public void helpTest() {
		hs.load();
		String help = hs.getHelp();
		System.out.println(help);
	}

}
