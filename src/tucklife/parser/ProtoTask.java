package tucklife.parser;

import java.util.Calendar;

public class ProtoTask {
	
	private String command;
	private String taskDesc;
	private String location;
	private String category;
	private String additional;
	private String searchKey;
	private String sortCrit;
	private String demoCommand;
	private String path;
	
	private int priority = -1;
	private int id = -1;
	private int sortOrder = -1;
	
	private Calendar startDate;
	private Calendar endDate;
	
	private boolean isError;
	private String errorMessage;
	
	/*****************************
	 * Constructor for ProtoTask *
	 *****************************/
	public ProtoTask(String commandType) {
		if (commandType.equalsIgnoreCase("error")) {
			isError = true;
		} else {
			isError = false;
			command = commandType;
		}
	}
	
	/**************************
	 * Getters for parameters *
	 **************************/
	public String getCommand() {
		return command;
	}
	
	public String getTaskDesc() {
		return taskDesc;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getAdditional() {
		return additional;
	}
	
	public String getSearchKey() {
		return searchKey;
	}
	
	public String getSortCrit() {
		return sortCrit;
	}
	
	public String getDemoCommand() {
		return demoCommand;
	}
	
	public String getPath() {
		return path;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public int getId() {
		return id;
	}
	
	public int getSortOrder() {
		return sortOrder;
	}
	
	public Calendar getStartDate() {
		return startDate;
	}
	
	public Calendar getEndDate() {
		return endDate;
	}
	
	public boolean isError() {
		return isError;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	/**************************
	 * Setters for parameters *
	 **************************/
	public void setTaskDesc(String td) {
		taskDesc = td;
	}
	
	public void setLocation(String l) {
		location = l;
	}
	
	public void setCategory(String c) {
		category = c;
	}
	
	public void setAdditional(String a) {
		additional = a;
	}
	
	public void setSearchKey(String sk) {
		searchKey = sk;
	}
	
	public void setSortCrit(String sc) {
		sortCrit = sc;
	}
	
	public void setDemoCommand(String dc) {
		demoCommand = dc;
	}
	
	public void setPath(String p) {
		path = p;
	}

	public void setPriority(int p) {
		priority = p;
	}
	
	public void setId(int i) {
		id = i;
	}
	
	public void setSortOrder(int so) {
		sortOrder = so;
	}
	
	public void setStartDate(Calendar sd) {
		startDate = sd;
	}
	
	public void setEndDate(Calendar ed) {
		endDate = ed;
	}
	
	public void setErrorMessage(String em) {
		isError = true;
		errorMessage = em;
	}
	
	/*****************
	 * Other methods *
	 *****************/
	public String toString() {
		if (isError) {
			return "Error: " + errorMessage + "\n";
		} else {
			String toDisplay = "Command type: " + command + "\n";
			toDisplay += "Parameters: \n";

			if (taskDesc != null) {
				toDisplay += "Task description: " + taskDesc + "\n";
			}

			if (location != null) {
				toDisplay += "Location: " + location + "\n";
			}

			if (category != null) {
				toDisplay += "Category: " + category + "\n";
			}

			if (additional != null) {
				toDisplay += "Additional information: " + additional + "\n";
			}

			if (searchKey != null) {
				toDisplay += "Search keyword: " + searchKey + "\n";
			}

			if (sortCrit != null) {
				toDisplay += "Sort criteria: " + sortCrit + "\n";
			}

			if (demoCommand != null) {
				toDisplay += "Demo command: " + demoCommand + "\n";
			}


			if (path != null) {
				toDisplay += "Path: " + path + "\n";
			}

			if (priority != -1) {
				toDisplay += "Priority: " + priority + "\n";
			}

			if (id != -1) {
				toDisplay += "ID: " + id + "\n";
			}

			if (sortOrder != -1) {
				toDisplay += "Sort order: " + sortOrder + "\n";
			}

			if (startDate != null) {
				toDisplay += "Start date: " + startDate + "\n";
			}

			if (endDate != null) {
				toDisplay += "End date: " + endDate + "\n";
			}

			return toDisplay;
		}
	}
}
