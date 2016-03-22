package tucklife.storage;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import tucklife.parser.ProtoTask;

public class ListStorage {
	
	private String targetFile;
	private TaskList list;
	private boolean hasLoadedCorrectly, hasSavedCorrectly;
	
	protected ListStorage(String fileName){
		targetFile = fileName;
		list = new TaskList();
		hasLoadedCorrectly = false;
		hasSavedCorrectly = false;
	}
	
	// loads the required TaskList (todo or done list) - returns true if loaded ok - false if not
	private boolean loadList(){
		
		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
		
		try{
			fis = new FileInputStream(targetFile);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
			while(br.ready()){
				String nextTask = br.readLine();
				
				if(!nextTask.equals("")){
					ProtoTask pt = parseTask(nextTask);
					
					list.add(pt);
				}			
			}
			
			br.close();
			isr.close();
			fis.close();
			
			hasLoadedCorrectly = true;
			
		} catch(IOException ioe){
			hasLoadedCorrectly = false;
		}
		
		return hasLoadedCorrectly;
	}
	
	// converts string version of task into ProtoTask form
	private ProtoTask parseTask(String task){
		ProtoTask pt = new ProtoTask("add");
		
		String[] taskDetails = task.split("\\|");
		
		pt.setTaskDesc(taskDetails[0].trim());
		
		for(int i = 1; i < taskDetails.length; i++){
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
				try{
					SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM HH:mm");
					Calendar c = Calendar.getInstance();
					c.setTime(sdf.parse(field.substring(10)));
					pt.setEndDate(c);
				
				// date is wrong - ignore it for now	
				} catch(ParseException pe){
					// whatever - wrong date is the same as no date
				}
				
			} else if(fieldHeader.equalsIgnoreCase("start:")){
				// event date loading TBC
				
			}						
		}
		
		return pt;
	}
	
	// for use by main ExternalStorage
	protected TaskList getList(){
		
		hasLoadedCorrectly = false;
		loadList();
		
		if(hasLoadedCorrectly){
			return list;
		} else{
			return new TaskList();
		}
	}
	
	// status of load
	protected boolean getLoadStatus(){
		return hasLoadedCorrectly;
	}
	
	// exact opposite - saves the todo list and returns true on success
	private boolean saveList(String fileName){
		
		FileOutputStream fos;
		BufferedOutputStream bos;
		
		try{
			fos = new FileOutputStream(fileName);
			bos = new BufferedOutputStream(fos);
			
			Iterator<Task> tasks = list.iterator();
			
			while(tasks.hasNext()){
				Task t = tasks.next();
				
				String taskString = t.displayAll();
				int idBreak = taskString.indexOf((int) ' ');
				
				taskString = taskString.substring(idBreak + 1);
				
				bos.write(taskString.getBytes());
				bos.write("\n".getBytes());
			}
			
			bos.flush();
			bos.close();
			fos.close();
			
			hasSavedCorrectly = true;
			
		} catch(IOException ioe){
			hasSavedCorrectly = false;
		}
		
		return hasSavedCorrectly;
	}
	
	// normal saving - for use by ExternalStorage
	protected boolean normalSave(TaskList listToSave){
		hasSavedCorrectly = false;
		list = listToSave;
		return saveList(targetFile);
	}
	
	// used when save path is changed
	protected boolean pathSave(String newTargetFile, TaskList listToSave){
		hasSavedCorrectly = false;
		list = listToSave;
		saveList(newTargetFile);
		
		if(hasSavedCorrectly){
			targetFile = newTargetFile;
		}
		
		return hasSavedCorrectly;
	}
}
