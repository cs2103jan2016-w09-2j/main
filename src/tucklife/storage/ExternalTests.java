package tucklife.storage;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ExternalTests {
	
	private ExternalStorage es;
	private TaskList[] lists;

	@Before
	public void setUp() throws Exception {
		es = new ExternalStorage();
		es.testSetup();
	}

	@Test
	public void loadTest() {
		boolean loaded = es.load();
		assertEquals(loaded, true);
	}
	
	@Test
	public void retriveTest() {
		es.load();
		lists = es.getLoadedLists();
		assertEquals(lists.length, 2);
	}
	
	@Test
	public void contentsTest() {
		es.load();
		lists = es.getLoadedLists();
		
		TaskList todo = lists[0];
		System.out.println(todo.display());
		
		TaskList done = lists[1];
		System.out.println(done.display());
	}
}
