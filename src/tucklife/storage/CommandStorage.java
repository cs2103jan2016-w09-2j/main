// @@author A0121352X
package tucklife.storage;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;

public class CommandStorage {
	
	private static final String FILENAME_COMMANDS = "commands.txt";
	
	private Hashtable<String, String> commandTable;
	
	private String[] commandTypes = { "add", "change", "complete", "delete", "demo", "display", "displaydone",
									  "edit", "exit", "help", "queue", "redo",
									  "save", "saveto", "setlimit", "undo" };
	
	public CommandStorage() {
		commandTable = new Hashtable<String, String>();
	}
	
	public boolean loadCommands() {
		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
		
		try {
			// Create new commands file if it does not exist
			if (!Files.exists(Paths.get(FILENAME_COMMANDS))) {
				Files.createFile(Paths.get(FILENAME_COMMANDS));
			}
			
			fis = new FileInputStream(FILENAME_COMMANDS);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
			String[] nextLine;
			
			while(br.ready()){
				nextLine = br.readLine().split(",");
				commandTable.put(nextLine[0], nextLine[1]);
			}
			
			br.close();
			isr.close();
			fis.close();
			
		} catch(IOException ioe){
			ioe.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean saveCommands(Hashtable<String, String> ht) {
		try {
			FileOutputStream fos = new FileOutputStream(FILENAME_COMMANDS);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			// Write custom commands to file
			for (String key:commandTypes) {
				if (ht.containsKey(key)) {
					String newEntry = key + "," + ht.get(key) + "\n";
					bos.write(newEntry.getBytes());
				}
			}
			
			bos.close();
			fos.close();
		
		// Should not happen under any circumstance
		} catch(FileNotFoundException fnfe){
			return false;
			
		} catch(IOException ioe){
			return false;
		}
		
		return true;
	}
	
	public Hashtable<String, String> getCommands() {
		return commandTable;
	}
}
