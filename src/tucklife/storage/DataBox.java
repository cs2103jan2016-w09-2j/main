package tucklife.storage;

import java.util.Hashtable;

public class DataBox {
	
	private TaskList[] lists;
	private PrefsStorage prefs;
	private Hashtable<String, String> commands;
	
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
	
	public Hashtable<String, String> getCommands() {
		return commands;
	}
	
	public void setCommands(Hashtable<String, String> ht) {
		commands = ht;
	}
}
