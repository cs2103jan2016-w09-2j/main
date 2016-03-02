package tucklife.parser;

import java.util.Scanner;

public class TestParser {

	public static void main(String[] args) {
		Parser p = new Parser();
		Scanner sc = new Scanner(System.in);
		
		while (true) {
			System.out.print("Command? ");
			String command = sc.nextLine();
			System.out.println();
			
			if (command.equalsIgnoreCase("exit")) {
				sc.close();
				System.exit(0);
			} else {
				ProtoTask pt = p.parse(command);
				
				System.out.println(pt.toString());
			}
		}
	}
}
