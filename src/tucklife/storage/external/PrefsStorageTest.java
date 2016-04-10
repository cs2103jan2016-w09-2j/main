// @@author A0121352X
package tucklife.storage.external;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;


public class PrefsStorageTest {
	
	private PrefsStorage psNormal, psTester;
	
	private static final String TEST_PATH = "test\\";
	private static final String TEST_NEWPATH_RELATIVE = "test\\relative\\";
	private static final String TEST_NEWPATH_GLOBAL = "C:\\Users\\Ryan\\Desktop\\Holding Area\\";
	private static final String FILENAME_PREFS_TEST = "prefsTest.txt";
	private static final String FILENAME_PREFS = "prefs.txt";

	@Before
	public void setUp() throws Exception {
		psNormal = new PrefsStorage();
		psTester = new PrefsStorage(TEST_PATH);
	}
	
	// delete test\prefs.txt before starting
	// test for file creation/loading
	@Test
	public void createAndLoadTest(){
		// loading normal prefs.txt
		assertEquals(true, psNormal.loadPreferences());
		// creating a special test\prefs.txt
		assertEquals(true, psTester.loadPreferences());
	}
	
	// check that the used default values are okay 
	@Test
	public void defaultValuesTest() {
		assertEquals("", psTester.getSavePath());
		assertEquals(50, psTester.getOverloadLimit());
	}
	
	// save without changing anything
	@Test
	public void unchangedTest(){
		psTester.loadPreferences();		
		assertEquals(true, psTester.savePreferences());	
	}
	
	// modify the parameters that we use and see that they are saved correctly
	@Test
	public void saveTest(){
		psTester.loadPreferences();
		
		psTester.setOverloadLimit(31);
		psTester.setSavePath(TEST_NEWPATH_RELATIVE);
		
		assertEquals(true, psTester.savePreferences());
	}
	
	// check if loading an existing file works
	@Test
	public void loadValueTest(){
		// copy the base test file into test\prefs.txt 
		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
				
		FileOutputStream fos;
		BufferedOutputStream bos;
				
		try{
			fis = new FileInputStream(TEST_PATH + FILENAME_PREFS_TEST);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
					
			fos = new FileOutputStream(TEST_PATH + FILENAME_PREFS);
			bos = new BufferedOutputStream(fos);
					
			while(br.ready()){
				String nextLine = br.readLine();
				bos.write(nextLine.getBytes());
				bos.write("\n".getBytes());			
			}
					
			br.close();
			isr.close();
			fis.close();
					
			bos.close();
			fos.close();
		
		// should not happen if test file is present
		} catch(IOException ioe){
			
		}

		psTester.loadPreferences();
		
		assertEquals(TEST_NEWPATH_GLOBAL, psTester.getSavePath());
		assertEquals(74, psTester.getOverloadLimit());
	}

}
