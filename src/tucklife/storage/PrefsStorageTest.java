// @@author A0121352X
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
		assertEquals(-1, ps.getDefPriority());
		assertEquals("", ps.getDefAdditional());
		assertEquals(true, ps.getReminderStatus());
	}
	
	// remove prefs.txt before running to check for file creation
	@Test
	public void loadTest(){
		assertEquals(true, ps.loadPreferences());
	}
	
	// use the test prefs.txt to check for value loading
	@Test
	public void valueTest(){
		ps.loadPreferences();
		
		assertEquals("Default additional information", ps.getDefAdditional());
		assertEquals("DefCat", ps.getDefCategory());
		assertEquals(2, ps.getDefPriority());
		assertEquals("", ps.getDefLocation());
		assertEquals(false, ps.getReminderStatus());
		
		// to replace this string when testing folder is put in
		assertEquals("C:\\Users\\Ryan\\Desktop\\Holding Area\\", ps.getSavePath());
		
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
		
		assertEquals("TestLoc", ps.getDefLocation());
		assertEquals(25, ps.getOverloadLimit());
		assertEquals(3, ps.getDefPriority());
		assertEquals(true, ps.getReminderStatus());
		assertEquals("C:\\Users\\Ryan\\Desktop\\Holding Area\\test\\", ps.getSavePath());
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
		
		assertEquals(true, ps.savePreferences());
	}

}
