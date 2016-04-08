// @@author A0121352X
package tucklife.storage;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import tucklife.parser.ProtoTask;

public class ListStorage {
	
	private static final Logger EXTERNAL_LOG = Logger.getLogger(ExternalStorage.class.getName());
	
	private String targetFile;
	private TaskList list;
	private boolean hasLoadedCorrectly, hasSavedCorrectly;
	
	private static final String HEADER_LOCATION = "Location:";
	private static final String HEADER_CATEGORY = "Category:";
	private static final String HEADER_PRIORITY = "Priority:";
	private static final String HEADER_DEADLINE = "By:";
	private static final String HEADER_ADDITIONAL = "Additional:";
	private static final String HEADER_EVENT_START = "From:";
	private static final String HEADER_QUEUE = "Q:";
	
	private static final String PRIORITY_HIGH = "High";
	private static final String PRIORITY_MEDIUM = "Med";
	private static final String PRIORITY_LOW = "Low";
	
	private static final String LOG_TASK_DATE_ERROR = "Error loading task dates";	
	private static final String LOG_TASK_NAME = "Loaded task name: $1%s";
	private static final String LOG_TASK_LOCATION = "Loaded task location: $1%s";
	private static final String LOG_TASK_CATEGORY = "Loaded task category: $1%s";
	private static final String LOG_TASK_PRIORITY = "Loaded task priority: $1%s";
	private static final String LOG_TASK_ADDITIONAL = "Loaded task additional: $1%s";
	private static final String LOG_TASK_QUEUE = "Loaded task queueNo: $1%s";
	private static final String LOG_TASK_END = "Loaded task end: $1%s";
	private static final String LOG_TASK_START = "Loaded task start: $1%s";
	private static final String LOG_SAVE_TASK = "Saved task: $1%s";
	private static final String LOG_SAVE_QUEUE = "Task queueNo: $1%s";
	
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
		
		assert(targetFile != null);
		
		try{
			fis = new FileInputStream(targetFile);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
			while(br.ready()){
				String nextTask = br.readLine();
				
				if(!nextTask.equals("")){
					ProtoTask pt = parseTask(nextTask);
					
					try {
						list.add(pt);
					} catch (invalidDateException e) {
						// should not happen if save was correct
						EXTERNAL_LOG.log(Level.WARNING, LOG_TASK_DATE_ERROR);		
					}
				}			
			}
			
			br.close();
			isr.close();
			fis.close();
			
			hasLoadedCorrectly = true;
		
		// either file not created yet or TuckLife/the files were moved
		// ignore as new files will be created on save
		} catch(FileNotFoundException fnfe){
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
		
		EXTERNAL_LOG.log(Level.FINER, String.format(LOG_TASK_NAME, taskDetails[0].trim()));
		
		for(int i = 1; i < taskDetails.length; i++){
			processField(pt, taskDetails[i].trim());
		}
		
		return pt;
	}
	
	private ProtoTask processField(ProtoTask pt, String field){
		String[] fieldDetails = field.split(" ");
		String fieldHeader = fieldDetails[0];
		
		if(fieldHeader.equalsIgnoreCase(HEADER_CATEGORY)){
			pt.setCategory(removeFirstWord(field));
			EXTERNAL_LOG.log(Level.FINER, String.format(LOG_TASK_CATEGORY, removeFirstWord(field)));
			
		} else if(fieldHeader.equalsIgnoreCase(HEADER_PRIORITY)){
			String p = fieldDetails[1];
			if(p.equalsIgnoreCase(PRIORITY_HIGH)){
				pt.setPriority(1);
				EXTERNAL_LOG.log(Level.FINER, String.format(LOG_TASK_PRIORITY, PRIORITY_HIGH));
			} else if(p.equalsIgnoreCase(PRIORITY_MEDIUM)){
				pt.setPriority(2);
				EXTERNAL_LOG.log(Level.FINER, String.format(LOG_TASK_PRIORITY, PRIORITY_MEDIUM));
			} else if(p.equalsIgnoreCase(PRIORITY_LOW)){
				pt.setPriority(3);
				EXTERNAL_LOG.log(Level.FINER, String.format(LOG_TASK_PRIORITY, PRIORITY_LOW));
			}
			
		} else if(fieldHeader.equalsIgnoreCase(HEADER_LOCATION)){
			pt.setLocation(removeFirstWord(field));
			EXTERNAL_LOG.log(Level.FINER, String.format(LOG_TASK_LOCATION, removeFirstWord(field)));
			
		} else if(fieldHeader.equalsIgnoreCase(HEADER_ADDITIONAL)){
			pt.setAdditional(removeFirstWord(field));
			EXTERNAL_LOG.log(Level.FINER, String.format(LOG_TASK_ADDITIONAL, removeFirstWord(field)));
			
		} else if(fieldHeader.equalsIgnoreCase(HEADER_DEADLINE)){
			processTime(false, pt, field);
		} else if(fieldHeader.equalsIgnoreCase(HEADER_EVENT_START)){
			processTime(true, pt, field);
		} else if(fieldHeader.equalsIgnoreCase(HEADER_QUEUE)){
			pt.setPosition(Integer.parseInt(removeFirstWord(field)));
			EXTERNAL_LOG.log(Level.FINER, String.format(LOG_TASK_QUEUE, Integer.parseInt(removeFirstWord(field))));
		}		
		return pt;
	}
	
	private ProtoTask processTime(boolean event, ProtoTask pt, String field){
		
		if(event){
			String[] fieldDetails = field.split(" ");
			StringBuilder startDate = new StringBuilder();
			StringBuilder endDate = new StringBuilder();
			
			for(int j = 1; j <= 5; j++){
				startDate.append(fieldDetails[j] + " ");
			}
			
			for(int j = 7; j <= 11; j++){
				endDate.append(fieldDetails[j] + " ");
			}
			
			String sDate = startDate.toString();
			String eDate = endDate.toString();
			
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
				Calendar cStart = Calendar.getInstance();
				Calendar cEnd = Calendar.getInstance();
				cStart.setTime(sdf.parse(sDate));
				cEnd.setTime(sdf.parse(eDate));
				
				pt.setStartDate(cStart);
				pt.setEndDate(cEnd);
				
				EXTERNAL_LOG.log(Level.FINER, String.format(LOG_TASK_START, cStart.getTime().toString()));
				EXTERNAL_LOG.log(Level.FINER, String.format(LOG_TASK_END, cEnd.getTime().toString()));
			
			// dates is wrong - ignore them	
			} catch(ParseException pe){
				// do nothing - wrong date is the same as no date
			}
			
		} else{
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
				Calendar c = Calendar.getInstance();
				c.setTime(sdf.parse(removeFirstWord(field)));
				
				pt.setEndDate(c);
				
				EXTERNAL_LOG.log(Level.FINER, String.format(LOG_TASK_END, c.getTime().toString()));
			
			// date is wrong - ignore it	
			} catch(ParseException pe){
				// do nothing - wrong date is the same as no date
			}
		}
		
		return pt;
	}
	
	private String removeFirstWord(String s){
		for(int i = 0; i < s.length(); i++){
			if(s.charAt(i) == ' '){
				return s.substring(i+1);
			}
		}	
		return s;
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
				
				EXTERNAL_LOG.log(Level.FINER, String.format(LOG_SAVE_TASK, taskString));
				
				if(t.getQueueID() != -1){
					bos.write((" | " + HEADER_QUEUE + " " + Integer.toString(t.getQueueID())).getBytes());
					EXTERNAL_LOG.log(Level.FINER, String.format(LOG_SAVE_QUEUE, Integer.toString(t.getQueueID())));
				}
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
