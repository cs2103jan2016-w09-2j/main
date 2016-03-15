package tucklife.storage;

import java.util.Calendar;

public class ProtoTaskStubForStorage {
	
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
	
	public ProtoTaskStubForStorage(){
		
	}


	public ProtoTaskStubForStorage(String command, String taskDesc, String location, String category, String additional,
			int id, Calendar startDate, Calendar endDate) {
		this.command = command;
		this.taskDesc = taskDesc;
		this.location = location;
		this.category = category;
		this.additional = additional;
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
	}


	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAdditional() {
		return additional;
	}

	public void setAdditional(String additional) {
		this.additional = additional;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public String getSortCrit() {
		return sortCrit;
	}

	public void setSortCrit(String sortCrit) {
		this.sortCrit = sortCrit;
	}

	public String getDemoCommand() {
		return demoCommand;
	}

	public void setDemoCommand(String demoCommand) {
		this.demoCommand = demoCommand;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	
	
}
