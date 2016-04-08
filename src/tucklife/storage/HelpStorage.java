// @@author A0121352X
package tucklife.storage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

public class HelpStorage {
	
	public static final String FILENAME_HELP = "help.txt";
	public static final String FILENAME_DEMO = "demo.txt";
	
	public static final String DEMO_LINE1 = "Command: %1$s";
	public static final String DEMO_LINE2 = "Result: %1$s";
	
	private Hashtable<String, ArrayList<String>> demoDirectory;
	private ArrayList<String> helpDirectory;
	
	public HelpStorage(){
		helpDirectory = new ArrayList<String>();
		demoDirectory = new Hashtable<String, ArrayList<String>>();
	}
	
	public boolean load(){
		boolean helpLoaded = loadHelp();
		boolean demoLoaded = loadDemo();
		
		return helpLoaded && demoLoaded;
	}
	
	private boolean loadHelp(){
		
		InputStream is;
		InputStreamReader isr;
		BufferedReader br;
		
		try{
			
			// IDE version
			is = new FileInputStream(FILENAME_HELP);
			
			// JAR version
			//is = this.getClass().getClassLoader().getResourceAsStream(FILENAME_HELP);
			
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			
			br.readLine(); // removes first line
			
			while(br.ready()){
				String[] nextLine = br.readLine().split(",");
				helpDirectory.add(nextLine[1] + ":\n");
				helpDirectory.add(nextLine[2]);
			}
			
			br.close();
			isr.close();
			is.close();
			
		} catch(IOException ioe){
			ioe.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private boolean loadDemo(){
		
		InputStream is;
		InputStreamReader isr;
		BufferedReader br;
		
		try{
			// IDE version
			is = new FileInputStream(FILENAME_DEMO);
			
			// JAR version
			//is = this.getClass().getClassLoader().getResourceAsStream(FILENAME_DEMO);
			
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			
			br.readLine(); // removes first line
			
			String currCommand = null;
			ArrayList<String> exampleTable = new ArrayList<String>();
			
			while(br.ready()){
				String[] nextLine = br.readLine().split(",");
				
				if(currCommand == null){
					currCommand = nextLine[0];
					exampleTable = new ArrayList<String>();
					exampleTable.add(String.format(DEMO_LINE1, nextLine[1]));
					exampleTable.add(String.format(DEMO_LINE2, nextLine[2]));
					
				} else if(currCommand.equals(nextLine[0])){
					exampleTable.add(String.format(DEMO_LINE1, nextLine[1]));
					exampleTable.add(String.format(DEMO_LINE2, nextLine[2]));
				} else{
					demoDirectory.put(currCommand, exampleTable);
					currCommand = nextLine[0];
					exampleTable = new ArrayList<String>();
					exampleTable.add(String.format(DEMO_LINE1, nextLine[1]));
					exampleTable.add(String.format(DEMO_LINE2, nextLine[2]));
				}
			}
			
			demoDirectory.put(currCommand, exampleTable);
			
			br.close();
			isr.close();
			is.close();
			
		} catch(IOException ioe){
			ioe.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	protected String getHelp(){
		
		StringBuilder helpString = new StringBuilder();
		
		for(int i = 0; i < helpDirectory.size(); i += 2){
			helpString.append(helpDirectory.get(i) + helpDirectory.get(i+1));
			helpString.append("\n\n");
		}
		
		return helpString.toString();
	}
	
	protected String getDemo(String command){
		
		StringBuilder demoString = new StringBuilder();
		ArrayList<String> demoTable = demoDirectory.get(command);
		
		for(int i = 0; i < demoTable.size(); i += 2){
			demoString.append(demoTable.get(i));
			demoString.append("\n");
			demoString.append(demoTable.get(i+1));
			demoString.append("\n\n");
		}
		
		demoString.deleteCharAt(demoString.length() - 1);
		
		return demoString.toString();
	}
}
