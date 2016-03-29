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
		assertEquals(es.load(), true);
	}
	
	@Test
	public void DataBoxLoadTest(){
		es.load();
		DataBox db = es.getLoadedData();
		
		TaskList[] lists = db.getLists();
		assertEquals(lists.length, 2);
	}
	
	@Test
	public void DataBoxSaveTest(){
		es.load();
		DataBox db = es.getLoadedData();
		
		assertEquals(es.saveData(db), "Data saved successfully.");
	}
	
	@Test
	public void DataBoxSaveToTest(){
		es.load();
		DataBox db = es.getLoadedData();
		
		assertEquals(es.saveTo(db, "C:\\Users\\Ryan\\Desktop\\Holding Area\\"), "Data saved successfully.");
	}

}
