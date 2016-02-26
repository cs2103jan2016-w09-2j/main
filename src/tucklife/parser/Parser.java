public class Parser {
	
	private String[] commandTypes = { "add", "complete", "delete", "demo", "display", "displaydone",
									  "edit", "help", "save", "saveto" };
	private String[] paramSymbols = { "-", "+", "$", "#", "!", "&", "@" };
	private ProtoTask pt;
	
	public Parser() {
		
	}
	
	public ProtoTask getProtoTask() {
		return pt;
	}
	
	public String parse(String command) {
		String commandType = getFirstWord(command);
		String commandArgument = getRemainingArgument(command);
		
		if (isValidCommandType(commandType)) {
			pt = new ProtoTask(commandType.toLowerCase());
			
			String message = splitParameters(commandType, commandArgument);
			if (!message.isEmpty()) {
				return message;
			}
		} else {
			return "error: '" + commandType + "' is not a valid command";
		}
		
		// Parse successful
		return "";
	}
	
	public String getFirstWord(String command) {
		int i = command.indexOf(" ");
		
		if (i == -1) {
			return command;
		} else {
			return command.substring(0, i);
		}
	}
	
	public String getRemainingArgument(String command) {
		int i = command.indexOf(" ");
		
		if (i == -1) {
			return "";
		} else {
			return command.substring(i + 1, command.length());
		}
	}
	
	public boolean isValidCommandType(String type) {
		for (int i = 0; i < commandTypes.length; i++) {
			if (type.equalsIgnoreCase(commandTypes[i])) {
				return true;
			}
		}
		
		return false;
	}
	
	public String splitParameters(String commandType, String commandArg) {
		switch (commandType) {
			case "edit" :
				if (commandArg.isEmpty() || getRemainingArgument(commandArg).isEmpty()) {
					return "error: too little parameters"; 
				} else if (!isPositiveInteger(getFirstWord(commandArg))) {
					return "error: 'id' must be a positive integer";
				} else {
					pt.addParam("id", getFirstWord(commandArg));
					commandArg = getRemainingArgument(commandArg);
				}
				
				// No break, edit and add share similar parameters
				
			case "add" :
				if (commandArg.isEmpty()) {
					return "error: too litte parameters";
				} else {
					String title = extractParameter("", commandArg);
					
					if (!title.isEmpty()) {
						pt.addParam("title", title);
					}
					
					String location = extractParameter("@", commandArg);
					String priority = extractParameter("!", commandArg);
					String cat = extractParameter("#", commandArg);
					String time = extractParameter("+", commandArg);
					String date = extractParameter("$", commandArg);
					String info = extractParameter("&", commandArg);
					
					if (!location.isEmpty()) {
						pt.addParam("location", location);
					}
					
					if (!priority.isEmpty()) {
						pt.addParam("priority", priority);
					}
					
					if (!cat.isEmpty()) {
						pt.addParam("category", cat);
					}
					
					if (!time.isEmpty()) {
						pt.addParam("time", time);
					}
					
					if (!date.isEmpty()) {
						pt.addParam("date", date);
					}
					
					if (!info.isEmpty()) {
						pt.addParam("infomation", info);
					}
				}
				break;
				
			// Parameter: id
			case "complete" :
			case "delete" :
				if (commandArg.isEmpty()) {
					return "error: 'id' required";
				} else if (!isPositiveInteger(commandArg)) {
					return "error: 'id' must be a positive integer";
				} else {
					pt.addParam("id", commandArg);
				}
				break;
				
			// Parameter: command
			case "demo" :
				if (isValidCommandType(commandArg)) {
					pt.addParam("command", commandArg);
				} else {
					return "error: '" + commandArg + "' is not recognised";
				}
				break;
				
			case "display" :
				if (isPositiveInteger(commandArg)) {
					pt.addParam("id", commandArg);
					break;
				}
				
				// No break, display and displaydone have similar parameters
				
			case "displaydone" :
				//TODO: Check if sorting by valid parameter
				String search = extractParameter("", commandArg);
				if (!search.isEmpty()) {
					pt.addParam("search", search);
				}
				
				String sortOrder = "";
				String sortBy = extractParameter("+", commandArg);
				
				if (!sortBy.isEmpty()) {
					sortOrder = "asc";
				} else {
					sortBy = extractParameter("-", commandArg);
					
					if (!sortBy.isEmpty()) {
						sortOrder = "desc";
					}
				}
				
				if (!sortOrder.isEmpty()) {
					pt.addParam("sortOrder", sortOrder);
					pt.addParam("sortBy", sortBy);
				}
				
				break;
				
			// Parameter: file path
			case "saveto" :
				//TODO: Check if file path is valid
				if (commandArg.isEmpty()) {
					return "error: 'file path' required";
				} else {
					pt.addParam("path", commandArg);
				}
				break;
				
			// No parameters
			case "help" :
			case "save" :
				if (!commandArg.isEmpty()) {
					return "error: too many parameters";
				}
				break;
		}
		
		// Split successful
		return "";
	}
	
	public String extractParameter(String symbol, String commandArg) {
		/* Parameter symbol must be at the start of command argument
		 * or have a space preceding it to be valid
		 */
		if (!commandArg.contains(symbol)) {
			// Parameter not provided
			return "";
		} else if (commandArg.indexOf(symbol) == 0) {
			if (!symbol.isEmpty()) {
				commandArg = commandArg.substring(1, commandArg.length());
			}
		} else if (!commandArg.contains(" " + symbol)) {
			return "";
		} else {
			commandArg = commandArg.substring(commandArg.indexOf(" " + symbol) + 2, commandArg.length());
		}
		
		if (commandArg.isEmpty()) {
			// Use default parameter
			return "";
		}
		
		int splitPoint = -1;
		for (int i = 0; i < paramSymbols.length; i++) {
			if (commandArg.contains(" " + paramSymbols[i])) {
				int index = commandArg.indexOf(" " + paramSymbols[i]);
					
				if (splitPoint == -1 || splitPoint > index) {
					splitPoint = index;
				}
			}
		}

		if (splitPoint == -1) {
			return commandArg.substring(0, commandArg.length());
		} else {
			return commandArg.substring(0, splitPoint);
		}
	}
	
	public boolean isPositiveInteger(String s) {
		boolean isInt = true;
		
		try {
			int i = Integer.parseInt(s);
			if (i <= 0) {
				 isInt = false;
			}
		} catch (NumberFormatException nfe) {
			isInt = false;
		}
		
		return isInt;
	}
}