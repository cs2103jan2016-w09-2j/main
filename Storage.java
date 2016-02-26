package cs2103tut;

import cs2103tut.Task;
import cs2103tut.TaskList;

public class Storage {
	
	static TaskList toDoList;
	static TaskList doneList;
	
	void parseCommand() {
		//parse
	}
	
	void add(Task task) {
		toDoList.add(task);
	}
	
	void edit(int taskID, Task toEditTask) {
		toDoList.edit(taskID, toEditTask);
	}
	
	void complete(int taskID) {
		Task completedTask = toDoList.delete(taskID);
		doneList.add(completedTask);
	}
	
	void delete(int taskID) {
		toDoList.delete(taskID);
	}
	
	String displayID(int taskID) {
		return toDoList.displayID(taskID);
	}
	
	String display() {
		return toDoList.display();
	}
	
	String displayDone() {
		return doneList.display();
	}
}
