package tucklife.storage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

public class HelpStorage {
	
	public static final String FILENAME_HELP = "help.txt";
	public static final String FILENAME_DEMO = "demo.txt";
	
	private Hashtable<String, ArrayList<String>> demoDirectory;
	private ArrayList<String> helpDirectory;
	
	public HelpStorage(){
		helpDirectory = new ArrayList<String>();
		demoDirectory = new Hashtable<String, ArrayList<String>>();
		
		loadHelp();
		loadDemo();
	}
	
	private void loadHelp(){
		
		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
		
		try{
			fis = new FileInputStream(FILENAME_HELP);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
			br.readLine(); // removes first line
			
			while(br.ready()){
				String[] nextLine = br.readLine().split(",");
				helpDirectory.add(nextLine[1] + ": ");
				helpDirectory.add(nextLine[2]);
			}
			
			br.close();
			isr.close();
			fis.close();
			
		} catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	private void loadDemo(){
		
		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
		
		try{
			fis = new FileInputStream(FILENAME_DEMO);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
			br.readLine(); // removes first line
			
			String currCommand = null;
			ArrayList<String> exampleTable = new ArrayList<String>();
			
			while(br.ready()){
				String[] nextLine = br.readLine().split(",");
				
				if(currCommand == null){
					currCommand = nextLine[0];
					exampleTable = new ArrayList<String>();
					exampleTable.add(nextLine[1]);
					exampleTable.add(nextLine[2]);
					
				} else if(currCommand.equals(nextLine[0])){
					exampleTable.add(nextLine[1]);
					exampleTable.add(nextLine[2]);
				} else{
					demoDirectory.put(currCommand, exampleTable);
					currCommand = nextLine[0];
					exampleTable = new ArrayList<String>();
					exampleTable.add(nextLine[1]);
					exampleTable.add(nextLine[2]);
				}
			}
			
			demoDirectory.put(currCommand, exampleTable);
			
			br.close();
			isr.close();
			fis.close();
			
		} catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	protected String getHelp(){
		
		StringBuilder helpString = new StringBuilder();
		
		for(int i = 0; i < helpDirectory.size(); i += 2){
			helpString.append(helpDirectory.get(i) + helpDirectory.get(i+1));
			helpString.append("\n");
		}
		
		return helpString.toString();
	}
	
	protected String getDemo(String command){
		
		StringBuilder demoString = new StringBuilder();
		ArrayList<String> demoTable = demoDirectory.get(command);
		
		for(int i = 0; i < demoTable.size(); i++){
			demoString.append(demoTable.get(i));
			demoString.append("\n");
		}
		
		return demoString.toString();
	}
}
