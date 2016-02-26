package tucklife.storage;

import tucklife.storage.ProtoTask;
import tucklife.storage.TaskList;

public class Storage {
	
	static TaskList toDoList;
	static TaskList doneList;
	
	enum COMMAND_TYPE {
		ADD, DISPLAY, COMPLETE, DISPLAYDONE, DELETE, EDIT, INVALID, SAVE, SORT, SEARCH
	}
	
	String parseCommand(ProtoTask pt) {
		COMMAND_TYPE ct = determineCommandType(pt.getType());
		String returnMessage = parseCommand(pt,ct); 
		return returnMessage;
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
		} else if (commandTypeString.equalsIgnoreCase("save")) {
			return COMMAND_TYPE.SAVE;
		} else if (commandTypeString.equalsIgnoreCase("sort")) {
			return COMMAND_TYPE.SORT;
		} else if (commandTypeString.equalsIgnoreCase("search")) {
			return COMMAND_TYPE.SEARCH;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}
	
	private static String parseCommand(ProtoTask pt, COMMAND_TYPE commandType) throws Error {
		switch (commandType) {
		case ADD :
			return add(pt);
		case DISPLAY :
			return display();
		case DISPLAYDONE :
			return displayDone();
		case DELETE :
			return delete(pt.getID());
		case EDIT :
			return edit(pt.getID(), pt);
		case INVALID :
			return invalid(pt);
		case SAVE :
			return save(pt);
		case SORT :
			return sort(pt);
		case SEARCH :
			return search(pt);
		default:
			//throw an error if the command is not recognized
			throw new Error("Unrecognized command type");
		}
	}
	
	static String add(ProtoTask task) {
		toDoList.add(task);
		return "gg";
	}
	
	static String edit(int taskID, ProtoTask toEditTask) {
		toDoList.edit(taskID, toEditTask);
		return "gg";
	}
	
	static void complete(int taskID) {
		Task completedTask = toDoList.delete(taskID);
		doneList.add(completedTask);
	}
	
	static String delete(int taskID) {
		toDoList.delete(taskID);
		return "gg";
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
