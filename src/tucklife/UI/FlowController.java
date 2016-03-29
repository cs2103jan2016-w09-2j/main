package tucklife.UI;

import tucklife.parser.Parser;
import tucklife.parser.ProtoTask;
import tucklife.storage.ExternalStorage;
import tucklife.storage.Storage;

public class FlowController {

	private static Parser p = new Parser();
	private static Storage s = new Storage();
	private static ExternalStorage es = new ExternalStorage();


	public void execute(String command){

		ProtoTask pt = p.parse(command);

		if(pt.isError()){
			System.out.println(pt.toString());
		}
		else{
			if(pt.getCommand().equals("save")){
				System.out.println(es.saveData(s.save()));
			} else if(pt.getCommand().equals("load")){
				es.load();
				s.load(es.getLoadedData());
			} else if(pt.getCommand().equals("help")){
				System.out.println(es.getHelp());
			} else if(pt.getCommand().equals("demo")){
				System.out.println(es.getDemo(pt));
			} else if (pt.getCommand().equals("exit")) {
				System.out.println(es.saveData(s.save()));
				System.exit(0);
			} else{
				System.out.println(s.parseCommand(pt));
			}
		}

	}
}
