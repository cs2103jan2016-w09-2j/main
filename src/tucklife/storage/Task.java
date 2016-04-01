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
	
	public Task(ProtoTask task){
		//create the Task
		this.location = task.getLocation();
		this.priority = task.getPriority();
		this.category = task.getCategory();
		this.additional = task.getAdditional();
		this.name = task.getTaskDesc();
		this.startDate = task.getStartDate();
		this.endDate = task.getEndDate();
		this.floating = startDate == null && endDate == null; //task.isFloating();
		this.id = globalID;	
		this.queueID = -1;
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
	
	protected Task edit(ProtoTask task){
		//edit task
		this.location = task.getLocation() == null ? this.location : task.getLocation();
		this.priority = task.getPriority() == -1 ? this.priority : task.getPriority();
		this.category = task.getCategory() == null ? this.category : task.getCategory();
		this.additional = task.getAdditional() == null ? this.additional : task.getAdditional();
		this.name = task.getTaskDesc() == null ? this.name : task.getTaskDesc();
		this.startDate = task.getStartDate() == null ? this.startDate : task.getStartDate();
		this.endDate = task.getEndDate() == null ? this.endDate : task.getEndDate();
		this.floating = startDate == null && endDate == null; //task.isFloating();
		this.id = task.getId() == -1 ? this.id : task.getId();
		log.log( Level.FINE, "Task has been edited via ProtoTask");
		return this;
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
	
}