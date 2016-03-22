package tucklife.storage;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ListStorageTest {
	
	private ListStorage todo, done;
	private TaskList todoList, doneList;
	
	private static final String TEST_PATH = "C:\\Users\\Ryan\\Desktop\\Holding Area\\";
	public static final String FILENAME_TODO = "todo.txt";
	public static final String FILENAME_DONE = "done.txt";
	
	@Before
	public void setUp() throws Exception {
		todo = new ListStorage(TEST_PATH + FILENAME_TODO);
		done = new ListStorage(TEST_PATH + FILENAME_DONE);
		todoList = todo.getList();
		doneList = done.getList();
	}
	
	// test for file access/load
	@Test
	public void loadTest() {		
		assertEquals(todo.getLoadStatus(), true);
		assertEquals(done.getLoadStatus(), true);
	}
	
	// loading for various task types 
	@Test
	public void taskTest(){
		try{
			// todo list should have four tasks as test file has four tasks
			assertEquals(todoList.contains(1), true);
			assertEquals(todoList.contains(2), true);
			assertEquals(todoList.contains(3), true);
			assertEquals(todoList.contains(4), true);
			
			// done list should have two tasks
			assertEquals(doneList.contains(5), true);
			assertEquals(doneList.contains(6), true);
		} catch(IDNotFoundException IDnfe){
			// failed the task check
			assertEquals(false, true);
		}
		
		// first task - no parameters
		String task = todoList.displayID(1);
		assertEquals(task, "1. task2 | ");
		
		// second task - location parameter
		task = todoList.displayID(2);
		assertEquals(task, "2. task3 | location: room4 | ");
		
		// third task - category parameter
		task = todoList.displayID(3);
		assertEquals(task, "3. task6 | category: 7th");
		
		// fourth task - additional info
		task = todoList.displayID(4);
		assertEquals(task, "4. task7 |  | additional information: testing");
		
		// fifth task - priority parameter
		task = doneList.displayID(5);
		assertEquals(task, "5. task5 | priority: 2 | ");
		
		// sixth task - deadline parameter
		task = doneList.displayID(6);
		assertEquals(task, "6. task8 | deadline: Wed, 23 Mar 17:00 | ");
		
	}
	
	// to add: save tests
	
}
