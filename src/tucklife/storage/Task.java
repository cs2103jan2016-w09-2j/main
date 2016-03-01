package tucklife.storage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import tucklife.parser.ProtoTask;

public class Task {
	
	String location;
	int priority;
	String category;
	String additional;
	String name;
	
	Calendar startDate; //if null, means that it is a task not event
	Calendar endDate; //either deadline or end time for event. if null, means it is a floating task
	
	boolean floating;
	
	static int globalID = 1;

	int id;
	
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
		globalID++;
	}
	
	Task edit(ProtoTask task){
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
	
	String display(){
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
			displayString += "category: " + category;
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
			displayString += "start: " + sdf.format(startDate.getTime()) + "end:" + sdf.format(endDate.getTime()) + " | ";
		}
		return displayString;
	}
	
	String displayAll(){
		String displayString = display();
		if (additional != null) {
			displayString += " | " + "additional information: " + additional;
		}
		return displayString;
	}
	
	
	
}