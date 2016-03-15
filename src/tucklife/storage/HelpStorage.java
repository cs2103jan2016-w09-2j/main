package tucklife.storage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class HelpStorage {
	
	public static final String FILENAME_HELP = "help.txt";
	public static final String FILENAME_DEMO = "demo.txt";
	
	private Hashtable<String, Hashtable<String, String>> demoDirectory;
	private Hashtable<String, String> helpDirectory;
	
	public HelpStorage(){
		helpDirectory = new Hashtable<String, String>();
		demoDirectory = new Hashtable<String, Hashtable<String, String>>();
		
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
				helpDirectory.put(nextLine[1], nextLine[2]);
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
			Hashtable<String, String> exampleTable = new Hashtable<String, String>();
			
			while(br.ready()){
				String[] nextLine = br.readLine().split(",");
				
				if(currCommand == null){
					currCommand = nextLine[0];
					exampleTable = new Hashtable<String, String>();
					exampleTable.put(nextLine[1], nextLine[2]);
					
				} else if(currCommand.equals(nextLine[0])){
					exampleTable.put(nextLine[1], nextLine[2]);
				} else{
					demoDirectory.put(currCommand, exampleTable);
					currCommand = nextLine[0];
					exampleTable = new Hashtable<String, String>();
					exampleTable.put(nextLine[1], nextLine[2]);
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
		
		helpDirectory.
	}
}
