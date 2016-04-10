// @@author A0121352X
package tucklife.storage.external;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class PrefsStorage {
	
	private static final String FILENAME_PREFS = "prefs.txt";
	private static final String FILE_BLANK = "Preferences:\n\n50";
	private static final String FILE_HEADER = "Preferences:";
	
	private String testPath;
	private String savePath;
	private int overloadLimit;
//	private boolean reminderOn;
//	
//	private String defLocation;
//	private int defPriority;
//	private String defCategory;
//	private String defAdditional;
	
	public PrefsStorage(){
		// default values
		savePath = "";
		overloadLimit = 50;
		testPath = "";
		
//		reminderOn = true;
//		
//		defLocation = "";
//		defPriority = -1;
//		defCategory = "";
//		defTime = "";
//		defAdditional = "";
//		
//		testPath = "";
	}
	
	// special constructor meant for testing only
	protected PrefsStorage(String path){
		// default values
		savePath = "";
		overloadLimit = 50;
		testPath = path;
		
//		reminderOn = true;		
//		defLocation = "";
//		defPriority = -1;
//		defCategory = "";
//		defTime = "";
//		defAdditional = "";
	}
	
	protected boolean loadPreferences(){
			
		// check for existence of prefs file.
		try{
			boolean loaded = loadPrefs();
			return loaded;
		
		// file not found - create a blank one now
		} catch(FileNotFoundException fnfe){
			boolean loaded = writeBlankPrefs();	
			return loaded;
		}
	}
	
	private boolean loadPrefs() throws FileNotFoundException{
		try{
			FileInputStream fis = new FileInputStream(testPath + FILENAME_PREFS);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			
			/* 
			 * format for prefs file
			 * line 0 - Preferences:
			 * line 1 - savePath
			 * line 2 - overload limit
			 */
			
			// remove first line
			br.readLine();
			
			savePath = br.readLine();
			overloadLimit = Integer.parseInt(br.readLine());
			
//			defLocation = br.readLine();
//			defPriority = Integer.parseInt(br.readLine());
//			defCategory = br.readLine();
//			defTime = br.readLine();
//			defAdditional = br.readLine();
//					
//			reminderOn = Boolean.parseBoolean(br.readLine());
			
			br.close();
			isr.close();
			fis.close();
			
			return true;
		
		// signal that a new blank prefs.txt is needed
		} catch(FileNotFoundException fnfe){
			throw fnfe;
			
		} catch(IOException ioe){
			return false;
		}
	}
	
	private boolean writeBlankPrefs(){
		try{
			FileOutputStream fos = new FileOutputStream(testPath + FILENAME_PREFS);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			// write blank preferences
			bos.write(FILE_BLANK.getBytes());
			
			bos.close();
			fos.close();
		
		// should not happen under normal use
		} catch(FileNotFoundException fnfe){
			return false;
			
		} catch(IOException ioe){
			return false;
		}
		
		return true;
	}
	
	protected boolean savePreferences(){
		try{
			FileOutputStream fos = new FileOutputStream(testPath + FILENAME_PREFS);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			/* 
			 * format for prefs file
			 * line 0 - Preferences:
			 * line 1 - savePath
			 * line 2 - overload limit
			 */
			
			// write preferences to file
			bos.write(FILE_HEADER.getBytes());
			bos.write("\n".getBytes());
			
			bos.write(savePath.getBytes());
			bos.write("\n".getBytes());
			
			bos.write(Integer.toString(overloadLimit).getBytes());
			bos.write("\n".getBytes());
			
//			bos.write(defLocation.getBytes());
//			bos.write("\n".getBytes());
//			
//			bos.write(Integer.toString(defPriority).getBytes());
//			bos.write("\n".getBytes());
//			
//			bos.write(defCategory.getBytes());
//			bos.write("\n".getBytes());
//			
//			bos.write(defTime.getBytes());
//			bos.write("\n".getBytes());
//			
//			bos.write(defAdditional.getBytes());
//			bos.write("\n".getBytes());
//			
//			bos.write(Boolean.toString(reminderOn).getBytes());
//			bos.write("\n".getBytes());
//			
			bos.close();
			fos.close();
		
		// should not happen unless file was deleted after startup
		} catch(FileNotFoundException fnfe){
			return false;
			
		} catch(IOException ioe){
			return false;
		}
		
		return true;
	}
	
	protected void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public void setOverloadLimit(int overloadLimit) {
		this.overloadLimit = overloadLimit;
	}
	
	protected String getSavePath(){
		return savePath;
	}
	
	public int getOverloadLimit(){
		return overloadLimit;
	}
	
	// @@author A0121352X-unused
//	// these default parameters were to be used by an abandoned feature
//	protected void setReminderOn(boolean reminderOn) {
//		this.reminderOn = reminderOn;
//	}
//
//	protected void setDefLocation(String defLocation) {
//		this.defLocation = defLocation;
//	}
//
//	protected void setDefPriority(int defPriority) {
//		this.defPriority = defPriority;
//	}
//
//	protected void setDefCategory(String defCategory) {
//		this.defCategory = defCategory;
//	}
//
//	protected void setDefAdditional(String defAdditional) {
//		this.defAdditional = defAdditional;
//	}
//	
//	protected boolean getReminderStatus(){
//		return reminderOn;
//	}
//	
//	protected String getDefLocation(){
//		return defLocation;
//	}
//	
//	protected int getDefPriority(){
//		return defPriority;
//	}
//	
//	protected String getDefCategory(){
//		return defCategory;
//	}
//	
//	protected String getDefAdditional(){
//		return defAdditional;
//	}
}

