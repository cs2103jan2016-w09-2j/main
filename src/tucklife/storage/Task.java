//@@author A0111101N
package tucklife.storage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import tucklife.parser.ProtoTask;
import tucklife.storage.internal.Storage;
import tucklife.storage.internal.StorageExceptions;
import tucklife.storage.internal.StorageExceptions.InvalidDateException;

public class Task {

	private static final Logger log = Logger.getLogger(Storage.class.getName());

	private String location;
	private int priority;
	private String category;
	private String additional;
	private String name;

	// if null, means that it is a task not event
	private Calendar startDate;

	// either deadline or end time for event. if null, means it is a floating task
	
	private Calendar endDate;

	private boolean floating;
	private boolean deadline;

	private static int globalID = 1;

	private int id;

	private int queueID;

	private static final String HEADER_LOCATION = "Location: ";
	private static final String HEADER_CATEGORY = "Category: ";
	private static final String HEADER_PRIORITY = "Priority: ";
	private static final String HEADER_DEADLINE = "By: ";
	private static final String HEADER_ADDITIONAL = "Additional: ";
	private static final String HEADER_EVENT_START = "From: ";
	private static final String HEADER_EVENT_END = " To: ";

	private static final String PRIORITY_HIGH = "High";
	private static final String PRIORITY_MEDIUM = "Med";
	private static final String PRIORITY_LOW = "Low";

	private static final String TASK_NAME_EXTENDER = "...";
	private static final String TASK_FIELD_SEPERATOR = " | ";

	public static void resetGlobalId() {
		globalID = 1;
	}

	// used when Task is created but not added into TaskList
	public static void decrementGlobalId() {
		globalID = globalID - 1;
	}

	public int getId() {
		return id;
	}

	public String getLocation() {
		return location;
	}

	public int getPriority() {
		return priority;
	}

	public String getCategory() {
		return category;
	}

	public String getAdditional() {
		return additional;
	}

