package tucklife.storage;


public class IDNotFoundException extends Exception {
	
	public int errorID;
	public IDNotFoundException(int errorID) {
		this.errorID = errorID;
	}
}

class overloadException extends Exception {
	public int limit;
	public overloadException(int limit) {
		this.limit = limit;
	}
}

class nothingToUndoException extends Exception {
	
}

class nothingToRedoException extends Exception {
	
}
