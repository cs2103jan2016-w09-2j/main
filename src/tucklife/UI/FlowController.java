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

	public void execute(String command){

		ProtoTask pt = p.parse(command);

		if(pt.isError()){
			System.out.println(pt.toString());
		} else {
			if (pt.getCommand().equals("save")) {
				executeSave();
			} else if (pt.getCommand().equals("help")) {
				System.out.println(es.getHelp());
			} else if (pt.getCommand().equals("demo")) {
				System.out.println(es.getDemo(pt));
			} else if (pt.getCommand().equals("change")) {
				System.out.println(pt.getChangeMessage());
			} else if (pt.getCommand().equals("exit")) {
				executeSave();
				System.exit(0);
			} else {
				System.out.println(s.parseCommand(pt));
			}
		}
	}
	
	public void executeSave() {
		DataBox db = s.save();
		db.setCommands(p.getCommands());
		System.out.println(es.saveData(db));
	}
}
