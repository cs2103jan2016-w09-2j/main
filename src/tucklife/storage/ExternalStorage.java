package tucklife.storage;

import tucklife.parser.ProtoTask;

public class ExternalStorage {
	
	private static final String FILENAME_TODO = "todo.txt";
	private static final String FILENAME_DONE = "done.txt";
	private static final String FILENAME_RECUR = "recur.txt";
	
	private static final String MSG_LOAD_COMPLETE = "Data loaded successfully.";
	private static final String MSG_SAVE_COMPLETE = "Data saved successfully.";
	
	private static final String ERROR_LOAD = "Error loading files. New todo list has been created.";
	private static final String ERROR_SAVE = "Error saving files. Files have been saved to default location.";
	private static final String ERROR_SAVETO = "Error saving files to new location. Files have been saved to previous location.";
	
	private String targetFolder;
	private TaskList[] lists;
	private ListStorage todo, done;
	private HelpStorage help;
	private PrefsStorage prefs;
	
	public ExternalStorage(){
		prefs = new PrefsStorage();
		
		prefs.loadPreferences();
		targetFolder = prefs.getSavePath();
		
		todo = new ListStorage(targetFolder + FILENAME_TODO);
		done = new ListStorage(targetFolder + FILENAME_DONE);
		help = new HelpStorage();
		
	}
	
	public boolean load(){		
		lists = new TaskList[2];

		lists[0] = todo.getList();
		lists[1] = done.getList();	
		
		return (todo.getLoadStatus() & done.getLoadStatus() & help.load());
	}
	
	public TaskList[] getLoadedLists(){
		return lists;
	}
	
	// new load that uses a DataBox
	public DataBox getLoadedData(){
		DataBox db = new DataBox(lists, prefs);
		return db;
	}

	public String save(TaskList[] listsToSave){		
		
		boolean savedTodo = todo.normalSave(listsToSave[0]);
		boolean savedDone = done.normalSave(listsToSave[1]);
		
		if(!savedTodo | !savedDone){
			return ERROR_SAVE;
		}
		
		return MSG_SAVE_COMPLETE;
	}
	
	// new save that uses a DataBox
	public String saveData(DataBox db){
		TaskList[] listsToSave = db.getLists();
		prefs = db.getPrefs();
		
		boolean savedTodo = todo.normalSave(listsToSave[0]);
		boolean savedDone = done.normalSave(listsToSave[1]);
		
		boolean savedPrefs = prefs.savePreferences();
		
		if(!savedTodo | !savedDone | !savedPrefs){
			return ERROR_SAVE;
		}
		
		return MSG_SAVE_COMPLETE;
	}
	
	public String saveTo(DataBox db, String newPath){
		
		TaskList[] listsToSave = db.getLists();
		prefs = db.getPrefs();
		
		boolean savedTodo = todo.pathSave(newPath + FILENAME_TODO, listsToSave[0]);
		boolean savedDone = done.pathSave(newPath + FILENAME_DONE, listsToSave[1]);
		
		// saving in new place is successful
		if(savedDone && savedTodo){
			prefs.setSavePath(newPath);
			prefs.savePreferences();
			return MSG_SAVE_COMPLETE;
		
		// saving unsuccessful 
		} else{
			todo.pathSave(targetFolder + FILENAME_TODO, listsToSave[0]);
			done.pathSave(targetFolder + FILENAME_DONE, listsToSave[1]);
			return ERROR_SAVETO;
		}
	}
	
	public String getHelp(){
		return help.getHelp();
	}
	
	public String getDemo(ProtoTask pt){
		String command = pt.getDemoCommand();
		return help.getDemo(command);
	}
}
