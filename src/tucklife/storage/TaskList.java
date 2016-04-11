//@@author A0111101N
package tucklife.storage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import tucklife.parser.ProtoTask;
import tucklife.storage.internal.Storage;
import tucklife.storage.internal.StorageExceptions.InvalidDateException;

public class TaskList {

	private static final Logger log = Logger.getLogger(Storage.class.getName());

	private ArrayList<Task> taskList;

	public TaskList() {
		taskList = new ArrayList<Task>();
	}

	public boolean contains(int taskID) {
		boolean containsID = false;
		for (Task task : taskList) {
			if (hasFoundID(taskID, task)) {
				containsID = true;
			}
		}
		return containsID;
	}

	// precondition: there must be a task with taskID in the ArrayList
	public String displayID(int taskID) {
		String displayString = "";
		for (Task task : taskList) {
			if (hasFoundID(taskID, task)) {
				displayString = task.displayAll();
			}
		}
		return displayString;
	}

	public String display(int itemsToDisplay) {
		StringBuilder sb = new StringBuilder();
		int i;
		boolean flag = true;
		if (taskList.size() <= itemsToDisplay) {
			itemsToDisplay = taskList.size();
			flag = false;
		}
		for (i = 0; i < itemsToDisplay; i++) {
			Task task = taskList.get(i);
			sb.append(task.display());
			sb.append("\n");
		}
		if (flag) {
			int remaining = taskList.size() - i;
			sb = getRemainingString(sb, remaining, "And %1$s other task\n", "And %1$s other tasks\n");
		}
		return sb.toString();
	}

	public String displayDefault(int itemsToDisplay) {
		if (taskList.size() == 0) {
			return "No tasks to display!";
		}
		StringBuilder sb = new StringBuilder();

		int qCounter = 0;
		for (Task qTask : taskList) {
			if (qTask.getQueueID() > 0) {
				qCounter += 1;
			}
		}
		int rCounter = taskList.size() - qCounter;

		int queueItemsToDisplay = itemsToDisplay / 2;
		int counter = 0;
		Task task = taskList.get(0);

		// Only add the "Queue:\n" and "\nOther Tasks:\n" headers if there are queued Task
		if (task.getQueueID() > 0) {
			counter = displayQueuedTasks(sb, qCounter, queueItemsToDisplay, counter);
			displayOtherTasks(itemsToDisplay, sb, rCounter, counter);
		} else {
			// Since there are no queued Task, we can just use the display function
			return display(itemsToDisplay);
		}
		return sb.toString();
	}

	private void displayOtherTasks(int itemsToDisplay, StringBuilder sb, int rCounter, int counter) {
		sb.append("\nOther Tasks:\n");
		int remainingItemsToDisplay = itemsToDisplay - counter;
		counter = 0;
		for (Task oTask : taskList) {
			// Not all other Tasks are displayed, stops when the limit is reached
			if (counter == remainingItemsToDisplay) {
				int remainingOTask = rCounter - remainingItemsToDisplay;
				sb = getRemainingString(sb, remainingOTask, "And %1$s other task\n", "And %1$s other tasks\n");
				break;
			}
			if (oTask.getQueueID() > 0) {
				continue;
			} else {
				sb.append(oTask.display());
				sb.append("\n");
			}
			counter++;
		}
	}

	private int displayQueuedTasks(StringBuilder sb, int qCounter, int queueItemsToDisplay, int counter) {
		sb.append("Queue:\n");
		for (Task qTask : taskList) {
			// Not all queued Tasks are displayed, stops when the limit is reached
			if (counter >= queueItemsToDisplay) {
				int remainingQTask = qCounter - queueItemsToDisplay;
				sb = getRemainingString(sb, remainingQTask, "And %1$s other task in queue\n",
						"And %1$s other tasks in queue\n");
				break;
			}
			if (qTask.getQueueID() != -1) {
				sb.append(qTask.display());
				sb.append("\n");
			} else {
				break;
			}
			counter++;
		}
		return counter;
	}

	private StringBuilder getRemainingString(StringBuilder sb, int remainingTask, String case1, String case2) {
		if (remainingTask == 1) {
			sb.append(String.format(case1, remainingTask));
		} else {
			if (remainingTask > 1) {
				sb.append(String.format(case2, remainingTask));
			}
		}
		return sb;
	}

	public String search(String searchKey) {
		StringBuilder sb = new StringBuilder();
		getExactMatches(searchKey, sb);
		getPartialMatches(searchKey, sb);
		log.log(Level.FINE, "Done searching through taskList for Tasks with searchKey");
		return sb.toString();
	}

