// @@author A0127835Y
package tucklife.parser;

import java.util.Calendar;
import java.util.Hashtable;

public class Parser {
	
	private String[] commandTypes = { "add", "change", "complete", "delete", "demo", "display", "displaydone",
									  "edit", "exit", "help", "queue", "redo", "save", "saveto", "setlimit", "undo" };
	private String[] paramSymbols = { "-", "+", "$", "#", "!", "&", "@" };
	private ProtoTask pt;
	private DateParser dp;
	private Hashtable<String, String> commandTable;
	
	// Error messages:
	private final String ERROR_PARAMS_NONE = "Format: %1$s";
	private final String ERROR_PARAMS_ID = "Format: %1$s <id>";
	private final String ERROR_PARAMS_ADD = "Format: add <task description> (optional: $<date> "
										  + "+<time> #<category> !<priority> @<location> &<additional>)";
	private final String ERROR_PARAMS_EDIT = "Format: edit <id> (optional: <task description> "
										   + "$<date> +<time> #<category> !<priority> @<location> &<additional>)";
	private final String ERROR_PARAMS_DISPLAY = "Format: display (optional: <search term> +/-<sort order>)";
	private final String ERROR_PARAMS_QUEUE = "Format: queue <id> <pos>";
	private final String ERROR_PARAMS_DEMO = "Format: demo <command>";
	private final String ERROR_PARAMS_LIMIT = "Format: setlimit <limit>";
	private final String ERROR_PARAMS_SAVETO = "Format: saveto <file path>";
	private final String ERROR_PARAMS_CHANGE = "Format: change <old command> <new command>";
	
	private final String ERROR_INVALID_PARAMS = "Incorrect number of parameters";
	private final String ERROR_INVALID_COMMAND = "'%1$s' is not a valid command";

	public Parser() {
	}
	
	/**
	 * This method takes in a user input command and parses it into a ProtoTask object
	 * 
	 * @param command User input command.
	 * @return ProtoTask with the relevant parameters.
	 */
	public ProtoTask parse(String command) {
		String commandAlias = getFirstWord(command).toLowerCase();
		String commandArgument = getRemainingArgument(command);
		
		if (isValidCommandAlias(commandAlias)) {
			// Convert from custom alias to standard command type
			String commandType = getCommandType(commandAlias);
			
			pt = new ProtoTask(commandType);
			parseParameters(commandType, commandArgument);
		} else {
			// Unrecognized command type
			createErrorTask(String.format(ERROR_INVALID_COMMAND, commandAlias));
		}
		
		return pt;
	}
	
	/**
	 * This method extracts the first word (space delimited) from the user command.
	 * It returns the entire command if the command is a single word.
	 * 
	 * @param command User input command.
	 * @return First word in the command.
	 */
	private String getFirstWord(String command) {
		int i = command.indexOf(" ");
		
		if (i == -1) {
			return command;
		} else {
			return command.substring(0, i);
		}
	}
	
	/**
	 * This method extracts the remainder of the user command without the first word.
	 * It returns an empty string if the command is a single word.
	 * 
	 * @param command User input command.
	 * @return Command without the first word.
	 */
	private String getRemainingArgument(String command) {
		int i = command.indexOf(" ");
		
		if (i == -1) {
			return "";
		} else {
			return command.substring(i + 1, command.length());
		}
	}
	
	/**
	 * This method checks whether the command alias refers to a valid command.
	 * 
	 * @param alias Command alias.
	 * @return True if alias is valid, false otherwise.
	 */
	private boolean isValidCommandAlias(String alias) {
		if (commandTable.contains(alias)) {
			return true;
		} else {
			return isValidCommandType(alias);
		}
	}
	
