package tucklife.storage;

import tucklife.parser.ProtoTask;

public class ExternalStorage {
	
	private static final String FILENAME_TODO = "todo.txt";
	private static final String FILENAME_DONE = "done.txt";
	
	private static final String MSG_SAVE_COMPLETE = "Files saved.";
	
	private static final String ERROR_SAVE = "Error saving files. Files have been saved to TuckLife's folder.";
	private static final String ERROR_SAVETO = "Error saving files to new location. Files have been saved to previous location.";
	
	private String targetFolder;
	private TaskList[] lists;
	private ListStorage todo, done;
	private HelpStorage help;
	private PrefsStorage prefs;
	private CommandStorage commands;
	
	public ExternalStorage(){
		prefs = new PrefsStorage();
		
		prefs.loadPreferences();
		targetFolder = prefs.getSavePath();
		
		todo = new ListStorage(targetFolder + FILENAME_TODO);
		done = new ListStorage(targetFolder + FILENAME_DONE);
		help = new HelpStorage();
		
		commands = new CommandStorage();
		commands.loadCommands();
	}
	
	public boolean load(){		
		lists = new TaskList[2];

		lists[0] = todo.getList();
		lists[1] = done.getList();	
		
		return (todo.getLoadStatus() & done.getLoadStatus() & help.load());
	}
	
	// new load that uses a DataBox
	public DataBox getLoadedData(){
		DataBox db = new DataBox(lists, prefs);
		db.setCommands(commands.getCommands());
		return db;
	}
	
	// new save that uses a DataBox
	public String saveData(DataBox db){
		TaskList[] listsToSave = db.getLists();
		prefs = db.getPrefs();
		
		boolean savedTodo = todo.normalSave(listsToSave[0]);
		boolean savedDone = done.normalSave(listsToSave[1]);
		
		boolean savedPrefs = prefs.savePreferences();
		boolean savedCommands = commands.saveCommands(db.getCommands());
		
		if(!savedTodo | !savedDone | !savedPrefs | !savedCommands){
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
