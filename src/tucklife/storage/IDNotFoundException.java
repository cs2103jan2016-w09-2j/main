package tucklife.storage;


public class IDNotFoundException extends Exception {
	
	public int errorID;
	public IDNotFoundException(int errorID) {
		this.errorID = errorID;
	}
}