	/**
	 * This method checks whether the type of command is valid.
	 * 
	 * @param commandType Type of command.
	 * @return True if command type is valid, false otherwise.
	 */
	private boolean isValidCommandType(String commandType) {
		for (String type:commandTypes) {
			if (commandType.equals(type)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * This method converts a command alias into the standard command type.
	 * 
	 * @param alias Command alias.
	 * @return Type of command if alias is found, "error" otherwise (should not happen).
	 */
	private String getCommandType(String alias) {
		for (String key:commandTypes) {
			if (alias.equals(key) || alias.equals(commandTable.get(key))) {
				return key;
			}
		}
		
		// Should not be reachable
		return "error";
	}
	
	/**
	 * This method parses the parameters based on the command type and
	 * adds the parameters into a ProtoTask object. If any of the parameters are
	 * invalid, it creates an error ProtoTask.
	 * 
	 * @param commandType Type of command.
	 * @param commandArg String of all the parameters for the command.
	 */
	private void parseParameters(String commandType, String commandArg) {
		switch (commandType) {
			case "edit" :
				if (commandArg.isEmpty() || getRemainingArgument(commandArg).isEmpty()) {
					createErrorTask(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_EDIT);
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
						createErrorTask(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_ADD);
				} else {
					String taskDesc = extractParameter("", commandArg);
					
					if (!taskDesc.isEmpty()) {
						pt.setTaskDesc(taskDesc);
					} else if (commandType.equals("add")) {
						createErrorTask(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_ADD);
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
						Calendar startDate = null;
						Calendar startTime = null;
						Calendar endDate = null;
						Calendar endTime = null;
						String[] times, dates;
						
						try {
							if (time.isEmpty()) {
								// Time not specified
								dates = splitEventDate(date);
								
								if (dates.length == 1) {
									// Deadline
									endDate = dp.parseDate(date);
									
									if (!commandType.equals("edit")) {
										endTime = dp.getDefaultEndTime();
									}

								} else if (dates.length == 2) {
									// Event
									startDate = dp.parseDate(dates[0]);
									endDate = dp.parseDate(dates[1]);
									
									startTime = dp.getDefaultStartTime();
									endTime = dp.getDefaultEndTime();
									
									startDate = dp.combineDateTime(startDate, startTime);
									endDate = dp.combineDateTime(endDate, endTime);
								} else {
									// Unrecognized format
									createErrorTask("invalid date");
									break;
								}
							} else if (date.isEmpty()) {
								// Date not specified
								times = splitEventDate(time);
								
								if (times.length == 1) {
									// Deadline
									endTime = dp.parseTime(time);
									
									if (!commandType.equals("edit")) {
										endDate = dp.getDefaultDate();
										
										if (dp.hasDatePassed(endDate, endTime)) {
											endDate = dp.getNextDay(endDate);
										}
									}									
								} else if (times.length == 2) {
									// Event
									startTime = dp.parseTime(times[0]);
									endTime = dp.parseTime(times[1]);
									
									if (!commandType.equals("edit"))  {
										startDate = dp.getDefaultDate();
										
										if (dp.hasDatePassed(startDate, startTime)) {
											startDate = dp.getNextDay(startDate);
										}
										
										endDate = startDate; 
									}
								} else {
									// Unrecognized format
									createErrorTask("invalid time");
									break;
								}
							} else {
								dates = splitEventDate(date);
								times = splitEventDate(time);
								
								if (dates.length == times.length) {
									if (dates.length == 1) {
										// Deadline
										endDate = dp.parseDate(date);
										endTime = dp.parseTime(time);
										
										endDate = dp.combineDateTime(endDate, endTime);
									} else if (dates.length == 2) {
										// Multiple day event
										startDate = dp.parseDate(dates[0]);
										endDate = dp.parseDate(dates[1]);
										
										startTime = dp.parseTime(times[0]);
										endTime = dp.parseTime(times[1]);
										
										startDate = dp.combineDateTime(startDate, startTime);
										endDate = dp.combineDateTime(endDate, endTime);
									} else {
										// Unrecognized format
										createErrorTask("invalid event format");
										break;
									}
								} else if (dates.length == 1 && times.length == 2) {
									// Same day event
									startDate = dp.parseDate(date);
									endDate = dp.parseDate(date);
									
									startTime = dp.parseTime(times[0]);
									endTime = dp.parseTime(times[1]);

									startDate = dp.combineDateTime(startDate, startTime);
									endDate = dp.combineDateTime(endDate, endTime);
								} else {
									// Unrecognized format
									createErrorTask("invalid event format");
									break;
								}
							}
							
							// Setting the parameters in ProtoTask
							if (startDate != null) {
								pt.setStartDate(startDate);
							}
							
							if (startTime != null) {
								pt.setStartTime(startTime);
							}
							
							if (endDate != null) {
								pt.setEndDate(endDate);
							}
							
							if (endTime != null) {
								pt.setEndTime(endTime);
							}
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
					createErrorTask(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_ID);
				} else if (!isInteger(commandArg) || Integer.parseInt(commandArg) <= 0) {
					createErrorTask("'id' must be a positive integer");
				} else {
					pt.setId(Integer.parseInt(commandArg));
				}
				break;
				
			// Parameter: task limit
			case "setlimit" :
				if (commandArg.isEmpty()) {
					createErrorTask(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_LIMIT);
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
					createErrorTask(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_QUEUE);
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
				if (isValidCommandAlias(commandArg)) {
					pt.setDemoCommand(commandArg);
				} else {
					if (commandArg.isEmpty()) {
						createErrorTask(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_DEMO);
					} else {
						createErrorTask("'" + commandArg + "' is not a valid command");
					}
				}
				break;
			
			// Parameter: old command, new command
			case "change" :
				String[] commands = commandArg.split(" ");
				if (commands.length != 2) {
					createErrorTask(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_CHANGE);
				} else {
					String oldCommand = commands[0];
					String newCommand = commands[1];
					
					if (isValidCommandAlias(oldCommand)) {
						if (isValidCommandAlias(newCommand)) {
							createErrorTask("'" + newCommand + "' is a reserved command alias / type");
						} else {
							String type = getCommandType(oldCommand);
							commandTable.put(type, newCommand);
							pt.setChangeMessage("'" + type + "' has been changed to '" + newCommand + "'!");
						}
					} else {
						createErrorTask("'" + oldCommand + "' is not a valid command");
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
					if (isValidSortCrit(sortBy)) {
						pt.setHasSortOrder(true);
						pt.setIsAscending(isAscending);
						pt.setSortCrit(sortBy);
					} else {
						createErrorTask("invalid sort parameters");
					}
				}
				
				break;

			// Parameter: file path
			case "saveto" :
				if (commandArg.isEmpty()) {
					createErrorTask(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_SAVETO);
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
					createErrorTask(ERROR_INVALID_PARAMS + "\n"
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
				if (symbol.isEmpty()) {
					// No description
					splitPoint = 0;
				} else if (commandArg.length() == 1){
					// For sorting
					splitPoint = 1;
				}
				break;
			} else if (commandArg.contains(" " + paramSymbols[i])) {
				if (i == 0) {
					if (symbol.equals("-")) {
						int index = commandArg.indexOf(" " + paramSymbols[i]);

						if (splitPoint == -1 || splitPoint > index) {
							splitPoint = index;
						}
					}
				} else {
					int index = commandArg.indexOf(" " + paramSymbols[i]);

					if (splitPoint == -1 || splitPoint > index) {
						splitPoint = index;
					}
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
	
	private boolean isValidSortCrit(String sortParam) {
		boolean isValidSort = false;
		
		for (int i = 1; i < paramSymbols.length; i++) {
			if (sortParam.equals(paramSymbols[i])) {
				isValidSort = true;
				break;
			}
		}
		
		return isValidSort;
	}
	
	private String[] splitEventDate(String s) {
		String[] result;
		if (s.contains(" - ")) {
			result = s.split("\\s-\\s");
		} else if (s.contains(" to ")) {
			result = s.split("\\sto\\s");
		} else {
			String[] ss = {s};
			return ss;
		}
		
		if (result[0].startsWith("next") && !result[1].startsWith("next")) {
			result[1] = "next " + result[1];
		}
		
		return result;
	}
	
	private void parseCommandWithoutParam(String command, String arg) {
		if (!arg.isEmpty()) {
			createErrorTask(ERROR_INVALID_PARAMS + "\n"
		                    + String.format(ERROR_PARAMS_NONE, command));
		}
	}
	
	public Hashtable<String, String> getCommands() {
		return commandTable;
	}
	
	public void loadCommands(Hashtable<String, String> ht) {
		commandTable = ht;
	}
}