//@@author A0111101N
package tucklife.storage;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import tucklife.parser.ProtoTask;
import tucklife.storage.TaskList;

public class Storage {
	
	private static final Logger log = Logger.getLogger( Storage.class.getName() );
	
	private static final String RETURN_MESSAGE_FOR_ADD = "{%1$s} has been added to TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_EDIT = "{%1$s} has been edited in TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_DELETE = "{%1$s} has been deleted from TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_QUEUE = "{%1$s} has been added to TuckLife's queue at position {%2$s}!";
	private static final String RETURN_MESSAGE_FOR_SETLIMIT = "Limit has been set to %1$s in TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_SETLIMIT_WHEN_ABOVE_LIMIT = RETURN_MESSAGE_FOR_SETLIMIT
			+ " Be aware that there are some days with more tasks than your new limit!";
	private static final String RETURN_MESSAGE_FOR_SETLIMIT_OFF = "Limit has been turned off in TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_COMPLETE = "{%1$s} has been moved to TuckLife's done list!";
	private static final String RETURN_MESSAGE_FOR_UNCOMPLETE = "{%1$s} has been moved to TuckLife's to-do list!";
	
	private static final String RETURN_MESSAGE_FOR_NONEXISTENT_ID = "No task with id:%1$s in TuckLife's to-do list!";
	private static final String RETURN_MESSAGE_FOR_NONEXISTENT_ID_DONELIST = "No task with id:%1$s in TuckLife's done list!";
	
	private static final String STATUS_HEADER = "\n\n\n\n\n\n\n\n\n\nTasks at a glance...";
	private static final String STATUS_OUTSTANDING = "Total outstanding tasks: %1$s";
	private static final String STATUS_TODAY = "Tasks due today: %1$s";
	private static final String STATUS_CURRENT = "Current task: {%1$s}";
	private static final String STATUS_CURRENT_NONE = "None";
	
	private static final int defaultNumberOfDisplayedTasks = 20;
	
	private static TaskList toDoList;
	private static TaskList doneList;
	
	private static TaskList queueList;
	
	private static SaveState state = new SaveState();

	private static PrefsStorage pf;
	
	private enum COMMAND_TYPE {
		ADD, DISPLAY, COMPLETE, DISPLAYDONE, DELETE, EDIT, INVALID, QUEUE, SETLIMIT, UNDO, REDO, UNCOMPLETE
	}
	
	public String parseCommand(ProtoTask pt) {
		COMMAND_TYPE ct = determineCommandType(pt.getCommand());
		String returnMessage = parseCommand(pt,ct);
		return returnMessage;
	}
	
	private static String undo() throws nothingToUndoException{
		if (state.getUndoSaveState().size() == 0) {
			throw new nothingToUndoException();
		}
		state.storeRedoSaveState(toDoList, doneList);
		TaskList[] tl = state.restoreSaveState("undo");
		toDoList = tl[0];
		queueList = tl[1];
		doneList = tl[2];
		return "undone";
	}
	
	private static String redo() throws nothingToRedoException{
		if (state.getRedoSaveState().size() == 0) {
			throw new nothingToRedoException();
		}
		state.storeUndoSaveState(toDoList, doneList);
		TaskList[] tl = state.restoreSaveState("redo");
		toDoList = tl[0];
		queueList = tl[1];
		doneList = tl[2];
		return "redone";
	}
	
	public DataBox save() {
		TaskList[] saveList = new TaskList[2];
		saveList[0] = toDoList;
		saveList[1] = doneList;
		DataBox db = new DataBox(saveList, pf);
		return db;
	}
	
