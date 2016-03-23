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
		assertEquals(hs.load(), true);
	}
	
	// Partition: functions with only demo command
	@Test
	public void singleDemoTest() {
		hs.load();
		String demo = hs.getDemo("save");
		assertEquals(demo, "Command: save\nResult: Saves any changes you have made in TuckLife\n");
	}
	
	// Partition: functions with multiple demo commands
	@Test
	public void multipleDemoTest() {
		hs.load();
		String demo = hs.getDemo("add");
		assertEquals(demo, "Command: add read a book\n"
				+ "Result: Adds the task \"read a book\" to TuckLife with no parameters\n"
				+ "\n"
				+ "Command: add read a book $2 May\n"
				+ "Result: Adds the task \"read a book\" to TuckLife with parameters: date: 2 May | time: 11:59pm\n"
				+ "\n"
				+ "Command: add walk the dog !low #pet +4pm\n"
				+ "Result: Adds the task \"walk the dog\" to TuckLife with parameters: date: today's date | time: 4:00pm | priority: low | category: pet\n"
				+ "\n"
				+ "Command: add walk the dog @park !low #pet $11 May +4pm to 6pm &walk 1km\n"
				+ "Result: Adds the task \"walk the dog\" to TuckLife with parameters: date: 11 May | time: 4:00pm to 6:00pm | location: park | priority: low | category: pet | additional information: walk 1km\n");
	}
	
	// test for help file loading
	@Test
	public void helpTest() {
		hs.load();
		String help = hs.getHelp();
		System.out.println(help);
	}

}
