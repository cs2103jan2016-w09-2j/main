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

import tucklife.parser.ProtoTask;

public class ListStorage {
	
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
					
					try {
						list.add(pt);
					} catch (invalidDateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
			}
			
			br.close();
			isr.close();
			fis.close();
			
			hasLoadedCorrectly = true;
		
		// either file not created yet or TuckLife was moved
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
		
		for(int i = 1; i < taskDetails.length; i++){
			String field = taskDetails[i].trim();
			String[] fieldDetails = field.split(" ");
			String fieldHeader = fieldDetails[0];
			
			if(fieldHeader.equalsIgnoreCase(HEADER_CATEGORY)){
				pt.setCategory(removeFirstWord(field));
				
			} else if(fieldHeader.equalsIgnoreCase(HEADER_PRIORITY)){
				String p = fieldDetails[1];
				if(p.equalsIgnoreCase(PRIORITY_HIGH)){
					pt.setPriority(1);
				} else if(p.equalsIgnoreCase(PRIORITY_MEDIUM)){
					pt.setPriority(2);
				} else if(p.equalsIgnoreCase(PRIORITY_LOW)){
					pt.setPriority(3);
				}
				
			} else if(fieldHeader.equalsIgnoreCase(HEADER_LOCATION)){
				pt.setLocation(removeFirstWord(field));
				
			} else if(fieldHeader.equalsIgnoreCase(HEADER_ADDITIONAL)){
				pt.setAdditional(removeFirstWord(field));
				
			} else if(fieldHeader.equalsIgnoreCase(HEADER_DEADLINE)){
				try{
					SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
					Calendar c = Calendar.getInstance();
					c.setTime(sdf.parse(removeFirstWord(field)));
					pt.setEndDate(c);
				
				// date is wrong - ignore it	
				} catch(ParseException pe){
					// do nothing - wrong date is the same as no date
				}
				
			} else if(fieldHeader.equalsIgnoreCase(HEADER_EVENT_START)){
				
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
				
				// dates is wrong - ignore them	
				} catch(ParseException pe){
					// do nothing - wrong date is the same as no date
				}
				
			} else if(fieldHeader.equalsIgnoreCase(HEADER_QUEUE)){
				pt.setPosition(Integer.parseInt(removeFirstWord(field)));
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
				
				if(t.getQueueID() != -1){
					bos.write((" | " + HEADER_QUEUE + " " + Integer.toString(t.getQueueID())).getBytes());
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
