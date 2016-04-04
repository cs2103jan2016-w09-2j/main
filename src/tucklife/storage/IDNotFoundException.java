package tucklife.storage;


public class IDNotFoundException extends Exception {
	
	public int errorID;
	public IDNotFoundException(int errorID) {
		this.errorID = errorID;
	}
}

class overloadException extends Exception {
	private int limit;
	
	public overloadException(int limit) {
		this.limit = limit;
	}
	
	public int getLimit() {
		return limit;
	}
}

class nothingToUndoException extends Exception {
	
}

class nothingToRedoException extends Exception {
	
}

class invalidDateException extends Exception {
	
}
