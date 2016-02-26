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
				String parseResult = p.parse(command);

				if (parseResult.isEmpty()) {
					System.out.println(p.getProtoTask().toString());
				} else {
					System.out.println(parseResult);
					System.out.println();
				}
			}
		}
	}
}
