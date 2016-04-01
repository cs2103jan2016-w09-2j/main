package tucklife.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import tucklife.parser.ProtoTask;


public class TaskList {

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
	
	protected void add(ProtoTask task) {
		Task newTask = new Task(task);
		taskList.add(newTask);
	}
	
	protected void add(Task task) {
		taskList.add(task);
	}
	
	protected void add(int index, Task task) {
		taskList.add(index, task);
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
		taskList.remove(removed);
		return removed;
	}
	
	protected Task remove(int index) {
		return taskList.remove(index);
	}
	
	protected void edit(int taskID, ProtoTask toEditTask) {
		for (Task task:taskList) {
			if (hasFoundID(taskID, task)) {
				int taskIndex = taskList.indexOf(task);
				Task newTask = task.edit(toEditTask);
				taskList.set(taskIndex, newTask);
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
			}
			
			if (sortBy.equals("!")) {
				Collections.sort(taskList,new taskComparators().new ComparatorPriority());
			}
			
			if (sortBy.equals("#")) {
				Collections.sort(taskList,new taskComparators().new ComparatorCategory());
			}
			
			if (sortBy.equals("$")) {
				Collections.sort(taskList,new taskComparators().new ComparatorTime());
			}
			
			if (sortBy.equals("+")) { //is there actually a point doing this?? Im setting it to time for now
				Collections.sort(taskList,new taskComparators().new ComparatorTime());
			}
			
			if (sortBy.equals("&")) {
				Collections.sort(taskList,new taskComparators().new ComparatorAdditional());
			}
			
			if (!isAscending) {
				Collections.reverse(taskList);
			}
		}
		else {
			Collections.sort(taskList,new taskComparators().new ComparatorDefault());
			//Collections.reverse(taskList);
		}
	}
	
}
