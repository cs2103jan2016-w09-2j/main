package tucklife.parser;

import java.util.Scanner;
import java.util.Hashtable;

public class TestParser {
	
	private static Parser p;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		p = new Parser();
		p.loadCommands(new Hashtable<String, String>());
		
		while (true) {		
			System.out.print("Command? ");
			String command = sc.nextLine();

			if (command.equalsIgnoreCase("exit")) {
				sc.close();
				System.exit(0);
			}

			ProtoTask pt = p.parse(command);

			System.out.println(pt.toString());
		}
	}
}
