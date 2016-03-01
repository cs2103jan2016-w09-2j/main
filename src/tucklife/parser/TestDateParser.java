package tucklife.parser;

import java.util.Scanner;

public class TestDateParser {
	
	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);
		
		while (true) {
			System.out.print("Date? ");
			String input = sc.nextLine();
			System.out.println();
			
			if (input.equalsIgnoreCase("exit")) {
				System.exit(0);
			} else {
				DateParser dp = new DateParser();
				dp.parseDate(input);
				System.out.println(dp.getDate().getTime());
				System.out.println();
			}
		}
	}
}
