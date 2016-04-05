//@@author a0111101n
package tucklife.storage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import tucklife.parser.ProtoTask;

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
	
	public Task(ProtoTask task) throws invalidDateException{
		//create the Task
		this.location = task.getLocation();
		this.priority = task.getPriority();
		this.category = task.getCategory();
		this.additional = task.getAdditional();
		this.name = task.getTaskDesc();
		this.startDate = combineDateTime(task.getStartDate(),task.getStartTime());
		this.endDate = combineDateTime(task.getEndDate(),task.getEndTime());
		checkValidDates(startDate, endDate);
		this.floating = startDate == null && endDate == null; //task.isFloating();
		this.id = globalID;
		this.queueID = task.getPosition();
		globalID++;
		log.log( Level.FINE, "Task has been created via ProtoTask");
	}
	
	/* unsure if needed*/
	public Task(Task task){
		//create the Task
		this.location = task.getLocation();
		this.priority = task.getPriority();
		this.category = task.getCategory();
		this.additional = task.getAdditional();
		this.name = task.getName();
		this.startDate = task.getStartDate();
		this.endDate = task.getEndDate();
		this.floating = startDate == null && endDate == null; //task.isFloating();
		this.id = task.getId();	
		this.queueID = task.getQueueID();
	}//*/
	
	protected Task edit(ProtoTask task) throws invalidDateException{
		//edit task
		this.location = task.getLocation() == null ? this.location : task.getLocation();
		this.priority = task.getPriority() == -1 ? this.priority : task.getPriority();
		this.category = task.getCategory() == null ? this.category : task.getCategory();
		this.additional = task.getAdditional() == null ? this.additional : task.getAdditional();
		this.name = task.getTaskDesc() == null ? this.name : task.getTaskDesc();
		
		this.startDate = task.getStartDate() == null ? this.startDate : task.getStartDate();
		this.endDate = task.getEndDate() == null ? this.endDate : task.getEndDate();
		
		if(task.getStartDate() == null && task.getEndDate() == null) { //only do such changes if there is a time but no date
			
			if(task.getStartTime() == null && task.getEndTime()!=null) { //current task must be a deadline else it does not make sense
				if(this.startDate != null) {
					throw new invalidDateException();
				} else {
					if(this.endDate == null) { //current task is a floating task
						this.endDate = task.getEndTime(); //take deadline to be nearest time, similiar to the way add handles a single time
					} else {
						this.endDate = combineDateTime(this.endDate, task.getEndTime()); //change the time of the deadline
					}
				} 
			}
			
			if(task.getStartTime()!=null && task.getEndTime()!=null) { //new edited task becomes an event
				if(isFloating()) { //cant created an event from a floating task since you dont know the dates
					throw new invalidDateException();
				}
				
				if(this.startDate != null && this.startDate != this.endDate) {
					this.startDate = combineDateTime(this.startDate,task.getStartTime());
					this.endDate = combineDateTime(this.endDate,task.getEndTime());
				} else {
					if(onSameDay(task.getStartTime(),task.getEndTime())) {
						this.startDate = combineDateTime(this.startDate,task.getStartTime());
						this.endDate = combineDateTime(this.endDate,task.getEndTime());
					} else {
						this.startDate = combineDateTime(this.startDate,task.getStartTime());
						this.endDate = combineDateTime(tomorrow(this.endDate),task.getEndTime());
					}
				}
			}
		}
		
		if(task.getEndTime() != null) {
			this.endDate = combineDateTime(this.endDate,task.getEndTime());
		}
		checkValidDates(startDate, endDate);
		
		this.floating = startDate == null && endDate == null; //task.isFloating();
		this.id = task.getId() == -1 ? this.id : task.getId();
		log.log( Level.FINE, "Task has been edited via ProtoTask");
		return this;
	}
	
	private Calendar tomorrow(Calendar endDate) {
		endDate.add(Calendar.DATE, 1);
		return endDate;
	}

	private boolean onSameDay(Calendar startTime, Calendar endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
		return sdf.format(startTime.getTime()).equals(sdf.format(endTime.getTime()));
	}

	protected String display(){
		StringBuilder displayString = new StringBuilder();
		
		// display order:
		// id. name | date | location | priority
		
		displayString.append(idField());
		displayString.append(" ");
		displayString.append(name);
		
		String[] fields = new String[4];
		
		fields[0] = dateField();
		fields[1] = locationField();
		fields[2] = priorityField();
		
		for(int i = 0; i < 3; i++){
			if(fields[i] != null){
				displayString.append(" | ");
				displayString.append(fields[i]);
			}
		}
		
		return displayString.toString();
	}
	
	protected boolean containsExact(String searchKey) {
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
	
	protected boolean containsPartial(String searchKey) {
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
		return Integer.toString(id) + ".";
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
	
	protected String displayAll(){
		String displayString = display();
		String[] otherStuff = new String[2];
		
		otherStuff[0] = categoryField();
		otherStuff[1] = additionalField();
		
		for(int i = 0; i < 2; i++){
			if(otherStuff[i] != null){
				displayString += " | " + otherStuff[i];
			}
		}
		
		return displayString;
	}
	
	private Calendar combineDateTime(Calendar date, Calendar time) {
		if(date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.YEAR, date.get(Calendar.YEAR));
		c.set(Calendar.MONTH, date.get(Calendar.MONTH));
		c.set(Calendar.DATE, date.get(Calendar.DATE));
		
		c.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		
		return c;
	}
	
	private void checkValidDates(Calendar start, Calendar end) throws invalidDateException{
		if(end == null) {
			return;
		}
		if (end.before(start) && start!= null) {
			throw new invalidDateException();
		}
	}
	
}