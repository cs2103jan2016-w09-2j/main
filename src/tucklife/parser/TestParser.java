package tucklife.parser;

import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Calendar;
import java.util.Hashtable;

public class TestParser {
	
	private static Parser p;
	private static DateParser dp;
	
	private static SimpleDateFormat sdfDate = new SimpleDateFormat("EEE, dd MMM yyyy");
	private static SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		boolean isParsingDate;
		dp = new DateParser();
		p = new Parser();
		p.loadCommands(new Hashtable<String, String>());
		
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
				String d = sc.nextLine();
				
				if (d.equalsIgnoreCase("exit")) {
					sc.close();
					System.exit(0);
				}
				
				System.out.print("Time? ");
				String t = sc.nextLine();
				
				try {
					Calendar date = dp.parseDate(d);
					Calendar time = dp.parseTime(t);
					System.out.println(sdfDate.format(date.getTime()) + ", " + sdfTime.format(time.getTime()));
				} catch (InvalidDateException e) {
					System.out.println("error: " + e.getMessage());
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
