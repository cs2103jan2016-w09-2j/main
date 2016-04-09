// @@author A0121352X
package tucklife.storage;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ExternalStorageTest {
	
	private ExternalStorage es;

	@Before
	public void setUp() throws Exception {
		es = new ExternalStorage();
	}

	@Test
	public void loadTest() {
		assertEquals(true, es.load());
	}
	
	@Test
	public void DataBoxLoadTest(){
		es.load();
		DataBox db = es.getLoadedData();
		
		TaskList[] lists = db.getLists();
		assertEquals(2, lists.length);
	}
	
	@Test
	public void DataBoxSaveTest(){
		es.load();
		DataBox db = es.getLoadedData();
		
		assertEquals("Files saved.", es.saveData(db));
	}
	
	@Test
	public void DataBoxSavetoSuccessTest(){
		es.load();
		DataBox db = es.getLoadedData();
		
		assertEquals("Files saved.", es.saveTo(db, "test\\"));
	}
	
//	@Test - cannot be run on Mac
//	public void DataBoxSavetoFailTest(){
//		es.load();
//		DataBox db = es.getLoadedData();
//		
//		assertEquals("Error saving files to new location. Files have been saved to previous location.", es.saveTo(db, "nyonexist\\"));
//	}

}
