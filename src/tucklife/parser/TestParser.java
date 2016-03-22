package tucklife.parser;

import java.util.Scanner;

public class TestParser {
	
	private static Parser p;
	private static DateParser dp;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		boolean isParsingDate;
		dp = new DateParser();
		p = new Parser();
		
		while (true) {
			System.out.print("Testing for date parser? ");
			String input = sc.nextLine();

			if (input.equalsIgnoreCase("y")) {
				isParsingDate = true;
				break;
			} else if (input.equalsIgnoreCase("n")) {
				isParsingDate = false;
				break;
			}
		}
		
		while (true) {
			if (isParsingDate) {
				System.out.print("Date? ");
				String date = sc.nextLine();
				
				if (date.equalsIgnoreCase("exit")) {
					sc.close();
					System.exit(0);
				}
				
				System.out.print("Time? ");
				String time = sc.nextLine();
				
				boolean isValidDate = dp.parseDate(date, time);
				
				if (isValidDate) {
					System.out.println(dp.getDate().getTime());
				} else {
					System.out.println("error: invalid date/time");
				}
				
				System.out.println();
			} else {
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
}