	public String getName() {
		return name;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public boolean isFloating() {
		return floating;
	}

	public static int getGlobalID() {
		return globalID;
	}

	public int getQueueID() {
		return queueID;
	}

	public boolean isDeadline() {
		return deadline;
	}

	public String getterForVariables() {
		return "";
	}

	public void setQueueID(int id) {
		this.queueID = id;
	}

	public Task(ProtoTask task) throws InvalidDateException {
		// create the Task
		this.location = task.getLocation();
		this.priority = task.getPriority();
		this.category = task.getCategory();
		this.additional = task.getAdditional();
		this.name = task.getTaskDesc();
		this.startDate = task.getStartDate();
		this.endDate = task.getEndDate();
		checkValidDates(startDate, endDate);
		this.floating = startDate == null && endDate == null;
		this.deadline = !this.floating && this.startDate == null;
		this.id = globalID;
		this.queueID = task.getPosition();
		globalID++;
		log.log(Level.FINE, "Task has been created via ProtoTask");
	}

	public Task(Task task) {
		// create the Task
		this.location = task.getLocation();
		this.priority = task.getPriority();
		this.category = task.getCategory();
		this.additional = task.getAdditional();
		this.name = task.getName();
		this.startDate = task.getStartDate();
		this.endDate = task.getEndDate();
		this.floating = startDate == null && endDate == null;
		this.deadline = this.floating || this.startDate == null;
		this.id = task.getId();
		this.queueID = task.getQueueID();
	}

	public Task edit(ProtoTask task) throws InvalidDateException {
		// edit task
		this.location = editParam(this.location, task.getLocation());
		this.priority = editParam(this.priority, task.getPriority());
		this.category = editParam(this.category, task.getCategory());
		this.additional = editParam(this.additional, task.getAdditional());
		this.name = editParam(this.name, task.getTaskDesc());

		editDate(task);

		this.floating = startDate == null && endDate == null;
		this.deadline = this.floating || this.startDate == null;
		this.id = task.getId() == -1 ? this.id : task.getId();
		log.log(Level.FINE, "Task has been edited via ProtoTask");
		return this;
	}

	private void editDate(ProtoTask task) throws InvalidDateException {
		preEditEndDate(task);

		preEditStartDate(task);

		boolean editHasNoDate = task.getStartDate() == null && task.getEndDate() == null;

		// only do such changes if there is a time but no date
		if (editHasNoDate) {
			// task to change into is a deadline
			if (task.getStartTime() == null && task.getEndTime() != null) {
				// if current task is a floating task
				if (this.endDate == null) {
					// take deadline to be nearest time, similar to the way add
					// handles a single time
					this.endDate = task.getEndTime();
				} else {
					// change the time of the deadline
					this.endDate = mergeDateTime(this.endDate, task.getEndTime());
				}
			}
			// task to change into is an event
			if (task.getStartTime() != null && task.getEndTime() != null) {
				// if current task is already a multiday event, just need to
				// merge
				if (this.startDate != null && this.startDate != this.endDate) {
					this.startDate = mergeDateTime(this.startDate, task.getStartTime());
					this.endDate = mergeDateTime(this.endDate, task.getEndTime());
				} else {
					// change current task into a single day event
					if (onSameDay(task.getStartTime(), task.getEndTime())) {
						this.startDate = mergeDateTime(this.endDate, task.getStartTime());
						this.endDate = mergeDateTime(this.endDate, task.getEndTime());
					} else {
						// timing does not fall within a single day
						this.startDate = mergeDateTime(this.endDate, task.getStartTime());
						this.endDate = mergeDateTime(tomorrow(this.endDate), task.getEndTime());
					}
				}

				// change floating task to event
				if (isFloating()) {
					this.startDate = task.getStartTime();
					this.endDate = task.getEndTime();
				}
			}
		}

		checkValidDates(startDate, endDate);

		boolean removeDateParam = task.getEndDate() != null && task.getEndDate().get(Calendar.YEAR) == 2000;
		if (removeDateParam) {
			this.startDate = null;
			this.endDate = null;
		}
	}

	private void preEditStartDate(ProtoTask task) {
		// need to merge if there is only date but no time
		if (task.getStartTime() == null && task.getStartDate() != null) {
			this.startDate = mergeDateTime(task.getStartDate(), this.startDate);
		} else {
			// no need to merge, can get the start date directly
			if (task.getStartTime() != null && task.getStartDate() != null) {
				this.startDate = task.getStartDate();
			}
			// since there is no start date and time given, set startdate to
			// null
			if (task.getStartTime() == null && task.getStartDate() == null) {
				this.startDate = null;
			}
		}
	}

	private void preEditEndDate(ProtoTask task) {
		// need to merge if there is only date but no time
		if (task.getEndTime() == null && task.getEndDate() != null) {
			this.endDate = mergeDateTime(task.getEndDate(), this.endDate);
			// no need to merge, can get the end date directly
			if (task.getStartDate() == null && this.startDate != null) {
				this.endDate = task.getEndDate();
			}
		} else {
			// no need to merge, can get the end date directly
			if (task.getEndTime() != null && task.getEndDate() != null) {
				this.endDate = task.getEndDate();
			}
		}
	}
	
	private String editParam(String self, String change) {
		//check if ProtoTask demands a change to the Task
		if (change != null) {
			//check if the parameter should be removed
			if (change.equals("")) {
				return null;
			} else {
				return change;
			}
		} else {
			return self;
		}
	}

	private int editParam(int self, int change) {
		//check if ProtoTask demands a change to the Task
		if (change != -1) {
			//check if the parameter should be removed
			if (change == 0) {
				return 0;
			} else {
				return change;
			}
		} else {
			return self;
		}
	}

	private Calendar tomorrow(Calendar endDate) {
		if (endDate == null) {
			return null;
		}
		endDate.add(Calendar.DATE, 1);
		return endDate;
	}

	private boolean onSameDay(Calendar startTime, Calendar endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
		return sdf.format(startTime.getTime()).equals(sdf.format(endTime.getTime()));
	}

	public String display() {
		StringBuilder displayString = new StringBuilder();

		// display order:
		// id. truncated name | date

		displayString.append(idField());
		displayString.append(" ");
		displayString.append(nameField(true));
		
		String date = dateField();

		if(date != null){
			displayString.append(TASK_FIELD_SEPERATOR);
			displayString.append(date);
		}

		return displayString.toString();
	}

	public boolean containsExact(String searchKey) {
		String[] fields = new String[6];

		fields[0] = dateField();
		fields[1] = locationField();
		fields[2] = priorityField();
		fields[3] = categoryField();
		fields[4] = additionalField();
		fields[5] = getName();
		
		String searchItem = " " + searchKey + " ";

		for (int i = 0; i < 6; i++) {
			if (fields[i] != null) {
				String searchFrom = "  " + fields[i] + " ";
				if (searchFrom.toLowerCase().contains(searchItem.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean containsPartial(String searchKey) {
		if (this.containsExact(searchKey)) {
			return false;
		}
		String[] fields = new String[6];

		fields[0] = dateField();
		fields[1] = locationField();
		fields[2] = priorityField();
		fields[3] = categoryField();
		fields[4] = additionalField();
		fields[5] = getName();

		for (int i = 0; i < 6; i++) {
			if (fields[i] != null) {
				if (fields[i].toLowerCase().contains(searchKey.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	private String idField() {
		String rawId = Integer.toString(id);
		int padSize = Integer.toString(globalID).length();

		String paddedId = String.format("%" + Integer.toString(padSize) + "s", rawId);

		return paddedId + ".";
	}

	private String nameField(boolean truncated) {
		if (truncated) {
			// truncates long names
			if (name.length() > 15) {
				return name.substring(0, 12) + TASK_NAME_EXTENDER;
			} else {
				return String.format("%-15s", name);
			}
		}

		return name;
	}

	private String dateField() {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");
		if (endDate == null) {
			return null;

		} else if (endDate != null && startDate == null) {
			return HEADER_DEADLINE + sdf.format(endDate.getTime());

		} else if (endDate != null && startDate != null) {
			return HEADER_EVENT_START + sdf.format(startDate.getTime()) + HEADER_EVENT_END
					+ sdf.format(endDate.getTime());
		}

		// shouldn't happen if task is correct
		return null;
	}

	private String locationField() {
		if (location == null) {
			return null;
		} else {
			return HEADER_LOCATION + location;
		}
	}

	private String priorityField() {
		if (priority == -1) {
			return null;
		} else {
			switch (priority) {
			case 1:
				return HEADER_PRIORITY + PRIORITY_HIGH;
			case 2:
				return HEADER_PRIORITY + PRIORITY_MEDIUM;
			case 3:
				return HEADER_PRIORITY + PRIORITY_LOW;

			// shouldn't happen if task is correct
			default:
				return null;
			}
		}
	}

	private String categoryField() {
		if (category == null) {
			return null;
		} else {
			return HEADER_CATEGORY + category;
		}
	}

	private String additionalField() {
		if (additional == null) {
			return null;
		} else {
			return HEADER_ADDITIONAL + additional;
		}
	}

	public String displayAll() {
		StringBuilder fullDisplayString = new StringBuilder();

		// displayAll order:
		// id. full name | date | location | priority | category | additional

		fullDisplayString.append(idField());
		fullDisplayString.append(" ");
		fullDisplayString.append(nameField(false));

		String[] fields = new String[5];

		fields[0] = dateField();
		fields[1] = locationField();
		fields[2] = priorityField();
		fields[3] = categoryField();
		fields[4] = additionalField();

		for (int i = 0; i < 5; i++) {
			if (fields[i] != null) {
				fullDisplayString.append(TASK_FIELD_SEPERATOR);
				fullDisplayString.append(fields[i]);
			}
		}

		return fullDisplayString.toString();
	}

	// @@author A0111101N
	private Calendar mergeDateTime(Calendar date, Calendar time) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR, date.get(Calendar.YEAR));
		c.set(Calendar.MONTH, date.get(Calendar.MONTH));
		c.set(Calendar.DATE, date.get(Calendar.DATE));
		if (time == null) {
			c.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, date.get(Calendar.MINUTE));
		} else {
			c.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		}

		return c;
	}

	private void checkValidDates(Calendar start, Calendar end) throws InvalidDateException {
		if (end == null) {
			return;
		}
		if (end.before(start) && start != null) {
			throw new StorageExceptions.InvalidDateException(start, end);
		}
	}

}