	public void load(DataBox db) {
		TaskList[] loadList = db.getLists();
		toDoList = loadList[0];
		doneList = loadList[1];
		queueList = state.getQueueListFromToDoList(toDoList);
		pf = db.getPrefs();
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
		} else if (commandTypeString.equalsIgnoreCase("uncomplete")) {
			return COMMAND_TYPE.UNCOMPLETE;
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
				state.prepareForUndo(toDoList, doneList);
				return add(pt);
			} catch (overloadException e) {
				return e.getReturnMsg();
			} catch (invalidDateException e) {
				return e.getReturnMsg();
			}
		case COMPLETE :
			state.prepareForUndo(toDoList, doneList);
			return complete(pt.getId());
		case DISPLAY :
			return display(pt, toDoList);
		case DISPLAYDONE :
			return display(pt, doneList);
		case DELETE :
			state.prepareForUndo(toDoList, doneList);
			return delete(pt.getId());
		case EDIT :
			try {
				state.prepareForUndo(toDoList, doneList);
				return edit(pt.getId(), pt);
			} catch (overloadException e) {
				return e.getReturnMsg();
			} catch (invalidDateException e) {
				return e.getReturnMsg();
			}
		case QUEUE :
			state.prepareForUndo(toDoList, doneList);
			return queue(pt.getId(), pt.getPosition());
		case SETLIMIT :
			return setLimit(pt.getLimit());
		case UNCOMPLETE :
			state.prepareForUndo(toDoList, doneList);
			return uncomplete(pt.getId());
		case UNDO :
			try {
				return undo();
			} catch (nothingToUndoException e) {
				return e.getReturnMsg();
			}
		case REDO :
			try {
				return redo();
			} catch (nothingToRedoException e) {
				return e.getReturnMsg();
			}
		default:
			//throw an error if the command is not recognized
			throw new Error("Unrecognized command type");
		}
	}
	
	private static String add(ProtoTask task) throws overloadException, invalidDateException{
		Task newTask = new Task(task);
		if (newTask.isFloating()) {
			toDoList.add(newTask);
			return String.format(RETURN_MESSAGE_FOR_ADD, newTask.displayAll());
		}
		toDoList.sort("$",true);
		if(isOverloaded(newTask)) {
			throw new overloadException(pf.getOverloadLimit());
		}
		
		toDoList.add(newTask);
		return String.format(RETURN_MESSAGE_FOR_ADD, newTask.displayAll());
	}

	private static boolean isOverloaded(Task newTask) {
		
		//dont count floating tasks and events
		if(isNotDeadline(newTask)) {
			return false;
		}
		
		int limit = pf.getOverloadLimit();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
		String newTaskDateString = sdf.format(newTask.getEndDate().getTime());
		return checkIsOverloaded(limit, newTaskDateString);
	}

	private static boolean checkIsOverloaded(int limit, String newDeadlineDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
		boolean isNewDeadlineDateInToDoList = true;
		boolean hasHitLimit = false;
		int limitCount = 0;
		String currentDate = null;
		Iterator<Task> taskListIter = toDoList.iterator();
		
		while(taskListIter.hasNext()) {
			Task t = taskListIter.next();
			
			//dont count floating tasks and events
			if(isNotDeadline(t)) {
				continue;
			} 
			
			String deadlineDate = sdf.format(t.getEndDate().getTime());
			
			//adds to count if the task's date is the same as the current date
			//otherwise reset count and update current date
			if(deadlineDate.equals(currentDate)) {
				limitCount += 1;
			} else {
				currentDate = deadlineDate; 
				limitCount = 1; 
			}
			if(isNewDeadlineDateInToDoList) {
				if(deadlineDate.equals(newDeadlineDate)) {
					limitCount +=1;
					isNewDeadlineDateInToDoList = false;
				}
			}
			if(limitCount > limit) {
				hasHitLimit = true;
				break;
			}
		}
		return hasHitLimit;
	}

	private static boolean isNotDeadline(Task t) {
		return t.isFloating() || t.getStartDate() != null;
	}
	
	private static String edit(int taskID, ProtoTask toEditTask) throws overloadException, invalidDateException {
		if(toDoList.contains(taskID)){	
			Task newTask = new Task(toEditTask);
			if(isOverloaded(newTask)) {
				throw new overloadException(pf.getOverloadLimit());
			}
			toDoList.edit(taskID, toEditTask);
			String editedTaskDetails = toDoList.displayID(taskID);
			return String.format(RETURN_MESSAGE_FOR_EDIT, editedTaskDetails);
		} else {
			return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID, taskID);
		}
	}
	
	private static String complete(int taskID) {
		if(toDoList.contains(taskID)){
			Task completedTask = toDoList.delete(taskID);
			completedTask.setQueueID(-(doneList.size() + 1));
			if(queueList.contains(taskID)) {
				queueList.delete(taskID);
				updateQueueIDs(0, 0);
			}
			doneList.add(completedTask);
			return String.format(RETURN_MESSAGE_FOR_COMPLETE, completedTask.displayAll());
		} else {
			return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID, taskID);
		}
	}
	
	private static String uncomplete(int taskID) {
		if(doneList.contains(taskID)) {
			Task uncompletedTask = doneList.delete(taskID);
			uncompletedTask.setQueueID(-1);
			toDoList.add(uncompletedTask);
			return String.format(RETURN_MESSAGE_FOR_UNCOMPLETE, uncompletedTask.displayAll());
		} else {
			return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID_DONELIST, taskID);
		}
	}
	
	private static String delete(int taskID) {
		if(toDoList.contains(taskID)){
			Task deletedTask = toDoList.delete(taskID);
			if(queueList.contains(taskID)) {
				queueList.delete(taskID);
				updateQueueIDs(0, 0);
			}
			return String.format(RETURN_MESSAGE_FOR_DELETE, deletedTask.displayAll());
		} else {
			return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID, taskID);
		}
	}
	
	private static String displayID(int taskID) {
		if(toDoList.contains(taskID)){
			return toDoList.displayID(taskID);
		} else {
			return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID, taskID);
		}
	}
	
	private static String display(ProtoTask pt, TaskList taskList) {
		if (pt.getSearchKey()!=null) {
			taskList.sort(null, true);
			return taskList.search(pt.getSearchKey());
		}
		
		if(pt.getId() != -1) {
			return displayID(pt.getId());
		} else {
			String sortBy = pt.getSortCrit();
			assert sortBy.equals("@") || sortBy.equals("!") || sortBy.equals("#") || sortBy.equals("$") || sortBy.equals("&") || sortBy.equals("+") || sortBy == null;
			boolean isAscending = pt.getIsAscending();
			taskList.sort(sortBy,isAscending);
			if (sortBy == null) {
				return taskList.displayDefault(defaultNumberOfDisplayedTasks);
			} else {
				return taskList.display(defaultNumberOfDisplayedTasks);
			}
		}
	}
	
	private static String queue(int taskID, int pos) {
		if(toDoList.contains(taskID)){
			boolean isPosDefault = pos == -1;
			boolean isPosTooLarge = pos>queueList.size();
			boolean isBackOfQueue = isPosDefault || isPosTooLarge;
			Task qTask = toDoList.get(taskID);
			if(isBackOfQueue) {
				pos = queueList.size();
			}
			if(queueList.contains(taskID)) {
				
				queueList.delete(taskID);
				queueList.add(pos-1,qTask);
				
			} else {
				if(isBackOfQueue) {
					queueList.add(pos,qTask);
				} else {
					queueList.add(pos-1,qTask);
				}
			}
			
			pos = updateQueueIDs(taskID, pos);
				
			return String.format(RETURN_MESSAGE_FOR_QUEUE, qTask.displayAll(), pos);
		} else {
			return String.format(RETURN_MESSAGE_FOR_NONEXISTENT_ID, taskID);
		}
	}

	private static int updateQueueIDs(int taskID, int pos) {
		Iterator<Task> qIter = queueList.iterator();
		int count = 1;
		while(qIter.hasNext()) {
			Task t = qIter.next();
			t.setQueueID(count);
			if(t.getId() == taskID) {
				pos = t.getQueueID();
			}
			count++;
		}
		return pos;
	}
	
	private static String setLimit(int limit) {
		assert limit >= 0;
		pf.setOverloadLimit(limit);
		if(limit == 0) {
			pf.setOverloadLimit(-1);
			return RETURN_MESSAGE_FOR_SETLIMIT_OFF;
		} else if(checkIsOverloaded(limit, null)) {
			return String.format(RETURN_MESSAGE_FOR_SETLIMIT_WHEN_ABOVE_LIMIT, limit);
		} else {
			return String.format(RETURN_MESSAGE_FOR_SETLIMIT, limit);
		}
	}
	
	public String getStatus(){
		StringBuilder status = new StringBuilder();
		
		status.append(STATUS_HEADER);
		status.append("\n");
		status.append(String.format(STATUS_OUTSTANDING, Integer.toString(toDoList.size())));
		status.append("\n");
		status.append(String.format(STATUS_TODAY, Integer.toString(toDoList.tasksToday())));
		status.append("\n");
		if(queueList.size() != 0){
			status.append(String.format(STATUS_CURRENT, queueList.display(10).split("\n")[0]));
		} else{
			status.append(String.format(STATUS_CURRENT, STATUS_CURRENT_NONE));
		}
		
		return status.toString();
	}
	
	
	//for testing purposes only
	public static void clear(){
		Task.resetGlobalId();
		pf = new PrefsStorage();
		setLimit(0);
		toDoList = new TaskList();
		queueList = new TaskList();
		doneList = new TaskList();
		state = new SaveState();
	}
	
	public static TaskList getTD(){
		return toDoList;
	}
	
	public static TaskList getQ(){
		return queueList;
	}
}
