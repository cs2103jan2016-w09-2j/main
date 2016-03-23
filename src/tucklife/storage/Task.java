package tucklife.storage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import tucklife.parser.ProtoTask;

public class Task {
	
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
	}
	
	/* unsure if needed
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
	}*/
	
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
		return this;
	}
	
	protected String display(){
		String displayString = "";
		displayString += id + ". ";
		displayString += name + " | ";
		displayString = addDateToDisplayString(displayString);
		displayString = addLocationToDisplayString(displayString);
		displayString = addPriorityToDisplayString(displayString);
		displayString = addCategoryToDisplayString(displayString);
		return displayString;
	}

	private String addCategoryToDisplayString(String displayString) {
		displayString = addAdditionalToDisplayString(displayString);
		return displayString;
	}

	private String addAdditionalToDisplayString(String displayString) {
		if (category != null) {
			displayString += "category: " + category + " | ";
		}
		return displayString;
	}

	private String addPriorityToDisplayString(String displayString) {
		if (priority != -1) {
			displayString += "priority: " + priority + " | ";
		}
		return displayString;
	}

	private String addLocationToDisplayString(String displayString) {
		if (location != null) {
			displayString += "location: " + location + " | ";
		}
		return displayString;
	}

	private String addDateToDisplayString(String displayString) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM HH:mm");	
		if (endDate != null && startDate == null) {
			displayString += "deadline: " + sdf.format(endDate.getTime()) + " | ";
		} else if (endDate != null && startDate != null) {
			displayString += "start: " + sdf.format(startDate.getTime()) + " | end: " + sdf.format(endDate.getTime()) + " | ";
		}
		return displayString;
	}
	
	protected String displayAll(){
		String displayString = display();
		if (additional != null) {
			displayString += "additional information: " + additional;
		}
		return displayString;
	}
	
	
	
}