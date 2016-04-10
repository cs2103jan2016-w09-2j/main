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
	
	private static final Logger log = Logger.getLogger( Storage.class.getName() );
	
	private String location;
	private int priority;
	private String category;
	private String additional;
	private String name;	
	
	private Calendar startDate; //if null, means that it is a task not event
	private Calendar endDate; //either deadline or end time for event. if null, means it is a floating task
	
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
	
	public int getId(){
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
	
	public int getQueueID(){
		return queueID;
	}
	
	public void setQueueID(int id){
		this.queueID = id;
	}
	
	public boolean isDeadline() {
		return deadline;
	}
	
	public Task(ProtoTask task) throws InvalidDateException{
		//create the Task
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
		log.log( Level.FINE, "Task has been created via ProtoTask");
	}
	
	public Task(Task task){
		//create the Task
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
	
	public Task edit(ProtoTask task) throws InvalidDateException{
		//edit task
		this.location = editParam(this.location, task.getLocation());
		this.priority = editParam(this.priority, task.getPriority());
		this.category = editParam(this.category, task.getCategory());
		this.additional = editParam(this.additional, task.getAdditional());
		this.name = editParam(this.name, task.getTaskDesc());
		
		editDate(task);
		
		this.floating = startDate == null && endDate == null;
		this.deadline = this.floating || this.startDate == null;
		this.id = task.getId() == -1 ? this.id : task.getId();
		log.log( Level.FINE, "Task has been edited via ProtoTask");
		return this;
	}

	private void editDate(ProtoTask task) throws InvalidDateException {
		if(task.getEndTime() == null && task.getEndDate()!=null) { //need to merge
			this.endDate = mergeDateTime(task.getEndDate(),this.endDate); 
			if(task.getStartDate()==null && this.startDate != null) { //no need to merge, can get the end date directly
				this.endDate = task.getEndDate();
			}
		} else {
			if(task.getEndTime()!=null && task.getEndDate()!=null) { //no need to merge, can get the end date directly
				this.endDate = task.getEndDate();
			}
		}
		
		if(task.getStartTime() == null && task.getStartDate()!=null) { //need to merge
			this.startDate = mergeDateTime(task.getStartDate(),this.startDate); 
		} else {
			if(task.getStartTime()!=null && task.getStartDate()!=null) { //no need to merge, can get the start date directly
				this.startDate = task.getStartDate();
			}
			
			if(task.getStartTime() == null && task.getStartDate() == null) {
				this.startDate = null;
			}
		}
		
		if(task.getStartDate() == null && task.getEndDate() == null) { //only do such changes if there is a time but no date
			
			if(task.getStartTime() == null && task.getEndTime()!=null) { //current task must be a deadline else it does not make sense
				if(this.startDate == null) {
					if(this.endDate == null) { //current task is a floating task
						this.endDate = task.getEndTime(); //take deadline to be nearest time, similiar to the way add handles a single time
					} else {
						this.endDate = mergeDateTime(this.endDate, task.getEndTime()); //change the time of the deadline
					}
				} 
				
				
				if(task.getStartDate() == null) {
					this.startDate = null;
				}
			}
			
			if(task.getStartTime()!=null && task.getEndTime()!=null) { //new edited task becomes an event
				
				if(this.startDate != null && this.startDate != this.endDate) {
					this.startDate = mergeDateTime(this.startDate,task.getStartTime());
					this.endDate = mergeDateTime(this.endDate,task.getEndTime());
				} else {
					if(onSameDay(task.getStartTime(),task.getEndTime())) {
						this.startDate = mergeDateTime(this.endDate,task.getStartTime());
						this.endDate = mergeDateTime(this.endDate,task.getEndTime());
					} else {
						this.startDate = mergeDateTime(this.endDate,task.getStartTime());
						this.endDate = mergeDateTime(tomorrow(this.endDate),task.getEndTime());
					}
				}
				
				if(isFloating()) { //floating task to event
					this.startDate = task.getStartTime();
					this.endDate = task.getEndTime();
				}
			}
		}
		
		checkValidDates(startDate, endDate);
		
		if(task.getEndDate() != null && task.getEndDate().get(Calendar.YEAR) == 2000) {
			this.startDate = null;
			this.endDate = null;
		}
	}

	private String editParam(String self, String change) {
		if(change != null) {
			if(change.equals("")) {
				return null;
			} else {
				return change;
			}
		} else {
			return self;
		}
	}
	
	private int editParam(int self, int change) {
		if(change != -1) {
			if(change == 0) {
				return 0;
			} else {
				return change;
			}
		} else {
			return self;
		}
	}
	
	private Calendar tomorrow(Calendar endDate) {
		if(endDate == null) {
			return null;
		}
		endDate.add(Calendar.DATE, 1);
		return endDate;
	}

	private boolean onSameDay(Calendar startTime, Calendar endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
		return sdf.format(startTime.getTime()).equals(sdf.format(endTime.getTime()));
	}

	public String display(){
		StringBuilder displayString = new StringBuilder();
		
		// display order:
		// id. truncated name | date | location
		
		displayString.append(idField());
		displayString.append(" ");
		displayString.append(nameField(true));
		
		String[] fields = new String[2];
		
		fields[0] = dateField();
		fields[1] = locationField();
		
		for(int i = 0; i < 2; i++){
			if(fields[i] != null){
				displayString.append(TASK_FIELD_SEPERATOR);
				displayString.append(fields[i]);
			}
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
		
		for(int i = 0; i < 6; i++){
			if(fields[i] != null){
				String searchFrom = "  " + fields[i] + " ";
				if (searchFrom.toLowerCase().contains(searchItem.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean containsPartial(String searchKey) {
		if(this.containsExact(searchKey)) {
			return false;
		}
		String[] fields = new String[6];
		
		fields[0] = dateField();
		fields[1] = locationField();
		fields[2] = priorityField();
		fields[3] = categoryField();
		fields[4] = additionalField();
		fields[5] = getName();
		
		for(int i = 0; i < 6; i++){
			if(fields[i] != null){
				if (fields[i].toLowerCase().contains(searchKey.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String idField(){
		String rawId = Integer.toString(id);
		int padSize = Integer.toString(globalID).length();
		
		String paddedId = String.format("%" + Integer.toString(padSize) + "s", rawId);
		
		return paddedId + ".";
	}
	
	private String nameField(boolean truncated){
		if(truncated){
			// truncates long names
			if(name.length() > 15){
				return name.substring(0, 12) + TASK_NAME_EXTENDER;
			}
		} 
		
		return name;
	}
	
	private String dateField(){
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
		if(endDate == null){
			return null;
			
		} else if (endDate != null && startDate == null) {
			return HEADER_DEADLINE + sdf.format(endDate.getTime());
			
		} else if (endDate != null && startDate != null) {
			return HEADER_EVENT_START + sdf.format(startDate.getTime()) + HEADER_EVENT_END + sdf.format(endDate.getTime());
		}
		
		// shouldn't happen if task is correct
		return null;
	}
	
	private String locationField(){
		if (location == null) {
			return null;
		} else{
			return HEADER_LOCATION + location;
		}
	}
	
	private String priorityField(){
		if(priority == -1){
			return null;
		} else{
			switch(priority){
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
	
	private String categoryField(){
		if (category == null) {
			return null;
		} else{
			return HEADER_CATEGORY + category;
		}
	}
	
	private String additionalField(){
		if(additional == null){
			return null;
		} else{
			return HEADER_ADDITIONAL + additional;
		}
	}
	
	public String displayAll(){
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
		
		for(int i = 0; i < 5; i++){
			if(fields[i] != null){
				fullDisplayString.append(TASK_FIELD_SEPERATOR);
				fullDisplayString.append(fields[i]);
			}
		}
		
		return fullDisplayString.toString();
	}
	
	//@@author A0111101N
	private Calendar mergeDateTime(Calendar date, Calendar time) {
		if(date == null) {
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
	
	private void checkValidDates(Calendar start, Calendar end) throws InvalidDateException{
		if(end == null) {
			return;
		}
		if (end.before(start) && start!= null) {
			throw new StorageExceptions.InvalidDateException(start,end);
		}
	}
	
}