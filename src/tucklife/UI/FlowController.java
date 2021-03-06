//@@author A0124274L

package tucklife.UI;

import tucklife.parser.Parser;
import tucklife.parser.ProtoTask;
import tucklife.storage.external.ExternalStorage;
import tucklife.storage.internal.Storage;
import tucklife.storage.internal.StorageExceptions.InvalidDateException;
import tucklife.storage.internal.StorageExceptions.NothingToRedoException;
import tucklife.storage.internal.StorageExceptions.NothingToUndoException;
import tucklife.storage.internal.StorageExceptions.OverloadException;
import tucklife.storage.DataBox;

public class FlowController {

	private static Parser p = new Parser();
	private static Storage s;
	private static ExternalStorage es = new ExternalStorage();
	
	//@@author A0111101N
	private enum COMMAND_TYPE {
		ADD, DISPLAY, COMPLETE, DISPLAYDONE, DELETE, EDIT, INVALID, QUEUE, SETLIMIT, UNDO, REDO, UNCOMPLETE
	}

	//@@author A0124274L
	public FlowController() {
		es.load();
		DataBox db = es.getLoadedData();
		p.loadCommands(db.getCommands());
		//s.load(db);
		s = new Storage(db);
	}
	
	public String statusOnly(){
		return s.getStatus().substring(10);
	}

	public String execute(String command) {

		ProtoTask pt = p.parse(command);

		String result, status;

		if (pt.isError()) {
			result = pt.toString();
		} else if (pt.getCommand().equals("save")) {
			result = executeSave();
		} else if (pt.getCommand().equals("saveto")) {
			result = executeSaveTo(pt.getPath());				
		} else if (pt.getCommand().equals("help")) {
			result = es.getHelp();
		} else if (pt.getCommand().equals("demo")) {
			result = es.getDemo(pt);
		} else if (pt.getCommand().equals("change")) {
			result = pt.getChangeMessage() + "\n" + executeSave();
		} else if (pt.getCommand().equals("exit")) {
			executeSave();
			System.exit(0);
			return null;
		} else{
			result = parseCommand(pt);
		}
		status = s.getStatus();
		executeSave();
		return status + "\n\n" + result;
	}
	
	//@@author A0111101N
	public String parseCommand(ProtoTask pt) {
		COMMAND_TYPE ct = determineCommandType(pt.getCommand());
		String returnMessage = parseCommand(pt,ct);
		return returnMessage;
	}
	
	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
		if (commandTypeString.equalsIgnoreCase("add")) {
			return COMMAND_TYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase("complete")) {
			return COMMAND_TYPE.COMPLETE;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return COMMAND_TYPE.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("displaydone")) {
			return COMMAND_TYPE.DISPLAYDONE;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return COMMAND_TYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("edit")) {
			return COMMAND_TYPE.EDIT;
		} else if (commandTypeString.equalsIgnoreCase("queue")) {
			return COMMAND_TYPE.QUEUE;
		} else if (commandTypeString.equalsIgnoreCase("setlimit")) {
			return COMMAND_TYPE.SETLIMIT;
		} else if (commandTypeString.equalsIgnoreCase("undo")) {
			return COMMAND_TYPE.UNDO;
		} else if (commandTypeString.equalsIgnoreCase("uncomplete")) {
			return COMMAND_TYPE.UNCOMPLETE;
		} else if (commandTypeString.equalsIgnoreCase("redo")) {
			return COMMAND_TYPE.REDO;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}
	
	private static String parseCommand(ProtoTask pt, COMMAND_TYPE commandType) throws Error {
		switch (commandType) {
		case ADD :
			try {
				return s.add(pt);
			} catch (OverloadException e) {
				return e.getReturnMsg();
			} catch (InvalidDateException e) {
				return e.getReturnMsg();
			}
		case COMPLETE :
			return s.complete(pt.getId());
		case DISPLAY :
			return s.display(pt, "toDoList");
		case DISPLAYDONE :
			return s.display(pt, "doneList");
		case DELETE :
			return s.delete(pt.getId());
		case EDIT :
			try {
				return s.edit(pt.getId(), pt);
			} catch (OverloadException e) {
				return e.getReturnMsg();
			} catch (InvalidDateException e) {
				return e.getReturnMsg();
			}
		case QUEUE :
			return s.queue(pt.getId(), pt.getPosition());
		case SETLIMIT :
			return s.setLimit(pt.getLimit());
		case UNCOMPLETE :
			return s.uncomplete(pt.getId());
		case UNDO :
			try {
				return s.undo();
			} catch (NothingToUndoException e) {
				return e.getReturnMsg();
			}
		case REDO :
			try {
				return s.redo();
			} catch (NothingToRedoException e) {
				return e.getReturnMsg();
			}
		default:
			throw new Error("Unrecognized command type");
		}
	}
	
	
	// @@author A0121352X
	public String executeSave() {
		DataBox db = s.save();
		db.setCommands(p.getCommands());
		return es.saveData(db);
	}
	
	public String executeSaveTo(String path) {
		DataBox db = s.save();
		db.setCommands(p.getCommands());
		return es.saveTo(db, path);
	}
	
	//@@author A0111101N
	//for testing purposes only
	public static Storage getStorage(){
		return s;
	}
}
