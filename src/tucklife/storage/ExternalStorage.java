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
	
	private String targetFolder;
	private TaskList[] lists;
	private ListStorage todo, done;
	private HelpStorage help;
	private PrefsStorage prefs;
	
	public ExternalStorage(){
		// load save-to path - assumption now is that file is in same directory as TuckLife.
		targetFolder = "";
		todo = new ListStorage(targetFolder + FILENAME_TODO);
		done = new ListStorage(targetFolder + FILENAME_DONE);
		help = new HelpStorage();
		prefs = new PrefsStorage();
	}
	
	public boolean load(){		
		lists = new TaskList[2];

		lists[0] = todo.getList();
		lists[1] = done.getList();	
		
		return (todo.getLoadStatus() & done.getLoadStatus() & help.load() & prefs.loadPreferences());
	}
	
	public TaskList[] getLoadedLists(){
		return lists;
	}
	
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
	
	public String getHelp(){
		return help.getHelp();
	}
	
	public String getDemo(ProtoTask pt){
		String command = pt.getDemoCommand();
		return help.getDemo(command);
	}
}
