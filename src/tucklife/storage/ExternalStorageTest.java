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
		
		assertEquals("Data saved successfully.", es.saveData(db));
	}
	
	@Test
	public void DataBoxSaveToTest(){
		es.load();
		DataBox db = es.getLoadedData();
		
		assertEquals("Data saved successfully.", es.saveTo(db, "C:\\Users\\Ryan\\Desktop\\Holding Area\\"));
	}

}
