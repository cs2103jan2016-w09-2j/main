package tucklife.parser;

public class Parser {
	
	private String[] commandTypes = { "add", "complete", "delete", "demo", "display", "displaydone",
									  "edit", "help", "save", "saveto" };
	private String[] paramSymbols = { "-", "+", "$", "#", "!", "&", "@" };
	private ProtoTask pt;
	
	public Parser() {
		
	}
	
	public ProtoTask parse(String command) {
		String commandType = getFirstWord(command);
		String commandArgument = getRemainingArgument(command);
		
		if (isValidCommandType(commandType)) {
			pt = new ProtoTask(commandType.toLowerCase());
			
			String message = splitParameters(commandType, commandArgument);
			if (!message.isEmpty()) {
				pt.setErrorMessage(message);
			}
		} else {
			pt = new ProtoTask("error");
			pt.setErrorMessage("'" + commandType + "' is not a valid command");
		}
		
		return pt;
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
					pt = new ProtoTask("error");
					pt.setErrorMessage("too little parameters");
				} else if (!isPositiveInteger(getFirstWord(commandArg))) {
					pt = new ProtoTask("error");
					pt.setErrorMessage("'id' must be a positive integer");
				} else {
					pt.setId(Integer.parseInt(getFirstWord(commandArg)));
					commandArg = getRemainingArgument(commandArg);
				}
				
				// No break, edit and add share similar parameters
				
			case "add" :
				if (commandArg.isEmpty()) {
					pt = new ProtoTask("error");
					pt.setErrorMessage("too little parameters");
				} else {
					String taskDesc = extractParameter("", commandArg);
					
					if (!taskDesc.isEmpty()) {
						pt.setTaskDesc(taskDesc);
					}
					
					String location = extractParameter("@", commandArg);
					String priority = extractParameter("!", commandArg);
					String cat = extractParameter("#", commandArg);
					String time = extractParameter("+", commandArg);
					String date = extractParameter("$", commandArg);
					String additional = extractParameter("&", commandArg);
					
					if (!location.isEmpty()) {
						pt.setLocation(location);
					}
					
					if (!priority.isEmpty()) {
						int priorityRank = convertPriority(priority);
						
						if (priorityRank == -1) {
							pt.setErrorMessage("invalid priority");
						} else {
							pt.setPriority(priorityRank);
						}
						
					}
					
					if (!cat.isEmpty()) {
						pt.setCategory(cat);
					}
					
					if (!time.isEmpty()) {
						//TODO parse time
					}
					
					if (!date.isEmpty()) {
						//TODO parse date
					}
					
					if (!additional.isEmpty()) {
						pt.setAdditional(additional);
					}
				}
				break;
				
			// Parameter: id
			case "complete" :
			case "delete" :
				if (commandArg.isEmpty()) {
					pt = new ProtoTask("error");
					pt.setErrorMessage("'id' required");
				} else if (!isPositiveInteger(commandArg)) {
					pt = new ProtoTask("error");
					pt.setErrorMessage("'id' must be a positive integer");
				} else {
					pt.setId(Integer.parseInt(commandArg));
				}
				break;
				
			// Parameter: command
			case "demo" :
				if (isValidCommandType(commandArg)) {
					pt.setDemoCommand(commandArg);
				} else {
					pt = new ProtoTask("error");
					pt.setErrorMessage("'" + commandArg + "' is not a valid command");
				}
				break;
				
			case "display" :
				if (isPositiveInteger(commandArg)) {
					pt.setId(Integer.parseInt(commandArg));
					break;
				}
				
				// No break, display and displaydone have similar parameters
				
			case "displaydone" :
				//TODO: Check if sorting by valid parameter
				String search = extractParameter("", commandArg);
				if (!search.isEmpty()) {
					pt.setSearchKey(search);
				}
				
				int sortOrder = -1;
				String sortBy = extractParameter("+", commandArg);
				
				if (!sortBy.isEmpty()) {
					sortOrder = 1;
				} else {
					sortBy = extractParameter("-", commandArg);
					
					if (!sortBy.isEmpty()) {
						sortOrder = 0;
					}
				}
				
				if (sortOrder != -1) {
					pt.setSortOrder(sortOrder);
					pt.setSortCrit(sortBy);
				}
				
				break;
				
			// Parameter: file path
			case "saveto" :
				//TODO: Check if file path is valid
				if (commandArg.isEmpty()) {
					pt = new ProtoTask("error");
					pt.setErrorMessage("file path required");
				} else {
					pt.setPath(commandArg);
				}
				break;
				
			// No parameters
			case "help" :
			case "save" :
				if (!commandArg.isEmpty()) {
					pt = new ProtoTask("error");
					pt.setErrorMessage("too many parameters");
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
	
	public int convertPriority(String priority) {
		if (priority.equalsIgnoreCase("high")) {
			return 1;
		} else if (priority.equalsIgnoreCase("medium")) {
			return 2;
		} else if (priority.equalsIgnoreCase("low")) {
			return 3;
		} else {
			return -1;
		}
	}
}