package tucklife.storage;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PrefsStorageTest {
	
	private PrefsStorage ps;

	@Before
	public void setUp() throws Exception {
		ps = new PrefsStorage();
	}

	@Test
	public void defaultValuesTest() {
		assertEquals(ps.getDefPriority(), -1);
		assertEquals(ps.getDefAdditional(), "");
		assertEquals(ps.getReminderStatus(), true);
	}
	
	// remove prefs.txt before running to check for file creation
	@Test
	public void loadTest(){
		assertEquals(ps.loadPreferences(), true);
	}
	
	// use the test prefs.txt to check for value loading
	@Test
	public void valueTest(){
		ps.loadPreferences();
		
		assertEquals(ps.getDefAdditional(), "Default additional information");
		assertEquals(ps.getDefCategory(), "DefCat");
		assertEquals(ps.getDefPriority(), 2);
		assertEquals(ps.getDefLocation(), "");
		assertEquals(ps.getReminderStatus(), false);
		
		// to replace this string when testing folder is put in
		assertEquals(ps.getSavePath(), "C:\\Users\\Ryan\\Desktop\\Holding Area\\");
		
	}
	
	@Test
	public void modifyTest(){
		ps.loadPreferences();
		
		ps.setDefLocation("TestLoc");
		ps.setOverloadLimit(25);
		ps.setDefPriority(3);
		ps.setReminderOn(true);
		
		// to replace this string also
		ps.setSavePath("C:\\Users\\Ryan\\Desktop\\Holding Area\\test\\");
		
		assertEquals(ps.getDefLocation(), "TestLoc");
		assertEquals(ps.getOverloadLimit(), 25);
		assertEquals(ps.getDefPriority(), 3);
		assertEquals(ps.getReminderStatus(), true);
		assertEquals(ps.getSavePath(), "C:\\Users\\Ryan\\Desktop\\Holding Area\\test\\");
	}
	
	// unfortunately file needs to be visually inspected
	@Test
	public void saveTest(){
		ps.loadPreferences();
		
		ps.setDefLocation("TestLoc");
		ps.setOverloadLimit(500);
		ps.setDefPriority(3);
		ps.setReminderOn(true);
		
		// to replace this string also
		ps.setSavePath("C:\\Users\\Ryan\\Desktop\\Holding Area\\test\\");
		
		assertEquals(ps.savePreferences(), true);
	}

}
