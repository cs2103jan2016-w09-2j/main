package tucklife.storage;

import java.util.ArrayList;
import java.util.Iterator;

import tucklife.parser.ProtoTask;

public class TaskList {

	private ArrayList<Task> taskList;
	
	public TaskList() {		
		taskList = new ArrayList<Task>();
	}
	
	protected boolean contains(int taskID) {
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
	
	protected Task delete(int taskID){
		Task removed = null; 
		for (Task task:taskList) {
			if (hasFoundID(taskID, task)) {
				removed = task;
				//taskList.remove(task);
			}
		}
		taskList.remove(removed);
		return removed;
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

	private boolean hasFoundID(int taskID, Task task) {
		return task.getId() == taskID;
	}
	
	public Iterator<Task> iterator(){
		return taskList.iterator();
	}
	
}
