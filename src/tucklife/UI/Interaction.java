package tucklife.UI;

import java.util.Scanner;
import tucklife.storage.ExternalStorage;
import tucklife.storage.Storage;

public class Interaction {

	private static final String MESSAGE_COMMAND_PROMPT = "Input command";
	private static final String MESSAGE_WELCOME = "Welcome to Tucklife!";

	private static Scanner sc = new Scanner(System.in);
	private static FlowController fc = new FlowController();

	public static void main(String[] args) {

		//initialisation
		System.out.println(MESSAGE_WELCOME);

		//program loop
		while (true) {
			System.out.println(MESSAGE_COMMAND_PROMPT);
			String command = sc.nextLine();
			fc.execute(command);
		}

		//String showResultToUser = getResults;
		//System.out.println(showResultToUser);
	}

}


