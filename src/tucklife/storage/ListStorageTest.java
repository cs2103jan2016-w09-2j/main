package tucklife.storage;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ListStorageTest {
	
	private ListStorage todo, done;
	private TaskList todoList, doneList;
	
	private static final String TEST_PATH = "C:\\Users\\Ryan\\Desktop\\Holding Area\\test\\";
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
		assertEquals(true, todo.getLoadStatus());
		assertEquals(true, done.getLoadStatus());
	}
	
	// loading for various task types - run separately from saveTest
	@Test
	public void taskTest(){
		
		// todo list should have seven tasks as test file has seven tasks
		// task5, 4, 11, 8, 6, 7, 3
		
		assertEquals(true, todoList.contains(1));
		assertEquals(true, todoList.contains(2));
		assertEquals(true, todoList.contains(3));
		assertEquals(true, todoList.contains(4));
		assertEquals(true, todoList.contains(5));
		assertEquals(true, todoList.contains(6));
		assertEquals(true, todoList.contains(7));
			
		// done list should have four tasks - task1, task9, task2 and task10
		assertEquals(true, doneList.contains(8));
		assertEquals(true, doneList.contains(9));
		assertEquals(true, doneList.contains(10));
		assertEquals(true, doneList.contains(11));
		
		// first task type - no parameters
		String task = doneList.displayID(8);
		assertEquals("8. task1", task);
		
		// second task type - location parameter
		task = doneList.displayID(10);
		assertEquals("10. task2 | Location: loc2", task);
		
		// third task type - priority parameter
		task = todoList.displayID(7);
		assertEquals("7. task3 | Priority: High", task);
		
		// fourth task type - deadline parameter (day only)
		task = todoList.displayID(2);
		assertEquals("2. task4 | By: Mon, 4 Apr 2016 23:59", task);
		
		// fifth task type - deadline parameter (time only)
		task = todoList.displayID(1);
		assertEquals("1. task5 | By: Tue, 29 Mar 2016 05:00", task);
			
		// sixth task type - category parameter
		task = todoList.displayID(5);
		assertEquals("5. task6 | Category: cat6", task);
		
		// seventh task type - additional parameter
		task = todoList.displayID(6);
		assertEquals("6. task7 | Additional: additional7", task);
		
		// eighth task type - event (days only)
		task = todoList.displayID(4);
		assertEquals("4. task8 | From: Mon, 8 Aug 2016 23:59 To: Wed, 10 Aug 2016 23:59", task);
		
		// ninth task type - event (time only)
		task = doneList.displayID(9);
		assertEquals("9. task9 | From: Tue, 29 Mar 2016 09:00 To: Tue, 29 Mar 2016 10:00", task);
		
		// tenth task type - event (both date and time)
		task = doneList.displayID(11);
		assertEquals("11. task10 | From: Wed, 10 Aug 2016 22:00 To: Fri, 12 Aug 2016 23:00", task);
		
		// consolidation - all parameters
		task = todoList.displayID(3);
		assertEquals("3. task11 | By: Mon, 11 Apr 2016 23:59 | Location: loc11 | Priority: Med | Category: cat11 | Additional: additional11", task);
	}
	
	@Test // note - run separately from loadTest
	public void saveTest(){
		
		// simulate a complete
		Task t = todoList.delete(4);
		doneList.add(t);
		
		// simulate a delete
		t = todoList.delete(3);
		
		assertEquals(true, done.normalSave(doneList));
		assertEquals(true, todo.normalSave(todoList));
	}
	
}
