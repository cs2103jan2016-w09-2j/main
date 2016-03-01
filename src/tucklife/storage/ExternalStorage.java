package tucklife.storage;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import tucklife.parser.ProtoTask;

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
				ProtoTask pt = new ProtoTask("add");
				
				String[] taskDetails = nextTask.split("|");
				
				pt.setTaskDesc(taskDetails[1].trim());
				
				for(int i = 2; i < taskDetails.length; i++){
					String field = taskDetails[i].trim();
					String[] fieldDetails = field.split(" ");
					String fieldHeader = fieldDetails[0];
					
					if(fieldHeader.equalsIgnoreCase("category:")){
						pt.setCategory(field.substring(10));
					} else if(fieldHeader.equalsIgnoreCase("priority:")){
						int p = Integer.parseInt(fieldDetails[1]);
						pt.setPriority(p);
					} else if(fieldHeader.equalsIgnoreCase("location:")){
						pt.setLocation(field.substring(10));
					} else if(fieldHeader.equalsIgnoreCase("additional")){
						pt.setAdditional(field.substring(24));
					} else if(fieldHeader.equalsIgnoreCase("deadline:")){
						// date loading TBC
						
					} else if(fieldHeader.equalsIgnoreCase("start:")){
						// date loading TBC
						
					}						
				}
				
				list.add(pt);
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
				Task t = tasks.next();
				
				bos.write(t.displayAll().getBytes());
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
