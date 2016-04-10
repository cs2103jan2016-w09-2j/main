// @@author A0121352X
package tucklife.storage.external;

import java.util.logging.Level;
import java.util.logging.Logger;

import tucklife.parser.ProtoTask;
import tucklife.storage.DataBox;
import tucklife.storage.TaskList;

public class ExternalStorage {
	
	private static final Logger EXTERNAL_LOG = Logger.getLogger(ExternalStorage.class.getName());
	
	private static final String FILENAME_TODO = "todo.txt";
	private static final String FILENAME_DONE = "done.txt";
	
	private static final String MSG_SAVE_COMPLETE = "Files saved.";
	
	private static final String ERROR_SAVE = "Error saving files. Files have been saved to TuckLife's folder.";
	private static final String ERROR_SAVETO = "Error saving files to new location. Files have been saved to previous location.";
	
	private static final String LOG_PREFS_COMPLETE = "Preferences loaded successfully.";
	private static final String LOG_PREFS_FAILED = "Using a new default preference file.";
	private static final String LOG_PREFS_SAVEPATH = "Detected savepath: $1%s";
	private static final String LOG_OTHERS_COMPLETE = "Files loaded successfully.";
	private static final String LOG_OTHERS_FAILED = "Error in loading files.";
	private static final String LOG_DATABOX_CREATED = "DataBox created successfully.";
	private static final String LOG_SAVETO_SUCCESS = "Files saved in $1%s successfully.";
	private static final String LOG_DEMO_ACCESS = "Accessing demo for: $1%s";
	
	private String targetFolder;
	private TaskList[] lists;
	private ListStorage todo, done;
	private HelpStorage help;
	private PrefsStorage prefs;
	private CommandStorage commands;
	
	public ExternalStorage(){
		prefs = new PrefsStorage();
		
		boolean prefsLoaded = prefs.loadPreferences();
		
		if(prefsLoaded){
			EXTERNAL_LOG.log(Level.FINE, LOG_PREFS_COMPLETE);
		} else{
			EXTERNAL_LOG.log(Level.WARNING, LOG_PREFS_FAILED);
		}
		
		targetFolder = prefs.getSavePath();
		assert(targetFolder != null);
		EXTERNAL_LOG.log(Level.FINE, String.format(LOG_PREFS_SAVEPATH, targetFolder));
		
		todo = new ListStorage(targetFolder + FILENAME_TODO);
		done = new ListStorage(targetFolder + FILENAME_DONE);
		help = new HelpStorage();
		commands = new CommandStorage();
	}
	
	// load command - return true on everything okay
	public boolean load(){		
		lists = new TaskList[2];

		lists[0] = todo.getList();
		lists[1] = done.getList();
		
		assert(lists[0] != null);
		assert(lists[1] != null);
		
		boolean loadStatus = todo.getLoadStatus() & done.getLoadStatus() & help.load() & commands.loadCommands();
		
		if(loadStatus){
			EXTERNAL_LOG.log(Level.FINE, LOG_OTHERS_COMPLETE);
		} else{
			EXTERNAL_LOG.log(Level.WARNING, LOG_OTHERS_FAILED);
		}
		
		return loadStatus;
	}
	
	// used to retrieve loaded data in the form of a DataBox
	public DataBox getLoadedData(){
		DataBox db = new DataBox(lists, prefs, commands);
		EXTERNAL_LOG.log(Level.FINE, LOG_DATABOX_CREATED);
		return db;
	}
	
	// normal save command
	public String saveData(DataBox db){
		
		assert(db != null);
		TaskList[] listsToSave = db.getLists();
		prefs = db.getPrefs();
		
		assert(listsToSave[0] != null);
		assert(listsToSave[1] != null);
		assert(prefs != null);
		
		boolean savedTodo = todo.normalSave(listsToSave[0]);
		boolean savedDone = done.normalSave(listsToSave[1]);
		
		boolean savedPrefs = prefs.savePreferences();
		boolean savedCommands = commands.saveCommands(db.getCommands());
		
		if(!savedTodo | !savedDone | !savedPrefs | !savedCommands){
			EXTERNAL_LOG.log(Level.WARNING, ERROR_SAVE);
			return ERROR_SAVE;
		}
		
		EXTERNAL_LOG.log(Level.FINE, MSG_SAVE_COMPLETE);
		return MSG_SAVE_COMPLETE;
	}
	
	// special save command for use during saveto
	public String saveTo(DataBox db, String newPath){
		
		assert(db != null);
		assert(newPath != null);
		
		TaskList[] listsToSave = db.getLists();
		prefs = db.getPrefs();
		
		assert(listsToSave[0] != null);
		assert(listsToSave[1] != null);
		assert(prefs != null);
		
		boolean savedTodo = todo.pathSave(newPath + FILENAME_TODO, listsToSave[0]);
		boolean savedDone = done.pathSave(newPath + FILENAME_DONE, listsToSave[1]);
		
		// saving in new place is successful
		if(savedDone && savedTodo){
			prefs.setSavePath(newPath);
			prefs.savePreferences();
			EXTERNAL_LOG.log(Level.FINE, String.format(LOG_SAVETO_SUCCESS, newPath));
			return MSG_SAVE_COMPLETE;
		
		// saving unsuccessful 
		} else{
			todo.pathSave(targetFolder + FILENAME_TODO, listsToSave[0]);
			done.pathSave(targetFolder + FILENAME_DONE, listsToSave[1]);
			EXTERNAL_LOG.log(Level.WARNING, ERROR_SAVETO);
			return ERROR_SAVETO;
		}
	}
	
	// help and demo retrieval functions
	public String getHelp(){
		return help.getHelp();
	}
	
	public String getDemo(ProtoTask pt){
		String command = pt.getDemoCommand();
		EXTERNAL_LOG.log(Level.FINE, String.format(LOG_DEMO_ACCESS, command));
		return help.getDemo(command);
	}
}
