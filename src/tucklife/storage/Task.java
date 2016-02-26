package tucklife.storage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Task {
	
	String location;
	String priority;
	String category;
	String additional;
	String name;
	
	Calendar startDate; //if null, means that it is a task not event
	Calendar endDate; //either deadline or end time for event. if null, means it is a floating task
	
	boolean floating;
	
	int id;
	
	public Task(ProtoTask task){
		
		//create the Task
	}
	
	Task edit(ProtoTask task){
		return task;
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
		if (priority != null) {
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