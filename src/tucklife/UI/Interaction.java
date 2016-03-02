package tucklife.UI;

import java.util.Scanner;
import tucklife.parser.Parser;
import tucklife.parser.ProtoTask;
import tucklife.storage.ExternalStorage;
import tucklife.storage.Storage;

public class Interaction {
	
	private static final String MESSAGE_COMMAND_PROMPT = "Input command";
	private static final String MESSAGE_WELCOME = "Welcome to Tucklife!";
	
	private static Scanner sc = new Scanner(System.in);
	private static Parser p = new Parser();
	private static Storage s = new Storage();
	private static ExternalStorage es = new ExternalStorage();
	
	public static void main(String[] args) {
		
		//initialisation
		System.out.println(MESSAGE_WELCOME);
		
		//program loop
		while (true) {
            System.out.println(MESSAGE_COMMAND_PROMPT);
            String command = sc.nextLine();
            
            ProtoTask pt = p.parse(command);
            
            if(pt.isError()){
            	System.out.println(pt.toString());
            }
            else{
            	if(pt.getCommand().equals("save")){
            		System.out.println(es.save(s.save()));
            	}
            	else{
	            	if(pt.getCommand().equals("load")){
	            		es.load();
	            		s.load(es.getLoadedLists());
	            	}
	            	else{
	            		System.out.println(s.parseCommand(pt));
	            	}
            	}
            }
            
            //String showResultToUser = getResults;
            //System.out.println(showResultToUser);
		}
		
	}
	
}
