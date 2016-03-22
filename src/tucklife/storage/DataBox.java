package tucklife.storage;

public class DataBox {
	
	private TaskList[] lists;
	private PrefsStorage prefs;
	
	public DataBox(TaskList[] l, PrefsStorage p){
		lists = l;
		prefs = p;
	}
	
	public TaskList[] getLists(){
		return lists;
	}
	
	public PrefsStorage getPrefs(){
		return prefs;
	}
}
