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
		assertEquals(todo.getLoadStatus(), true);
		assertEquals(done.getLoadStatus(), true);
	}
	
	// loading for various task types - run separately from saveTest
	@Test
	public void taskTest(){
		
		// todo list should have seven tasks as test file has seven tasks
		// task5, 4, 11, 8, 6, 7, 3
		
		assertEquals(todoList.contains(1), true);
		assertEquals(todoList.contains(2), true);
		assertEquals(todoList.contains(3), true);
		assertEquals(todoList.contains(4), true);
		assertEquals(todoList.contains(5), true);
		assertEquals(todoList.contains(6), true);
		assertEquals(todoList.contains(7), true);
			
		// done list should have four tasks - task1, task9, task2 and task10
		assertEquals(doneList.contains(8), true);
		assertEquals(doneList.contains(9), true);
		assertEquals(doneList.contains(10), true);
		assertEquals(doneList.contains(11), true);
		
		// first task type - no parameters
		String task = doneList.displayID(8);
		assertEquals(task, "8. task1");
		
		// second task type - location parameter
		task = doneList.displayID(10);
		assertEquals(task, "10. task2 | Location: loc2");
		
		// third task type - priority parameter
		task = todoList.displayID(7);
		assertEquals(task, "7. task3 | Priority: High");
		
		// fourth task type - deadline parameter (day only)
		task = todoList.displayID(2);
		assertEquals(task, "2. task4 | By: Mon, 4 Apr 2016 23:59");
		
		// fifth task type - deadline parameter (time only)
		task = todoList.displayID(1);
		assertEquals(task, "1. task5 | By: Tue, 29 Mar 2016 05:00");
			
		// sixth task type - category parameter
		task = todoList.displayID(5);
		assertEquals(task, "5. task6 | Category: cat6");
		
		// seventh task type - additional parameter
		task = todoList.displayID(6);
		assertEquals(task, "6. task7 | Additional: additional7");
		
		// eighth task type - event (days only)
		task = todoList.displayID(4);
		assertEquals(task, "4. task8 | From: Mon, 8 Aug 2016 23:59 To: Wed, 10 Aug 2016 23:59");
		
		// ninth task type - event (time only)
		task = doneList.displayID(9);
		assertEquals(task, "9. task9 | From: Tue, 29 Mar 2016 09:00 To: Tue, 29 Mar 2016 10:00");
		
		// tenth task type - event (both date and time)
		task = doneList.displayID(11);
		assertEquals(task, "11. task10 | From: Wed, 10 Aug 2016 22:00 To: Fri, 12 Aug 2016 23:00");
		
		// consolidation - all parameters
		task = todoList.displayID(3);
		assertEquals(task, "3. task11 | By: Mon, 11 Apr 2016 23:59 | Location: loc11 | Priority: Med | Category: cat11 | Additional: additional11");
	}
	
	@Test // note - run separately from loadTest
	public void saveTest(){
		
		// simulate a complete
		Task t = todoList.delete(4);
		doneList.add(t);
		
		// simulate a delete
		t = todoList.delete(3);
		
		assertEquals(done.normalSave(doneList), true);
		assertEquals(todo.normalSave(todoList), true);
	}
	
}
