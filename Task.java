package cs2103tut;

import java.util.Date;

public class Task {
	
	String location;
	String priority;
	String category;
	String additional;
	String name;
	
	Date startDate; //if null, means that it is a task not event
	Date endDate; //either deadline or end time for event. if null, means it is a floating task
	
	boolean floating;
	
	int id;
	
	public Task(){
		//create the Task
	}
	
	Task edit(Task task){
		return task;
	}
	
	String display(){
		String displayString = "";
		displayString += id + ". ";
		displayString += name + " | ";
		displayString += "date: " + endDate + " | "; //time??????
		displayString += "location: " + location + " | ";
		displayString += "priority: " + priority + " | ";
		displayString += "category: " + category;
		return displayString;
	}
	
	String displayAll(){
		String displayString = display();
		displayString += " | " + "additional information: " + additional;
		return displayString;
	}
	
	
	
}