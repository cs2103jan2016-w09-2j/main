package tucklife.storage;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import tucklife.parser.ProtoTask;
import tucklife.storage.TaskList;

public class Storage {
	
	private static final String RETURN_MESSAGE_FOR_ADD = "{%1$s} has been added to TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_EDIT = "{%1$s} has been edited in TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_DELETE = "{%1$s} has been deleted from TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_QUEUE = "{%1$s} has been added to TuckLife's queue at position {%2$s}!";
	private static final String RETURN_MESSAGE_FOR_SETLIMIT = "Limit has been set to %1$s in TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_COMPLETE = "{%1$s} has been moved to TuckLife's done list!";
	
	private static final String RETURN_MESSAGE_FOR_NONEXISTENT_ID = "No task with id:%1$s in TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_OVERLOAD = "That day has been filled with %1$s tasks! It hit the limit! You should reschedule the task to another day. "
			+ "Alternatively, you can either change the overload limit or turn it off.";
	private static final String RETURN_MESSAGE_FOR_NOTHING_TO_UNDO = "There is no previous action to undo!";
	private static final String RETURN_MESSAGE_FOR_NOTHING_TO_REDO = "There is no previous action to redo!";
	
	private static TaskList toDoList = new TaskList();
	private static TaskList doneList = new TaskList();
	
	private static TaskList queueList = new TaskList();
	
	private static ArrayList<TaskList> undoSaveState;
	private static ArrayList<TaskList> redoSaveState;
	
	private static PreferenceList pf = new PreferenceList();
	
	private enum COMMAND_TYPE {
		ADD, DISPLAY, COMPLETE, DISPLAYDONE, DELETE, EDIT, INVALID, QUEUE, SETLIMIT, UNDO, REDO
	}
	
	public String parseCommand(ProtoTask pt) {
		COMMAND_TYPE ct = determineCommandType(pt.getCommand());
		String returnMessage = parseCommand(pt,ct);
		return returnMessage;
	}

	private static void storeUndoSaveState() {
		
		undoSaveState = new ArrayList<TaskList>();
		TaskList oldToDoList = new TaskList();
		TaskList oldQueueList = new TaskList();
		Iterator<Task> taskListIter = toDoList.iterator();
		
		while(taskListIter.hasNext()){
			Task t = taskListIter.next();
			oldToDoList.add(t);
		}
		oldToDoList.sort(null, true);
		taskListIter = oldToDoList.iterator();
		while(taskListIter.hasNext()){
			Task t = taskListIter.next();
			if(t.getQueueID()!=-1) {
				oldQueueList.add(t.getQueueID() - 1,t);
			}
		}
		
		undoSaveState.add(oldToDoList);
		undoSaveState.add(oldQueueList);
		
		TaskList oldDoneList = new TaskList();
		taskListIter = doneList.iterator();
		while(taskListIter.hasNext()){
			Task t = taskListIter.next();
			oldDoneList.add(t);
		}
		undoSaveState.add(oldDoneList);
	}
	
	private static void storeRedoSaveState() {
		
		redoSaveState = new ArrayList<TaskList>();
		TaskList oldToDoList = new TaskList();
		TaskList oldQueueList = new TaskList();
		Iterator<Task> taskListIter = toDoList.iterator();
		
		while(taskListIter.hasNext()){
			Task t = taskListIter.next();
			oldToDoList.add(t);
		}
		oldToDoList.sort(null, true);
		taskListIter = oldToDoList.iterator();
		while(taskListIter.hasNext()){
			Task t = taskListIter.next();
			if(t.getQueueID()!=-1) {
				oldQueueList.add(t.getQueueID() - 1,t);
			}
		}
		redoSaveState.add(oldToDoList);
		redoSaveState.add(oldQueueList);
		
		TaskList oldDoneList = new TaskList();
		taskListIter = doneList.iterator();
		while(taskListIter.hasNext()){
			Task t = taskListIter.next();
			oldDoneList.add(t);
		}
		redoSaveState.add(oldDoneList);
	}
	
	private static String undo() throws nothingToUndoException{
		if (undoSaveState == null) {
			throw new nothingToUndoException();
		}
		storeRedoSaveState();
		
		restoreSaveState(undoSaveState);
		undoSaveState = null;
		return "undone";
	}

	private static void restoreSaveState(ArrayList<TaskList> saveState) {
		toDoList = saveState.get(0);
		queueList = saveState.get(1);
		doneList = saveState.get(2);
	}
	
