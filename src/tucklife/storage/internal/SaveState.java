//@@author A0111101N
package tucklife.storage.internal;

import java.util.ArrayList;
import java.util.Iterator;

import tucklife.storage.Task;
import tucklife.storage.TaskList;

public class SaveState {

	private ArrayList<ArrayList<TaskList>> undoSaveState = new ArrayList<ArrayList<TaskList>>();
	private ArrayList<ArrayList<TaskList>> redoSaveState = new ArrayList<ArrayList<TaskList>>();

	public ArrayList<ArrayList<TaskList>> getUndoSaveState() {
		return undoSaveState;
	}

	public ArrayList<ArrayList<TaskList>> getRedoSaveState() {
		return redoSaveState;
	}

	public void storeUndoSaveState(TaskList toDoList, TaskList doneList) {
		ArrayList<TaskList> saveState = getSaveState(toDoList, doneList);

		if (undoSaveState.size() < 50) {
			undoSaveState.add(saveState);
		} else {
			undoSaveState.remove(0);
			undoSaveState.add(saveState);
		}
	}

	public void storeRedoSaveState(TaskList toDoList, TaskList doneList) {
		ArrayList<TaskList> saveState = getSaveState(toDoList, doneList);

		if (redoSaveState.size() < 50) {
			redoSaveState.add(saveState);
		} else {
			redoSaveState.remove(0);
			redoSaveState.add(saveState);
		}
	}

	private ArrayList<TaskList> getSaveState(TaskList toDoList, TaskList doneList) {
		ArrayList<TaskList> saveState = new ArrayList<TaskList>();

		TaskList oldToDoList = duplicateTaskList(toDoList);
		TaskList oldQueueList = getQueueListFromToDoList(oldToDoList);

		saveState.add(oldToDoList);
		saveState.add(oldQueueList);

		TaskList oldDoneList = duplicateTaskList(doneList);

		saveState.add(oldDoneList);
		return saveState;
	}

	public TaskList getQueueListFromToDoList(TaskList oldToDoList) {
		oldToDoList.sort(null, true);
		TaskList oldQueueList = new TaskList();
		Iterator<Task> taskListIter = oldToDoList.iterator();
		while (taskListIter.hasNext()) {
			Task t = taskListIter.next();
			if (t.getQueueID() != -1) {
				oldQueueList.add(t);
			}
		}
		return oldQueueList;
	}

	private TaskList duplicateTaskList(TaskList originalList) {
		TaskList duplicateList = new TaskList();
		Iterator<Task> taskListIter = originalList.iterator();
		while (taskListIter.hasNext()) {
			Task t = new Task(taskListIter.next());
			duplicateList.add(t);
		}
		return duplicateList;
	}

	public TaskList[] restoreSaveState(String type) {
		ArrayList<TaskList> state;
		if (type.equals("undo")) {
			state = undoSaveState.remove(undoSaveState.size() - 1);
		} else {
			state = redoSaveState.remove(redoSaveState.size() - 1);
		}
		TaskList toDoList = state.get(0);
		TaskList queueList = state.get(1);
		TaskList doneList = state.get(2);
		TaskList[] tl = { toDoList, queueList, doneList };
		return tl;
	}

	public void prepareForUndo(TaskList toDoList, TaskList doneList) {
		storeUndoSaveState(toDoList, doneList);
		redoSaveState.clear();
	}

}
