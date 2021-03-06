//@@author A0124274L

package tucklife.UI;

import java.util.Scanner;

public class Interaction {

	private static final String MESSAGE_COMMAND_PROMPT = "Input command >>> ";
	private static final String MESSAGE_WELCOME = "=============================\n|  Welcome to Tucklife! :)  |\n" 
			+ "| Type help to get started. |\n=============================\n";

	private static Scanner sc = new Scanner(System.in);
	private static FlowController fc = new FlowController();

	public static void main(String[] args) {

		// Initialisation
		System.out.println(MESSAGE_WELCOME);
		System.out.println(fc.statusOnly() + "\n");

		// Program loop
		while (true) {
			System.out.print(MESSAGE_COMMAND_PROMPT);
			String command = sc.nextLine();
			System.out.println();
			String result = fc.execute(command);
			System.out.println(result);
			System.out.println();
		}
	}

}


