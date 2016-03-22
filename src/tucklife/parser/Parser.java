package tucklife.parser;

import java.util.Calendar;

public class Parser {
	
	private String[] commandTypes = { "add", "complete", "delete", "demo", "display", "displaydone",
									  "edit", "exit", "help", "queue", "redo",
									  "save", "saveto", "setlimit", "setdefault", "undo" };
	private String[] paramSymbols = { "-", "+", "$", "#", "!", "&", "@" };
	private ProtoTask pt;
	private DateParser dp;
	
	// Error messages:
	private final String ERROR_WRONG_PARAMS = "Incorrect number of parameters";
	private final String ERROR_PARAMS_NONE = "Format: %1$s";
	private final String ERROR_PARAMS_ID = "Format: %1$s <id>";
	private final String ERROR_PARAMS_ADD = "Format: add <task description> (optional: $<date> "
										  + "+<time> #<category> !<priority> @<location> &<additional>)";
	private final String ERROR_PARAMS_EDIT = "Format: edit <id> (optional: <task description> "
										   + "$<date> +<time> #<category> !<priority> @<location> &<additional>)";
	private final String ERROR_PARAMS_DISPLAY = "Format: display (optional: <search term> +/-<sort order>";
	private final String ERROR_PARAMS_QUEUE = "Format: queue <id> <pos>";
	private final String ERROR_PARAMS_DEMO = "Format: demo <command>";
	private final String ERROR_PARAMS_LIMIT = "Format: setlimit <limit>";
	private final String ERROR_PARAMS_SAVETO = "Format: saveto <file path>";

	public Parser() {
	}
	
	public ProtoTask parse(String command) {
		String commandType = getFirstWord(command);
		String commandArgument = getRemainingArgument(command);
		
		if (isValidCommandType(commandType)) {
			pt = new ProtoTask(commandType.toLowerCase());
			splitParameters(commandType, commandArgument);
		} else {
			createErrorTask("'" + commandType + "' is not a valid command");
		}
		
		return pt;
	}
	
	private String getFirstWord(String command) {
		int i = command.indexOf(" ");
		
		if (i == -1) {
			return command;
		} else {
			return command.substring(0, i);
		}
	}
	
	private String getRemainingArgument(String command) {
		int i = command.indexOf(" ");
		
		if (i == -1) {
			return "";
		} else {
			return command.substring(i + 1, command.length());
		}
	}
	
	private boolean isValidCommandType(String type) {
		for (int i = 0; i < commandTypes.length; i++) {
			if (type.equalsIgnoreCase(commandTypes[i])) {
				return true;
			}
		}
		
		return false;
	}
	
