package tucklife.storage;

public class ExternalStorage {
	
	public static final String FILENAME_TODO = "todo.txt";
	public static final String FILENAME_DONE = "done.txt";
	public static final String FILENAME_HELP = "help.txt";
	public static final String FILENAME_DEMO = "demo.txt";
	public static final String FILENAME_RECUR = "recur.txt";
	public static final String FILENAME_PREFS = "prefs.txt";
	public static final String FILENAME_PATH = "path.txt";
	
	public static final String MSG_LOAD_COMPLETE = "Data loaded successfully.";
	public static final String MSG_SAVE_COMPLETE = "Data saved successfully.";
	
	public static final String ERROR_LOAD = "Error loading files. New todo list has been created.";
	public static final String ERROR_SAVE = "Error saving files. Files have been saved to default location.";
	
	private String targetFolder;
	private TaskList[] lists;
	private ListStorage todo, done;
	
	public ExternalStorage(){
		// load save-to path - assumption now is that file is in same directory as TuckLife.
		targetFolder = "";
		todo = new ListStorage(targetFolder + FILENAME_TODO);
		done = new ListStorage(targetFolder + FILENAME_DONE);
	}
	
	public boolean load(){		
		lists = new TaskList[2];

		lists[0] = todo.getList();
		lists[1] = done.getList();
		
		return (todo.getLoadStatus() & done.getLoadStatus());
	}
	
	public TaskList[] getLoadedLists(){
		return lists;
	}

	public String save(TaskList[] listsToSave){		
		
		boolean savedTodo = todo.normalSave(listsToSave[0]);
		boolean savedDone = done.normalSave(listsToSave[1]);
		
		if(!savedTodo | !savedDone){
			return ERROR_SAVE;
		}
		
		return MSG_SAVE_COMPLETE;
	}
	
	// meant for testing in a folder with some test cases
	protected void testSetup(){
		targetFolder = "C:\\Users\\Ryan\\Desktop\\Holding Area\\";
		todo = new ListStorage(targetFolder + FILENAME_TODO);
		done = new ListStorage(targetFolder + FILENAME_DONE);
	}
}
