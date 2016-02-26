package tucklife.storage;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

public class ExternalStorage {
	
	public static final String FILENAME_TODO = "todo.txt";
	public static final String FILENAME_DONE = "done.txt";
	public static final String FILENAME_RECUR = "recur.txt";
	public static final String FILENAME_PREFS = "prefs.txt";
	
	public static final String MSG_LOAD_COMPLETE = "Data loaded successfully.";
	public static final String MSG_SAVE_COMPLETE = "Data saved successfully.";
	
	public static final String ERROR_LOAD = "Error loading files from %1$s. New todo list has been created.";
	public static final String ERROR_SAVE = "Error saving files to %1$s. Files have been saved to default location.";
	
	private String targetFolder;
	private boolean hasLoaded;
	private TaskList[] lists;
	
	public ExternalStorage(){
		// load save-to path - assumption now is that file is in same directory as TuckLife.
		targetFolder = "";
		hasLoaded = true;
	}
	
	public boolean load(){
		lists = new TaskList[2];
		TaskList todo = loadList(targetFolder + FILENAME_TODO);
		TaskList done = loadList(targetFolder + FILENAME_DONE);
		
		lists[0] = todo;
		lists[1] = done;
		
		return hasLoaded;
	}
	
	public TaskList[] getLoadedLists(){
		return lists;
	}
	
	private TaskList loadList(String directory){
		
		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
		
		TaskList list = new TaskList();
		
		try{
			fis = new FileInputStream(directory);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
			while(br.ready()){
				String nextTask = br.readLine();
				
				Task t = new Task();
				list.add(t);
			}
			
			br.close();
			isr.close();
			fis.close();
			
			return list;
			
		} catch(IOException ioe){
			hasLoaded = false;
			return new TaskList();
		}
		
	}

	public String save(TaskList[] lists){		
		boolean todoStatus = saveList(lists[0], targetFolder + FILENAME_TODO);
		boolean doneStatus = saveList(lists[1], targetFolder + FILENAME_DONE);
		
		if(!todoStatus){
			saveList(lists[0], FILENAME_TODO);
			return String.format(ERROR_SAVE, targetFolder);
		}
		
		if(!doneStatus){
			saveList(lists[1], FILENAME_TODO);
			return String.format(ERROR_SAVE, targetFolder);
		}
		
		return MSG_SAVE_COMPLETE;
	}
	
	private boolean saveList(TaskList list, String fileName){
		
		FileOutputStream fos;
		BufferedOutputStream bos;
		
		try{
			fos = new FileOutputStream(fileName);
			bos = new BufferedOutputStream(fos);
			
			Iterator<Task> tasks = list.iterator();
			
			while(tasks.hasNext()){
				bos.write(tasks.next().toString().getBytes());
			}
			
			bos.flush();
			bos.close();
			fos.close();
			
			return true;
			
		} catch(IOException ioe){
			return false;
		}
	}
}
