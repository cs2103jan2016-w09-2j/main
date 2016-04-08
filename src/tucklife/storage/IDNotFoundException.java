package tucklife.storage;


public class IDNotFoundException extends Exception {
	
	public int errorID;
	public IDNotFoundException(int errorID) {
		this.errorID = errorID;
	}
}

class overloadException extends Exception {
	private int limit;
	private static final String RETURN_MESSAGE_FOR_OVERLOAD = "That day has been filled with %1$s tasks! It hit the limit! You should reschedule the task to another day. "
			+ "Alternatively, you can either change the overload limit or turn it off.";
	
	public overloadException(int limit) {
		this.limit = limit;
	}
	/*
	public int getLimit() {
		return limit;
	}*/
	public String getReturnMsg() {
		return String.format(RETURN_MESSAGE_FOR_OVERLOAD, this.limit);
	}
}

class nothingToUndoException extends Exception {
	private static final String RETURN_MESSAGE_FOR_NOTHING_TO_UNDO = "There is no previous action to undo!";
	public String getReturnMsg() {
		return RETURN_MESSAGE_FOR_NOTHING_TO_UNDO;
	}
}

class nothingToRedoException extends Exception {
	private static final String RETURN_MESSAGE_FOR_NOTHING_TO_REDO = "There is no previous action to redo!";
	public String getReturnMsg() {
		return RETURN_MESSAGE_FOR_NOTHING_TO_REDO;
	}
}

class invalidDateException extends Exception {
	private String errorMessage = "Date is invalid";
	
	public String getErrorMessage(){
		return errorMessage;
	}
}