	private void splitParameters(String commandType, String commandArg) {
		switch (commandType) {
			case "edit" :
				if (commandArg.isEmpty() || getRemainingArgument(commandArg).isEmpty()) {
					createErrorTask(ERROR_WRONG_PARAMS + "\n" + ERROR_PARAMS_EDIT);
					break;
				} else if (!isInteger(getFirstWord(commandArg)) || Integer.parseInt(getFirstWord(commandArg)) <= 0) {
					createErrorTask("<id> must be greater than 0\n" + ERROR_PARAMS_EDIT);
					break;
				} else {
					pt.setId(Integer.parseInt(getFirstWord(commandArg)));
					commandArg = getRemainingArgument(commandArg);
				}
				
				// No break, edit and add share similar parameters
				
			case "add" :
				if (commandArg.isEmpty()) {
					createErrorTask(ERROR_WRONG_PARAMS + "\n" + ERROR_PARAMS_ADD);
				} else {
					String taskDesc = extractParameter("", commandArg);
					
					if (!taskDesc.isEmpty()) {
						pt.setTaskDesc(taskDesc);
					} else if (commandType.equals("add")) {
						createErrorTask(ERROR_WRONG_PARAMS + "\n" + ERROR_PARAMS_ADD);
						break;
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
							createErrorTask("invalid priority");
							break;
						} else {
							pt.setPriority(priorityRank);
						}
						
					}
					
					if (!cat.isEmpty()) {
						pt.setCategory(cat);
					}
					
					if (!time.isEmpty() || !date.isEmpty()) {
						dp = new DateParser();
						Calendar endDate;
						
						try {
							if (time.isEmpty()) {
								endDate = dp.parseDate(date, "");
							} else if (date.isEmpty()) {
								endDate = dp.parseDate("", time);
							} else {
								endDate = dp.parseDate(date, time);
							}
							
							pt.setEndDate(endDate);
						} catch (InvalidDateException e) {
							createErrorTask(e.getMessage());
							break;
						}
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
					createErrorTask(ERROR_WRONG_PARAMS + "\n" + ERROR_PARAMS_ID);
				} else if (!isInteger(commandArg) || Integer.parseInt(commandArg) <= 0) {
					createErrorTask("'id' must be a positive integer");
				} else {
					pt.setId(Integer.parseInt(commandArg));
				}
				break;
				
			// Parameter: task limit
			case "setlimit" :
				if (commandArg.isEmpty()) {
					createErrorTask(ERROR_WRONG_PARAMS + "\n" + ERROR_PARAMS_LIMIT);
				} else if (!isInteger(commandArg) || Integer.parseInt(commandArg) < 0) {
					createErrorTask("limit must be a non-negative integer");
				} else {
					pt.setLimit(Integer.parseInt(commandArg));
				}
				break;
				
			// Parameter: id, position
			case "queue" :
				String[] splitParams = commandArg.split(" ");
				if (splitParams.length != 1 && splitParams.length != 2) {
					createErrorTask(ERROR_WRONG_PARAMS + "\n" + ERROR_PARAMS_QUEUE);
				} else if (isInteger(splitParams[0]) && Integer.parseInt(splitParams[0]) > 0) {
					if (splitParams.length == 2) {
						if (isInteger(splitParams[1]) && Integer.parseInt(splitParams[1]) > 0) {
							pt.setPosition(Integer.parseInt(splitParams[1]));
						} else {
							createErrorTask("position must be a non-negative integer");
							break;
						}
					}
					
					pt.setId(Integer.parseInt(splitParams[0]));
				} else {
					createErrorTask("id must be a non-negative integer");
				}
				break;
				
			// Parameter: command
			case "demo" :
				if (isValidCommandType(commandArg)) {
					pt.setDemoCommand(commandArg);
				} else {
					if (commandArg.isEmpty()) {
						createErrorTask(ERROR_WRONG_PARAMS + "\n" + ERROR_PARAMS_DEMO);
					} else {
						createErrorTask("'" + commandArg + "' is not a valid command");
					}
				}
				break;

			case "display" :
				if (isInteger(commandArg) && Integer.parseInt(commandArg) > 0) {
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
				
				boolean hasSortOrder = false;
				boolean isAscending = false;
				String sortBy = extractParameter("+", commandArg);
				
				if (!sortBy.isEmpty()) {
					isAscending = true;
					hasSortOrder = true;
				} else {
					sortBy = extractParameter("-", commandArg);
					
					if (!sortBy.isEmpty()) {
						isAscending = false;
						hasSortOrder = true;
					}
				}
				
				if (hasSortOrder) {
					pt.setHasSortOrder(true);
					pt.setIsAscending(isAscending);
					pt.setSortCrit(sortBy);
				}
				
				break;
				
			case "setdefault" :
				// TODO: setdefault
				break;
				
			// Parameter: file path
			case "saveto" :
				if (commandArg.isEmpty()) {
					createErrorTask(ERROR_WRONG_PARAMS + "\n" + ERROR_PARAMS_SAVETO);
				} else {
					pt.setPath(commandArg);
				}
				break;
				
			// No parameters
			case "undo" :
			case "redo" :
			case "help" :
			case "save" :
			case "exit" :
				if (!commandArg.isEmpty()) {
					createErrorTask(ERROR_WRONG_PARAMS + "\n"
				                    + String.format(ERROR_PARAMS_NONE, commandType));
				}
				break;
		}
	}
	
	private String extractParameter(String symbol, String commandArg) {
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
			if (commandArg.indexOf(paramSymbols[i]) == 0) {
				// No description / search term
				splitPoint = 0;
				break;
			} else if (commandArg.contains(" " + paramSymbols[i])) {
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
	
	private boolean isInteger(String s) {
		boolean isInt = true;
		
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			isInt = false;
		}
		
		return isInt;
	}
	
	private int convertPriority(String priority) {
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
	
	private void createErrorTask(String errorMsg) {
		pt = new ProtoTask("error");
		pt.setErrorMessage(errorMsg);
	}
}