	private void getPartialMatches(String searchKey, StringBuilder sb) {
		sb.append("\nPartial Match\n");
		for (Task task : taskList) {
			if (task.containsPartial(searchKey)) {
				sb.append(task.displayAll());
				sb.append("\n");
			}
		}
		log.log(Level.FINE, "Partial matches obtained");
	}

	private void getExactMatches(String searchKey, StringBuilder sb) {
		sb.append("Exact Match\n");
		for (Task task : taskList) {
			if (task.containsExact(searchKey)) {
				sb.append(task.displayAll());
				sb.append("\n");
			}
		}
		log.log(Level.FINE, "Exact matches obtained");
	}

	public void add(ProtoTask task) throws InvalidDateException {
		Task newTask = new Task(task);
		taskList.add(newTask);
		log.log(Level.FINE, "{0} added to tasklist via ProtoTask", newTask.getName());
	}

	public void add(Task task) {
		taskList.add(task);
		log.log(Level.FINE, "{0} added to tasklist via Task", task.getName());
	}

	public void add(int index, Task task) {
		taskList.add(index, task);
		log.log(Level.FINE, "{0} added to tasklist via index:{1} and Task", new Object[] { task.getName(), index });
	}

	public int size() {
		return taskList.size();
	}

	// precondition: there must be a task with taskID in the ArrayList
	public Task delete(int taskID) {
		Task removed = null;
		for (Task task : taskList) {
			if (hasFoundID(taskID, task)) {
				removed = task;
			}
		}
		if (taskList.remove(removed)) {
			log.log(Level.FINE, "{0} has been removed", removed.getName());
		}
		return removed;
	}

	public Task remove(int index) {
		Task t = taskList.remove(index);
		log.log(Level.FINE, "{0} has been removed", t.getName());
		return t;
	}

	// precondition: there must be a task with taskID in the ArrayList
	public void edit(int taskID, ProtoTask toEditTask) throws InvalidDateException {
		for (Task task : taskList) {
			if (hasFoundID(taskID, task)) {
				int taskIndex = taskList.indexOf(task);
				Task newTask = task.edit(toEditTask);
				taskList.set(taskIndex, newTask);
				log.log(Level.FINE, "{0} has been edited", newTask.getName());
			}
		}
	}

	public Task get(int taskID) {
		Task getTask = null;
		for (Task task : taskList) {
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

	public void sort(String sortBy, boolean isAscending) {
		if (sortBy != null) {
			if (sortBy.equals("@")) {
				Collections.sort(taskList, new TaskComparators.ComparatorLocation());
				log.log(Level.FINE, "tasklist has been sorted by location");
			}

			if (sortBy.equals("!")) {
				Collections.sort(taskList, new TaskComparators.ComparatorPriority());
				log.log(Level.FINE, "tasklist has been sorted by priority");
			}

			if (sortBy.equals("#")) {
				Collections.sort(taskList, new TaskComparators.ComparatorCategory());
				log.log(Level.FINE, "tasklist has been sorted by category");
			}

			if (sortBy.equals("$")) {
				Collections.sort(taskList, new TaskComparators.ComparatorTime());
				log.log(Level.FINE, "tasklist has been sorted by time");
			}

			if (sortBy.equals("+")) {
				Collections.sort(taskList, new TaskComparators.ComparatorTime());
				log.log(Level.FINE, "tasklist has been sorted by time");
			}

			if (sortBy.equals("&")) {
				Collections.sort(taskList, new TaskComparators.ComparatorAdditional());
				log.log(Level.FINE, "tasklist has been sorted by additional information");
			}

			if (!isAscending) {
				Collections.reverse(taskList);
				log.log(Level.FINE, "tasklist has been sorted in reverse order");
			}
		} else {
			Collections.sort(taskList, new TaskComparators.ComparatorDefault());
			log.log(Level.FINE, "tasklist has been sorted by queue number, then by time");
		}
	}

	public int tasksToday() {
		Calendar c = Calendar.getInstance();
		int count = 0;

		for (Task t : taskList) {

			if (t.isFloating() || t.getStartDate() != null) {
				continue;
			}

			Calendar deadline = t.getEndDate();

			if (c.get(Calendar.YEAR) == deadline.get(Calendar.YEAR)
					&& c.get(Calendar.DAY_OF_YEAR) == deadline.get(Calendar.DAY_OF_YEAR)) {
				count += 1;
			}
		}

		return count;
	}

}
