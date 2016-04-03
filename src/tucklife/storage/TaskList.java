package tucklife.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import tucklife.parser.ProtoTask;


public class TaskList {
	
	private static final Logger log = Logger.getLogger( Storage.class.getName() );

	private ArrayList<Task> taskList;
	
	private static final String HEADER_ID = "ID";
	private static final String HEADER_NAME = "Name";
	private static final String HEADER_LOCATION = "Location";
	private static final String HEADER_CATEGORY = "Category";
	private static final String HEADER_PRIORITY = "Priority";
	private static final String HEADER_DEADLINE = "By";
	private static final String HEADER_ADDITIONAL = "Additional";
	private static final String HEADER_EVENT_START = "From";
	private static final String HEADER_EVENT_END = " To";
	
	public TaskList() {		
		taskList = new ArrayList<Task>();
	}
	
	protected boolean contains(int taskID){
		boolean containsID = false;
		for (Task task:taskList) {
			if (hasFoundID(taskID, task)) {
				containsID = true;
			}
		}
		return containsID;
	}
	
	protected String displayID(int taskID) {
		String displayString = "";
		for (Task task:taskList) {
			if (hasFoundID(taskID, task)) {
				displayString = task.displayAll();
			}
		}
		return displayString;
	}
	
	protected String display() {
		StringBuilder sb = new StringBuilder();
		
		for (Task task:taskList) {
			sb.append(task.display());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	protected String displayDefault() {
		StringBuilder sb = new StringBuilder();
		boolean qflag = false;
		for (Task task:taskList) {
			if (task.getQueueID() == -1 && !qflag) {
				return display();
			}
			if(task.getQueueID() != -1 && !qflag) {
				sb.append("Queue:\n");
				qflag = true;
			}
			if(task.getQueueID() == -1 && qflag) {
				sb.append("\nOther Tasks:\n");
				qflag = false;
			}
			sb.append(task.display());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	protected String search(String searchKey) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Exact Match\n");
		for (Task task:taskList) {
			if(task.containsExact(searchKey)) {
				sb.append(task.displayAll());
				sb.append("\n");
			}
		}
		
		sb.append("\nPartial Match\n");
		for (Task task:taskList) {
			if(task.containsPartial(searchKey)) {
				sb.append(task.displayAll());
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	protected void add(ProtoTask task) {
		Task newTask = new Task(task);
		taskList.add(newTask);
		log.log( Level.FINE, "{0} added to tasklist via ProtoTask", newTask.getName());
	}
	
	protected void add(Task task) {
		taskList.add(task);
		log.log( Level.FINE, "{0} added to tasklist via Task", task.getName());
	}
	
	protected void add(int index, Task task) {
		taskList.add(index, task);
		log.log( Level.FINE, "{0} added to tasklist via index:{1} and Task", new Object[]{task.getName(), index});
	}
	
	protected int size() {
		return taskList.size();
	}
	
	protected Task delete(int taskID) {
		Task removed = null; 
		for (Task task:taskList) {
			if (hasFoundID(taskID, task)) {
				removed = task;
			}
		}
		if (taskList.remove(removed)) {
			log.log( Level.FINE, "{0} has been removed", removed.getName());
		}
		return removed;
	}
	
	protected Task remove(int index) {
		Task t = taskList.remove(index);
		log.log( Level.FINE, "{0} has been removed", t.getName());
		return t;
	}
	
	protected void edit(int taskID, ProtoTask toEditTask) {
		for (Task task:taskList) {
			if (hasFoundID(taskID, task)) {
				int taskIndex = taskList.indexOf(task);
				Task newTask = task.edit(toEditTask);
				taskList.set(taskIndex, newTask);
				log.log( Level.FINE, "{0} has been edited", newTask.getName());
			}
		}
	}
	
	protected Task get(int taskID) {
		Task getTask = null; 
		for (Task task:taskList) {
			if (hasFoundID(taskID, task)) {
				getTask = task;
			}
		}
		return getTask;
	}

	private boolean hasFoundID(int taskID, Task task) {
		return task.getId() == taskID;
	}
	
	public Iterator<Task> iterator() {
		return taskList.iterator();
	}
	
	protected void sort(String sortBy , boolean isAscending) {
		if (sortBy != null) {
			if (sortBy.equals("@")) {
				Collections.sort(taskList,new taskComparators().new ComparatorLocation());
				log.log( Level.FINE, "tasklist has been sorted by location");
			}
			
			if (sortBy.equals("!")) {
				Collections.sort(taskList,new taskComparators().new ComparatorPriority());
				log.log( Level.FINE, "tasklist has been sorted by priority");
			}
			
			if (sortBy.equals("#")) {
				Collections.sort(taskList,new taskComparators().new ComparatorCategory());
				log.log( Level.FINE, "tasklist has been sorted by category");
			}
			
			if (sortBy.equals("$")) {
				Collections.sort(taskList,new taskComparators().new ComparatorTime());
				log.log( Level.FINE, "tasklist has been sorted by time");
			}
			
			if (sortBy.equals("+")) { //is there actually a point doing this?? Im setting it to time for now
				Collections.sort(taskList,new taskComparators().new ComparatorTime());
				log.log( Level.FINE, "tasklist has been sorted by time");
			}
			
			if (sortBy.equals("&")) {
				Collections.sort(taskList,new taskComparators().new ComparatorAdditional());
				log.log( Level.FINE, "tasklist has been sorted by additional information");
			}
			
			if (!isAscending) {
				Collections.reverse(taskList);
				log.log( Level.FINE, "tasklist has been sorted in reverse order");
			}
		}
		else {
			Collections.sort(taskList,new taskComparators().new ComparatorDefault());
			log.log( Level.FINE, "tasklist has been sorted by queue number, then by time");
			//Collections.reverse(taskList);
		}
	}
	
}