	private static String redo() throws nothingToRedoException{
		if (redoSaveState == null) {
			throw new nothingToRedoException();
		}
		storeUndoSaveState();
		
		restoreSaveState(redoSaveState);
		redoSaveState = null;
		return "redone";
	}
	
	public DataBox save() {
		
		TaskList[] saveList = new TaskList[2];
		saveList[0] = toDoList;
		saveList[1] = doneList;
		DataBox db = new DataBox(saveList, new PrefsStorage());
		return db;
	}
	
	public void load(DataBox db) {
		TaskList[] loadList = db.getLists();
		toDoList = loadList[0];
		doneList = loadList[1];
		toDoList.sort(null, true);
		Iterator<Task> iter = toDoList.iterator();
		while(iter.hasNext()){
			Task t = iter.next();
			if(t.getQueueID()!=-1){
				queueList.add(t);
			}
		}
		new PreferenceList().setLimit(db.getPrefs().getOverloadLimit());
	}
	
	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
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
		} else if (commandTypeString.equalsIgnoreCase("queue")) {
			return COMMAND_TYPE.QUEUE;
		} else if (commandTypeString.equalsIgnoreCase("setlimit")) {
			return COMMAND_TYPE.SETLIMIT;
		} else if (commandTypeString.equalsIgnoreCase("undo")) {
			return COMMAND_TYPE.UNDO;
		} else if (commandTypeString.equalsIgnoreCase("redo")) {
			return COMMAND_TYPE.REDO;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}
	
	private static String parseCommand(ProtoTask pt, COMMAND_TYPE commandType) throws Error {
		switch (commandType) {
		case ADD :
			try {
				prepareForUndo();
				return add(pt);
			} catch (overloadException e) {
				return String.format(RETURN_MESSAGE_FOR_OVERLOAD, e.limit);
			}
		case COMPLETE :
			try {
				prepareForUndo();
				return complete(pt.getId());
			} catch (IDNotFoundException e) {
				return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID, e.errorID);
			}
		case DISPLAY :
			try {
				return display(pt);
			} catch (IDNotFoundException e) {
				return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID, e.errorID);
			}
		case DISPLAYDONE :
			return displayDone();
		case DELETE :
			try {
				prepareForUndo();
				return delete(pt.getId());
			} catch (IDNotFoundException e) {
				return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID, e.errorID);
			}
		case EDIT :
			try {
				prepareForUndo();
				return edit(pt.getId(), pt);
			} catch (overloadException e) {
				return String.format(RETURN_MESSAGE_FOR_OVERLOAD, e.limit);
			}
		case QUEUE :
			prepareForUndo();
			try {
				return queue(pt.getId(), pt.getPosition());
			} catch (IDNotFoundException e) {
				return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID, e.errorID);
			}
		case SETLIMIT :
			return setLimit(pt.getLimit());
		case UNDO :
			try {
				return undo();
			} catch (nothingToUndoException e) {
				return RETURN_MESSAGE_FOR_NOTHING_TO_UNDO;
			}
		case REDO :
			try {
				return redo();
			} catch (nothingToRedoException e) {
				return RETURN_MESSAGE_FOR_NOTHING_TO_REDO;
			}
		default:
			//throw an error if the command is not recognized
			throw new Error("Unrecognized command type");
		}
	}

	private static void prepareForUndo() {
		storeUndoSaveState();
		redoSaveState = null;
	}
	
	private static String add(ProtoTask task) throws overloadException{
		Task newTask = new Task(task);
		if (newTask.isFloating()) {
			toDoList.add(newTask);
			return String.format(RETURN_MESSAGE_FOR_ADD, newTask.displayAll());
		}
		toDoList.sort("$",true);
		if(isOverloaded(newTask)) {
			throw new overloadException(new PreferenceList().getLimit());
		}
		
		toDoList.add(newTask);
		return String.format(RETURN_MESSAGE_FOR_ADD, newTask.displayAll());
	}

	private static boolean isOverloaded(Task newTask) {
		boolean hitLimit = false;
		int count = 0;
		int limit = pf.getLimit();
		
		if (limit == 0) {
			return false;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
		String newTaskDateString = newTask.isFloating() ? null : sdf.format(newTask.getEndDate().getTime());
		boolean flag = true;
		String oldDateString = null;
		Iterator<Task> taskListIter = toDoList.iterator();
		
		while(taskListIter.hasNext()){
			Task t = taskListIter.next();
			if(t.isFloating()) {
				continue;
			}
			String taskDateString = sdf.format(t.getEndDate().getTime());
			if(taskDateString.equals(oldDateString)) {
				count += 1;
			} else {
				oldDateString = taskDateString;
				count = 1;
			}
			if(flag) {
				if(taskDateString.equals(newTaskDateString)) {
					count +=1;
					flag = false;
				}
			}
			if(count == limit+1) {
				hitLimit = true;
				break;
			}
		}
		return hitLimit;
	}
	
	private static String edit(int taskID, ProtoTask toEditTask) throws overloadException {
		Task newTask = new Task(toEditTask);
		if(isOverloaded(newTask)) {
			throw new overloadException(new PreferenceList().getLimit());
		}
		toDoList.edit(taskID, toEditTask);
		String editedTaskDetails = toDoList.displayID(taskID);
		return String.format(RETURN_MESSAGE_FOR_EDIT, editedTaskDetails);
	}
	
	private static String complete(int taskID) throws IDNotFoundException {
		if(toDoList.contains(taskID)){
			Task completedTask = toDoList.delete(taskID);
			doneList.add(completedTask);
			return String.format(RETURN_MESSAGE_FOR_COMPLETE, completedTask.displayAll());
		} else {
			return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID, taskID);
		}
	}
	
	private static String delete(int taskID) throws IDNotFoundException {
		if(toDoList.contains(taskID)){
			Task deletedTask = toDoList.delete(taskID);
			if(queueList.contains(taskID)) {
				queueList.delete(taskID);
			}
			return String.format(RETURN_MESSAGE_FOR_DELETE, deletedTask.displayAll());
		} else {
			return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID, taskID);
		}
	}
	
	private static String displayID(int taskID) throws IDNotFoundException {
		if(toDoList.contains(taskID)){
			return toDoList.displayID(taskID);
		} else {
			return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID, taskID);
		}
	}
	
	private static String display(ProtoTask pt) throws IDNotFoundException {
		if(pt.getId() != -1) {
			return displayID(pt.getId());
		} else {
			String sortBy = pt.getSortCrit();
			assert sortBy.equals("@") || sortBy.equals("!") || sortBy.equals("#") || sortBy.equals("$") || sortBy.equals("&") || sortBy.equals("+") || sortBy == null;
			boolean isAscending = pt.getIsAscending();
			//assert ((sortOrder == 1 || sortOrder == 0) && sortBy != null) || (sortBy == null && sortOrder == -1);
			toDoList.sort(sortBy,isAscending);
			return toDoList.display();
		}
	}
	
	private static String displayDone() {
		return doneList.display();
	}
	
	private static String queue(int taskID, int pos) throws IDNotFoundException {
		/*
		Iterator<Task> qIter1 = queueList.iterator();
		Task gg = null;
		while(qIter1.hasNext()) {
			Task t = qIter1.next();
			if(t.getId() == taskID) {
				gg = t;
			}
		}
		if(gg!=null) {
			if(gg.getQueueID()>pos) {
				queueList.remove(gg.getQueueID() - 1);
				queueList.add(pos,gg);
			}
			else {
				queueList.add(pos-1,gg);
				queueList.remove(gg.getQueueID() - 1);
			}
			qIter1 = queueList.iterator();
			int count1 = 1;
			while(qIter1.hasNext()) {
				Task t = qIter1.next();
				t.setQueueID(count1);
				count1++;
			}
				
			return String.format(RETURN_MESSAGE_FOR_QUEUE, gg.displayAll(), pos);
			
		}
		*/
		if(toDoList.contains(taskID)){
			Task qTask = toDoList.get(taskID);
			if (pos == -1) {
				queueList.add(qTask);
				pos = queueList.size();
			} else {
				assert pos > 0;
				pos = pos - 1;
				queueList.add(pos, qTask);
			}
			Iterator<Task> qIter = queueList.iterator();
			int count = 1;
			while(qIter.hasNext()) {
				Task t = qIter.next();
				t.setQueueID(count);
				count++;
			}
				
			return String.format(RETURN_MESSAGE_FOR_QUEUE, qTask.displayAll(), pos);
		} else {
			return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID, taskID);
		}
		//return doneList.display();
	}
	
	private static String setLimit(int limit) {
		assert limit >= 0;
		PreferenceList.setLimit(limit);
		return String.format(RETURN_MESSAGE_FOR_SETLIMIT, limit);
	}
	
	
	//for testing purposes only
	public static void clear(){
		toDoList = new TaskList();
		doneList = new TaskList();
	}
}
