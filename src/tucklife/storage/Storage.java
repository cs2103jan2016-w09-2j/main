package tucklife.storage;

import tucklife.parser.ProtoTask;
import tucklife.storage.TaskList;

public class Storage {
	
	private static final String RETURN_MESSAGE_FOR_ADD = "{%1$s} has been added to TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_EDIT = "{%1$s} has been edited in TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_DELETE = "{%1$s} has been deleted from TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_COMPLETE = "{%1$s} has been moved to TuckLife's done list!";
	
	
	static TaskList toDoList = new TaskList();
	static TaskList doneList = new TaskList();
	
	enum COMMAND_TYPE {
		ADD, DISPLAY, COMPLETE, DISPLAYDONE, DELETE, EDIT, INVALID
	}
	
	public String parseCommand(ProtoTask pt) {
		COMMAND_TYPE ct = determineCommandType(pt.getCommand());
		String returnMessage = parseCommand(pt,ct); 
		return returnMessage;
	}
	
	public TaskList[] save() {
		TaskList[] saveList = new TaskList[2];
		saveList[0] = toDoList;
		saveList[1] = doneList;
		return saveList;
	}
	
	public void load(TaskList[] loadList) {
		toDoList = loadList[0];
		doneList = loadList[1];
	}
	
	static COMMAND_TYPE determineCommandType(String commandTypeString) {
		if (commandTypeString.equalsIgnoreCase("add")) {
			return COMMAND_TYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase("complete")) {
			return COMMAND_TYPE.COMPLETE;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return COMMAND_TYPE.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("displaydone")) {
			return COMMAND_TYPE.DISPLAYDONE;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return COMMAND_TYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("edit")) {
			return COMMAND_TYPE.EDIT;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}
	
	private static String parseCommand(ProtoTask pt, COMMAND_TYPE commandType) throws Error {
		switch (commandType) {
		case ADD :
			return add(pt);
		case COMPLETE :
			return complete(pt.getId());
		case DISPLAY :
			return display();
		case DISPLAYDONE :
			return displayDone();
		case DELETE :
			return delete(pt.getId());
		case EDIT :
			return edit(pt.getId(), pt);
		default:
			//throw an error if the command is not recognized
			throw new Error("Unrecognized command type");
		}
	}
	
	static String add(ProtoTask task) {
		Task newTask = new Task(task);
		toDoList.add(newTask);
		return String.format(RETURN_MESSAGE_FOR_ADD, newTask.displayAll());
	}
	
	static String edit(int taskID, ProtoTask toEditTask) {
		toDoList.edit(taskID, toEditTask);
		String editedTaskDetails = toDoList.displayID(taskID);
		return String.format(RETURN_MESSAGE_FOR_EDIT, editedTaskDetails);
	}
	
	static String complete(int taskID) {
		Task completedTask = toDoList.delete(taskID);
		doneList.add(completedTask);
		return String.format(RETURN_MESSAGE_FOR_DELETE, completedTask.displayAll());
	}
	
	static String delete(int taskID) {
		Task deletedTask = toDoList.delete(taskID);
		return String.format(RETURN_MESSAGE_FOR_DELETE, deletedTask.displayAll());
	}
	
	static String displayID(int taskID) {
		return toDoList.displayID(taskID);
	}
	
	static String display() {
		return toDoList.display();
	}
	
	static String displayDone() {
		return doneList.display();
	}
}
