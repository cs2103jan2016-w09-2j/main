//@@author a0124274l

package tucklife.UI;

import tucklife.parser.Parser;
import tucklife.parser.ProtoTask;
import tucklife.storage.ExternalStorage;
import tucklife.storage.Storage;
import tucklife.storage.DataBox;

public class FlowController {

	private static Parser p = new Parser();
	private static Storage s = new Storage();
	private static ExternalStorage es = new ExternalStorage();

	public FlowController() {
		es.load();
		DataBox db = es.getLoadedData();
		p.loadCommands(db.getCommands());
		s.load(db);
	}

	public String execute(String command){

		ProtoTask pt = p.parse(command);

		if(pt.isError()){
			return pt.toString();
		} else {
			
			String result, status;
			
			if (pt.getCommand().equals("save")) {
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
			} else if(pt.getCommand().equals("display") || pt.getCommand().equals("displaydone")){
				result = s.parseCommand(pt);
			} else{
				result = s.parseCommand(pt);
			}
			status = s.getStatus();
			executeSave();
			return status + "\n\n" + result;
		}
	}
	
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
}
