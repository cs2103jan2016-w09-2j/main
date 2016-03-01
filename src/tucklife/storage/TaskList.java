package tucklife.storage;

import java.util.ArrayList;
import java.util.Iterator;

import tucklife.parser.ProtoTask;

public class TaskList {

	ArrayList<Task> taskList;
	
	public TaskList() {		
		taskList = new ArrayList<Task>();
	}
	
	boolean contains(int taskID) {
		boolean containsID = false;
		for (Task task:taskList) {
			if (hasFoundID(taskID, task)) {
				containsID = true;
			}
		}
		return containsID;
	}
	
	String displayID(int taskID) {
		String displayString = "";
		for (Task task:taskList) {
			if (hasFoundID(taskID, task)) {
				displayString = task.displayAll();
			}
		}
		return displayString;
	}
	
	String display() {
		StringBuilder sb = new StringBuilder();
		for (Task task:taskList) {
			sb.append(task.display());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	void add(ProtoTask task) {
		Task newTask = new Task(task);
		taskList.add(newTask);
	}
	
	void add(Task task) {
		taskList.add(task);
	}
	
	Task delete(int taskID){
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
	
	//i think should be void. do checking external when receive command
	boolean edit(int taskID, ProtoTask toEditTask) {
		boolean edited = false;
		for (Task task:taskList) {
			if (hasFoundID(taskID, task)) {
				int taskIndex = taskList.indexOf(task);
				Task newTask = task.edit(toEditTask);
				taskList.set(taskIndex, newTask);
			}
		}
		return edited;
	}

	private boolean hasFoundID(int taskID, Task task) {
		return task.id == taskID;
	}
	
	public Iterator<Task> iterator(){
		return taskList.iterator();
	}
	
}
