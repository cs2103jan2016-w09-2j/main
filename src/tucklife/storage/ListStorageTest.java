// @@author A0121352X
package tucklife.storage;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;


public class ListStorageTest {
	
	private ListStorage todo, done;
	private TaskList todoList, doneList;
	
	private static final String TEST_PATH = "test\\";
	private static final String TEST_PATH_RELATIVE = "test\\relative\\";
	private static final String FILENAME_TODO = "todo.txt";
	private static final String FILENAME_DONE = "done.txt";
	private static final String FILENAME_TODO_TEST = "todoTest.txt";
	private static final String FILENAME_DONE_TEST = "doneTest.txt";
	private static final String FILENAME_TODO_RES = "todoRes.txt";
	private static final String FILENAME_DONE_RES = "doneRes.txt";
	
	@Before
	public void setUp() throws Exception {
		
		// make copies of the base test files
		FileInputStream fisTodo, fisDone;
		InputStreamReader isrTodo, isrDone;
		BufferedReader brTodo, brDone;
		
		FileOutputStream fosTodo, fosDone;
		BufferedOutputStream bosTodo, bosDone;
		
		Task.resetGlobalId();
		
		try{
			fisTodo = new FileInputStream(TEST_PATH + FILENAME_TODO_TEST);
			isrTodo = new InputStreamReader(fisTodo);
			brTodo = new BufferedReader(isrTodo);
			
			fosTodo = new FileOutputStream(TEST_PATH + FILENAME_TODO);
			bosTodo = new BufferedOutputStream(fosTodo);
			
			while(brTodo.ready()){
				String nextTask = brTodo.readLine();
				
				if(!nextTask.equals("")){
					bosTodo.write(nextTask.getBytes());
					bosTodo.write("\n".getBytes());
				}			
			}
			
			brTodo.close();
			isrTodo.close();
			fisTodo.close();
			
			bosTodo.close();
			fosTodo.close();
			
			fisDone = new FileInputStream(TEST_PATH + FILENAME_DONE_TEST);
			isrDone = new InputStreamReader(fisDone);
			brDone = new BufferedReader(isrDone);
			
			fosDone = new FileOutputStream(TEST_PATH + FILENAME_DONE);
			bosDone = new BufferedOutputStream(fosDone);
			
			while(brDone.ready()){
				String nextTask = brDone.readLine();
				
				if(!nextTask.equals("")){
					bosDone.write(nextTask.getBytes());
					bosDone.write("\n".getBytes());
				}			
			}
			
			brDone.close();
			isrDone.close();
			fisDone.close();
			
			bosDone.close();
			fosDone.close();
		
		// should not happen if test files are there - this will cause tests to fail
		} catch(IOException ioe){
			assert(true == false);
		}
	
		todo = new ListStorage(TEST_PATH + FILENAME_TODO);
		done = new ListStorage(TEST_PATH + FILENAME_DONE);
		todoList = todo.getList();
		doneList = done.getList();
	}
	
	// checks if basic load function executed correctly
	@Test
	public void loadTest() {		
		assertEquals(true, todo.getLoadStatus());
		assertEquals(true, done.getLoadStatus());
	}
	
	// checks that each individual task type is handled correctly
	@Test
	public void taskTest(){
		
		// todo list should have eight tasks as test file has seven tasks
		// task5, 4, 11, 8, 6, 7, 3, 12
		
		assertEquals(true, todoList.contains(1));
		assertEquals(true, todoList.contains(2));
		assertEquals(true, todoList.contains(3));
		assertEquals(true, todoList.contains(4));
		assertEquals(true, todoList.contains(5));
		assertEquals(true, todoList.contains(6));
		assertEquals(true, todoList.contains(7));
		assertEquals(true, todoList.contains(8));
			
		// done list should have four tasks - task1, task9, task2, task10
		assertEquals(true, doneList.contains(9));
		assertEquals(true, doneList.contains(10));
		assertEquals(true, doneList.contains(11));
		assertEquals(true, doneList.contains(12));
		
		// first task type - no parameters
		String task = doneList.displayID(9);
		assertEquals(" 9. task1", task);
		
		// second task type - location parameter
		task = doneList.displayID(11);
		assertEquals("11. task2 | Location: loc2", task);
		
		// third task type - priority parameter
		task = todoList.displayID(7);
		assertEquals(" 7. task3 | Priority: High", task);
		
		// fourth task type - deadline parameter (day only)
		task = todoList.displayID(2);
		assertEquals(" 2. task4 | By: Mon, 4 Apr 2016 23:59", task);
		
		// fifth task type - deadline parameter (time only)
		task = todoList.displayID(1);
		assertEquals(" 1. task5 | By: Tue, 29 Mar 2016 05:00", task);
			
		// sixth task type - category parameter
		task = todoList.displayID(5);
		assertEquals(" 5. task6 | Category: cat6", task);
		
		// seventh task type - additional parameter
		task = todoList.displayID(6);
		assertEquals(" 6. task7 | Additional: additional7", task);
		
		// eighth task type - event (days only)
		task = todoList.displayID(4);
		assertEquals(" 4. task8 | From: Mon, 8 Aug 2016 23:59 To: Wed, 10 Aug 2016 23:59", task);
		
		// ninth task type - event (time only)
		task = doneList.displayID(10);
		assertEquals(" 10. task9 | From: Tue, 29 Mar 2016 09:00 To: Tue, 29 Mar 2016 10:00", task);
		
		// tenth task type - event (both date and time)
		task = doneList.displayID(12);
		assertEquals(" 12. task10 | From: Wed, 10 Aug 2016 22:00 To: Fri, 12 Aug 2016 23:00", task);
		
		// eleventh task type - queue ID
		task = todoList.displayID(8);
		assertEquals(" 8. task12", task);
		Task t = todoList.delete(8);
		assertEquals(1, t.getQueueID());
		
		// consolidation - all parameters
		task = todoList.displayID(3);
		assertEquals(" 3. task11 | By: Mon, 11 Apr 2016 23:59 | Location: loc11 | Priority: Med | Category: cat11 | Additional: additional11", task);
	}
	
	// check that you can save without any changes
	@Test
	public void saveUnchangedTest(){
		assertEquals(true, done.normalSave(doneList));
		assertEquals(true, todo.normalSave(todoList));
	}
	
	// check that you can save changes - files are named differently for the sake of comparison
	@Test
	public void saveChangedTest(){		
		// simulate a complete
		Task t = todoList.delete(4);
		doneList.add(t);
		
		// simulate a delete
		t = todoList.delete(3);
		
		// save and compare doneRes and todoRes with doneExpected and todoExpected	
		assertEquals(true, done.pathSave(TEST_PATH + FILENAME_DONE_RES, doneList));
		assertEquals(true, todo.pathSave(TEST_PATH + FILENAME_TODO_RES, todoList));
	}
	
	
	// checks that a relative saveto path works.
	@Test
	public void savetoRelativeTest(){			
		assertEquals(true, done.pathSave(TEST_PATH_RELATIVE + FILENAME_DONE, doneList));
		assertEquals(true, todo.pathSave(TEST_PATH_RELATIVE + FILENAME_TODO, todoList));
	}
	
	// unfortunately global saveto cannot be tested automatically 
	// without prior knowledge of computer running test
	
}
