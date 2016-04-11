// @@author A0127835Y
package tucklife.parser;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Enumeration;

public class Parser {
	
	private String paramSymbols = "-+$#!&@";
	private ProtoTask pt;
	private DateParser dp;
	private Hashtable<String, String> commandTable;
	
	private enum CommandType {
		ADD, CHANGE, COMPLETE, DELETE, DEMO, DISPLAY, DISPLAYDONE, EDIT, ERROR, EXIT,
		HELP, QUEUE, REDO, SAVE, SAVETO, SETLIMIT, UNCOMPLETE, UNDO;
	}
	
	// Indicator for extractParameter to show that parameter is to be removed
	private final String EMPTY = " ";
	
	// Indicator for ProtoTask to tell Storage that parameter is to be removed
	private final String TO_REMOVE = "";
	
	/* ****************
	 * Error messages *
	 ******************/
	
	private final String ERROR_INVALID_PARAMS = "Incorrect number of parameters";
	private final String ERROR_INVALID_COMMAND = "'%1$s' is not a valid command";
	private final String ERROR_INVALID_SORT_PARAMS = "invalid sort parameters.\nValid parameters: "
													 + paramSymbols.substring(1);
	private final String ERROR_INVALID_PRIORITY = "invalid priority";
	private final String ERROR_INVALID_DATE = "invalid date";
	private final String ERROR_INVALID_TIME = "invalid time";
	private final String ERROR_INVALID_EVENT_FORMAT = "invalid event format";
	
	private final String ERROR_ID_NOT_POSITIVE = "id '%1$s' must be positive";
	private final String ERROR_POSITION_NOT_POSITIVE = "position '%1$s' must be positive";
	private final String ERROR_LIMIT_NOT_NON_NEGATIVE = "limit '%1$s' must be non-negative";
	private final String ERROR_RESERVED_ALIAS = "'%1$s' is a reserved command alias / type for the command '%2$s'";
	private final String ERROR_DATE_OVER = "date has passed.\nYou can't travel back in time!";
	private final String ERROR_START_AFTER_END = "start date is after end date.\nYou can't travel back in time!";
	private final String ERROR_ODD_INVERTED_COMMAS = "inverted commas don't match.\nMake sure to add closing inverted commas.";
	
	private final String ERROR_PARAMS_NONE = "Format: %1$s";
	private final String ERROR_PARAMS_ID = "Format: %1$s <id>";
	private final String ERROR_PARAMS_ADD = "Format: add <task description> (optional: $<date> "
										  + "+<time> #<category> !<priority> @<location> &<additional>)";
	private final String ERROR_PARAMS_EDIT = "Format: edit <id> (at least 1 of the following: <task description> "
										   + "$<date> +<time> #<category> !<priority> @<location> &<additional>)";
	private final String ERROR_PARAMS_QUEUE = "Format: queue <id> <pos>";
	private final String ERROR_PARAMS_DEMO = "Format: demo <command>";
	private final String ERROR_PARAMS_LIMIT = "Format: setlimit <limit>";
	private final String ERROR_PARAMS_SAVETO = "Format: saveto <file path>";
	private final String ERROR_PARAMS_CHANGE = "Format: change <old command> <new command>";
	
	/* ****************
	 * Other messages *
	 ******************/
	
	private final String MESSAGE_CHANGED_ALIAS = "'%1$s' has been changed to '%2$s'!";
	private final String MESSAGE_SAME_ALIAS = "'%1$s' is the same as '%2$s'! No change occurred.";

	public Parser() {
		dp = new DateParser();
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
		CommandType type = convertAliasToDefault(commandAlias);
		
		if (type == CommandType.ERROR) {
			// Unrecognized command type
			createErrorTask(String.format(ERROR_INVALID_COMMAND, commandAlias));
		} else {
			try {
				// Parse command parameters
				pt = new ProtoTask(commandTypeToString(type));
				parseParameters(type, commandArgument);
			} catch (InvalidParamException ipe) {
				// Invalid command
				createErrorTask(ipe.getMessage());
			}
		}
		
		return pt;
	}
	
	/**
	 * This method returns the Hashtable which stores all the custom command mappings.
	 * 
	 * @return Custom command mappings.
	 */
	public Hashtable<String, String> getCommands() {
		return commandTable;
	}
	
	/**
	 * This method loads the custom command mappings from a Hashtable.
	 * 
	 * @param ht Custom command mappings.
	 */
	public void loadCommands(Hashtable<String, String> ht) {
		commandTable = ht;
	}
	
	/**
	 * This method takes in a String representing a command type, and returns
	 * the type of command as a CommandType enum constant.
	 * 
	 * @param type Command type.
	 * @return CommandType enum constant corresponding to type of command,
	 * 		   CommandType.ERROR if no command type found.
	 */
	private CommandType getCommandType(String type) {
		if (type.equals("add")) {
			return CommandType.ADD;
		} else if (type.equals("change")) {
			return CommandType.CHANGE;
		} else if (type.equals("complete")) {
			return CommandType.COMPLETE;
		} else if (type.equals("delete")) {
			return CommandType.DELETE;
		} else if (type.equals("demo")) {
			return CommandType.DEMO;
		} else if (type.equals("display")) {
			return CommandType.DISPLAY;
		} else if (type.equals("displaydone")) {
			return CommandType.DISPLAYDONE;
		} else if (type.equals("edit")) {
			return CommandType.EDIT;
		} else if (type.equals("exit")) {
			return CommandType.EXIT;
		} else if (type.equals("help")) {
			return CommandType.HELP;
		} else if (type.equals("queue")) {
			return CommandType.QUEUE;
		} else if (type.equals("redo")) {
			return CommandType.REDO;
		} else if (type.equals("save")) {
			return CommandType.SAVE;
		} else if (type.equals("saveto")) {
			return CommandType.SAVETO;
		} else if (type.equals("setlimit")) {
			return CommandType.SETLIMIT;
		} else if (type.equals("uncomplete")) {
			return CommandType.UNCOMPLETE;
		} else if (type.equals("undo")) {
			return CommandType.UNDO;
		} else {
			// Invalid command type
			return CommandType.ERROR;
		}
	}
	
	/**
	 * This method parses the command parameters based on the command type and
	 * adds the parameters into a ProtoTask object. If any of the parameters are
	 * invalid, it creates an error ProtoTask.
	 * 
	 * @param commandType CommandType enum constant for the type of command.
	 * @param commandArg String of all the parameters for the command.
	 */
	private void parseParameters(CommandType commandType, String commandArg)
			throws InvalidParamException {
		switch (commandType) {
			case ADD :
				parseAdd(commandArg);
				break;
				
			case CHANGE :
				parseChange(commandArg);
				break;
				
			case COMPLETE :
				parseComplete(commandArg);
				break;
				
			case DELETE :
				parseDelete(commandArg);
				break;
				
			case DEMO :
				parseDemo(commandArg);
				break;
				
			case DISPLAY :
				parseDisplay(commandArg);
				break;
				
			case DISPLAYDONE :
				parseDisplay(commandArg);
				break;
				
			case EDIT :
				parseEdit(commandArg);
				break;
				
			case EXIT :
				parseExit(commandArg);
				break;
				
			case HELP :
				parseHelp(commandArg);
				break;
				
			case QUEUE :
				parseQueue(commandArg);
				break;
				
			case REDO :
				parseRedo(commandArg);
				break;
				
			case SAVE :
				parseSave(commandArg);
				break;
				
			case SAVETO :
				parseSaveto(commandArg);
				break;
				
			case SETLIMIT :
				parseSetlimit(commandArg);
				break;
				
			case UNCOMPLETE :
				parseUncomplete(commandArg);
				break;
				
			case UNDO :
				parseUndo(commandArg);
				break;
				
			default :
				// Should not happen
				createErrorTask("invalid command entered");
				break;
		}
	}
	
	// Add
	private void parseAdd(String commandArg) throws InvalidParamException {
		if (commandArg.isEmpty()) {
			// No parameter given
			createErrorTask(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_ADD);
		} else {
			// Parameter is given
			parseTaskCommands(CommandType.ADD, commandArg);
		}
	}
	
	// Change
	private void parseChange(String commandArg) throws InvalidParamException {
		String[] commands = commandArg.split(" ");
		
		if (commands.length != 2) {
			// Invalid number of parameters
			throw new InvalidParamException(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_CHANGE);
		} else {
			String oldCommand = commands[0];
			String newCommand = commands[1];
			CommandType oldType = convertAliasToDefault(oldCommand);
			CommandType newType = convertAliasToDefault(newCommand);
			
			if (oldType != CommandType.ERROR) {
				// Old command name is valid
				if (newType == oldType) {
					// New command has already been set
					pt.setChangeMessage(String.format(MESSAGE_SAME_ALIAS, oldCommand, newCommand));
				} else if (newType != CommandType.ERROR) {
					// New command is used for another command
					throw new InvalidParamException(String.format(ERROR_RESERVED_ALIAS, newCommand,
																  commandTypeToString(newType)));
				} else {
					// New command is unused
					commandTable.put(commandTypeToString(oldType), newCommand);
					pt.setChangeMessage(String.format(MESSAGE_CHANGED_ALIAS, oldCommand, newCommand));
				}
			} else {
				// Old command name is invalid
				throw new InvalidParamException(String.format(ERROR_INVALID_COMMAND, oldCommand));
			}
		}
	}
	
	// Complete
	private void parseComplete(String commandArg) throws InvalidParamException {
		parseCommandWithId(CommandType.COMPLETE, commandArg);
	}
	
	// Delete
	private void parseDelete(String commandArg) throws InvalidParamException {
		parseCommandWithId(CommandType.DELETE, commandArg);
	}
	
	// Demo
	private void parseDemo(String commandArg) throws InvalidParamException {
		if (commandArg.isEmpty()) {
			// No parameter given
			throw new InvalidParamException(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_DEMO);
		} else if (convertAliasToDefault(commandArg) != CommandType.ERROR) {
			// Command to demo is valid command
			pt.setDemoCommand(commandArg);
		} else {
			// Command to demo is invalid
			throw new InvalidParamException(String.format(ERROR_INVALID_COMMAND, commandArg));
		}
	}
	
	// Display and displaydone
	private void parseDisplay(String arg)
			throws InvalidParamException {
		if (!arg.isEmpty()) {
			// Only continue checking if a parameter is provided
			if (isInteger(arg) && Integer.parseInt(arg) > 0) {
				// Display or displaydone ID
				pt.setId(Integer.parseInt(arg));
			} else {
				String searchTerm = extractParameter("", arg).trim();

				if (!searchTerm.isEmpty()) {
					pt.setSearchKey(searchTerm);
				}

				boolean hasSortOrder = false;
				boolean isAscending = false;
				String sortBy = extractParameter("+", arg);

				if (!sortBy.isEmpty()) {
					isAscending = true;
					hasSortOrder = true;
				} else {
					sortBy = extractParameter("-", arg);

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
						throw new InvalidParamException(ERROR_INVALID_SORT_PARAMS);
					}
				}
			}
		}
	}
	
	// Edit
	private void parseEdit(String commandArg) throws InvalidParamException {
		if (commandArg.isEmpty() || getRemainingArgument(commandArg).isEmpty()) {
			// Invalid number of parameters
			throw new InvalidParamException(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_EDIT);
		} else if (!isInteger(getFirstWord(commandArg))
				   || Integer.parseInt(getFirstWord(commandArg)) <= 0) {
			// ID is not a positive integer
			throw new InvalidParamException(ERROR_ID_NOT_POSITIVE
											 + "\n" + ERROR_PARAMS_EDIT);
		} else {
			// ID is a positive integer
			// Continue parsing
			pt.setId(Integer.parseInt(getFirstWord(commandArg)));
			parseTaskCommands(CommandType.EDIT, getRemainingArgument(commandArg));
		}
	}
	
	// Exit
	private void parseExit(String commandArg) throws InvalidParamException {
		parseCommandWithoutParam(CommandType.EXIT, commandArg);
	}
	
	// Help
	private void parseHelp(String commandArg) throws InvalidParamException {
		parseCommandWithoutParam(CommandType.HELP, commandArg);
	}
	
	// Queue
	private void parseQueue(String commandArg) throws InvalidParamException {
		String[] splitParams = commandArg.split(" ");
		
		if (splitParams.length != 1 && splitParams.length != 2) {
			// Invalid number of parameters
			throw new InvalidParamException(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_QUEUE);
			
		} else if (isInteger(splitParams[0]) && Integer.parseInt(splitParams[0]) > 0) {
			// ID is a positive integer (valid)
			pt.setId(Integer.parseInt(splitParams[0]));
			
			// Check position
			if (splitParams.length == 2) {
				if (isInteger(splitParams[1]) && Integer.parseInt(splitParams[1]) > 0) {
					// Position is a positive integer (valid)
					pt.setPosition(Integer.parseInt(splitParams[1]));
				} else {
					// Position is not a positive integer (invalid)
					throw new InvalidParamException(String.format(ERROR_POSITION_NOT_POSITIVE,
																  splitParams[1]));
				}
			}
		} else {
			// ID is not a positive integer (invalid)
			throw new InvalidParamException(String.format(ERROR_ID_NOT_POSITIVE,
														  splitParams[0]));
		}
	}
	
	// Redo
	private void parseRedo(String commandArg) throws InvalidParamException {
		parseCommandWithoutParam(CommandType.REDO, commandArg);
	}
	
	// Save
	private void parseSave(String commandArg) throws InvalidParamException {
		parseCommandWithoutParam(CommandType.SAVE, commandArg);
	}
	
	// Saveto
	private void parseSaveto(String commandArg) throws InvalidParamException {
		if (commandArg.isEmpty()) {
			// No parameter given
			throw new InvalidParamException(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_SAVETO);
		} else {
			// File path is given
			// Checking for valid file path done in external storage
			pt.setPath(commandArg);
		}
	}
	
	// Setlimit
	private void parseSetlimit(String commandArg) throws InvalidParamException {
		if (commandArg.isEmpty()) {
			// No parameter given
			throw new InvalidParamException(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_LIMIT);
		} else if (!isInteger(commandArg) || Integer.parseInt(commandArg) < 0) {
			// Limit is not a non-negative integer
			throw new InvalidParamException(String.format(ERROR_LIMIT_NOT_NON_NEGATIVE, commandArg));
		} else {
			// Limit is a non-negative integer
			pt.setLimit(Integer.parseInt(commandArg));
		}
	}
	
	// Uncomplete
	private void parseUncomplete(String commandArg) throws InvalidParamException {
		parseCommandWithId(CommandType.UNCOMPLETE, commandArg);
	}
	
	// Undo
	private void parseUndo(String commandArg) throws InvalidParamException {
		parseCommandWithoutParam(CommandType.UNDO, commandArg);
	}
	
	// Command without param
	private void parseCommandWithoutParam(CommandType type, String arg)
			throws InvalidParamException {
		if (!arg.isEmpty()) {
			// Parameter is given
			throw new InvalidParamException(ERROR_INVALID_PARAMS + "\n"
					+ String.format(ERROR_PARAMS_NONE, commandTypeToString(type)));
		}
	}
	
	// Command with id
	private void parseCommandWithId(CommandType type, String arg)
			throws InvalidParamException {
		if (arg.isEmpty()) {
			// No parameter given
			throw new InvalidParamException(ERROR_INVALID_PARAMS + "\n"
											+ String.format(ERROR_PARAMS_ID, commandTypeToString(type)));
		} else {
			if (isInteger(arg) && Integer.parseInt(arg) > 0) {
				// ID is a positive integer
				pt.setId(Integer.parseInt(arg));
			} else {
				// ID is not a positive integer
				throw new InvalidParamException(String.format(ERROR_ID_NOT_POSITIVE, arg));
			}
		}
	}
	
	private void parseTaskCommands(CommandType type, String arg) 
			throws InvalidParamException {
		String taskDesc = extractParameter("", arg);

		// Check task description
		if (!taskDesc.isEmpty()) {
			pt.setTaskDesc(taskDesc);
		} else if (type == CommandType.ADD) {
			throw new InvalidParamException(ERROR_INVALID_PARAMS + "\n" + ERROR_PARAMS_ADD);
		}

		// Extract relevant parameters
		String location = extractParameter("@", arg);
		String priority = extractParameter("!", arg);
		String cat = extractParameter("#", arg);
		String time = extractParameter("+", arg);
		String date = extractParameter("$", arg);
		String additional = extractParameter("&", arg);

		// Check location
		if (!location.isEmpty()) {
			if (location.equals(EMPTY) && type == CommandType.EDIT) {
				// Remove location
				pt.setLocation(TO_REMOVE);
			} else {
				pt.setLocation(location);
			}
		}

		// Check priority
		if (!priority.isEmpty()) {
			int priorityRank = convertPriority(priority);

			if (priority.equals(EMPTY) && type == CommandType.EDIT) {
				// Remove priority
				pt.setPriority(0);
			} else {
				if (priorityRank == -1) {
					throw new InvalidParamException(ERROR_INVALID_PRIORITY);
				} else {
					pt.setPriority(priorityRank);
				}
			}
		}

		// Check category
		if (!cat.isEmpty()) {
			if (cat.equals(EMPTY) && type == CommandType.EDIT) {
				// Remove category
				pt.setCategory(TO_REMOVE);
			} else {
				pt.setCategory(cat);
			}
		}

		// Check date and time
		if (!time.isEmpty() || !date.isEmpty()) {
			dp.reset();

			if ((time.equals(EMPTY) || date.equals(EMPTY)) && type == CommandType.EDIT) {
				// Remove date and time
				pt.setEndDate(dp.getRemovalDate());

			} else {
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
							endDate = dp.combineDateTime(dp.parseDate(date), dp.getDefaultEndTime());

						} else if (dates.length == 2) {
							// Event
							startDate = dp.combineDateTime(dp.parseDate(dates[0]), dp.getDefaultStartTime());
							endDate = dp.combineDateTime(dp.parseDate(dates[1]), dp.getDefaultEndTime());
							boolean hasEndYear = dp.hasYear();

							if (endDate.before(startDate)) {
								if (hasEndYear) {
									throw new InvalidParamException(ERROR_START_AFTER_END);
								} else {
									endDate = dp.getNextYear(endDate);
								}
							}

						} else {
							// Unrecognized format
							throw new InvalidParamException(ERROR_INVALID_DATE);
						}
					} else if (date.isEmpty()) {
						// Date not specified
						times = splitEventDate(time);

						if (times.length == 1) {
							// Deadline
							if (type == CommandType.EDIT)  {
								endTime = dp.parseTime(time);

								if (dp.isDateOver(endTime)) {
									endTime = dp.getNextDay(endTime);
								}
							} else {
								endDate = dp.getDefaultDate();

								if (dp.isDateOver(dp.combineDateTime(endDate, dp.parseTime(time)))) {
									endDate = dp.getNextDay(endDate);
								}

								endDate = dp.combineDateTime(endDate, dp.parseTime(time));
							}

						} else if (times.length == 2) {
							// Event
							if (type == CommandType.EDIT) {
								startTime = dp.parseTime(times[0]);
								endTime = dp.parseTime(times[1]);

								if (dp.isDateOver(startTime)) {
									startTime = dp.getNextDay(startTime);
									endTime = dp.getNextDay(endTime);
								}

								if (endTime.before(startTime)) {
									endTime = dp.getNextDay(endTime);
								}										
							} else {
								startDate = dp.getDefaultDate();

								if (dp.isDateOver(dp.combineDateTime(startDate, dp.parseTime(times[0])))) {
									startDate = dp.getNextDay(startDate);
								}

								endDate = startDate;
								startDate = dp.combineDateTime(startDate, dp.parseTime(times[0]));
								endDate = dp.combineDateTime(endDate, dp.parseTime(times[1]));

								if (endDate.before(startDate)) {
									endDate = dp.getNextDay(endDate);
								}
							}

						} else {
							// Unrecognized format
							throw new InvalidDateException(ERROR_INVALID_TIME);
						}
					} else {
						dates = splitEventDate(date);
						times = splitEventDate(time);

						if (dates.length == times.length) {
							if (dates.length == 1) {
								// Deadline
								endDate = dp.combineDateTime(dp.parseDate(date), dp.parseTime(time));
								
								if (dp.isDateOver(endDate)) {
									throw new InvalidParamException(ERROR_DATE_OVER);
								}

								if (type == CommandType.EDIT) {
									endTime = dp.parseTime(time);
								}

							} else if (dates.length == 2) {
								// Multiple day event
								startDate = dp.combineDateTime(dp.parseDate(dates[0]), dp.parseTime(times[0]));
								endDate = dp.combineDateTime(dp.parseDate(dates[1]), dp.parseTime(times[1]));
								boolean hasEndYear = dp.hasYear();
								
								if (dp.isDateOver(endDate) || dp.isDateOver(startDate)) {
									throw new InvalidParamException(ERROR_DATE_OVER);
								} else if (endDate.before(startDate)) {
									if (hasEndYear) {
										throw new InvalidParamException(ERROR_START_AFTER_END);
									} else {
										endDate = dp.getNextYear(endDate);
									}
								}

								if (type == CommandType.EDIT) {
									startTime = dp.parseTime(times[0]);
									endTime = dp.parseTime(times[1]);
								}

							} else {
								// Unrecognized format
								throw new InvalidParamException(ERROR_INVALID_EVENT_FORMAT);
							}
						} else if (dates.length == 1 && times.length == 2) {
							// Same day event
							startDate = dp.combineDateTime(dp.parseDate(date), dp.parseTime(times[0]));
							endDate = dp.combineDateTime(dp.parseDate(date), dp.parseTime(times[1]));
							
							if (dp.isDateOver(endDate) || dp.isDateOver(startDate)) {
								throw new InvalidParamException(ERROR_DATE_OVER);
							}

							if (endDate.before(startDate)) {
								endDate = dp.getNextDay(endDate);
							}

							if (type == CommandType.EDIT) {
								startTime = dp.parseTime(times[0]);
								endTime = dp.parseTime(times[1]);
							}

						} else {
							// Unrecognized format
							throw new InvalidParamException(ERROR_INVALID_EVENT_FORMAT);
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
				} catch (InvalidDateException ide) {
					throw new InvalidParamException(ide.getMessage());
				}
			}
		}

		// Check additional
		if (!additional.isEmpty()) {
			if (additional.equals(EMPTY) && type == CommandType.EDIT) {
				pt.setAdditional(TO_REMOVE);
			} else {
				pt.setAdditional(additional);
			}
		}
	}
	
	/**
	 * This method extracts the parameter for a specific symbol from user entered command.
	 * Text enclosed within inverted commas are ignored when checking for parameter symbol.
	 * 
	 * @param symbol Symbol to indicate where to extract from.
	 * @param commandArg User entered command (without the first word).
	 * @return Contents for the parameter.
	 * @throws InvalidParamException if the inverted commas don't match.
	 */
	private String extractParameter(String symbol, String commandArg)
			throws InvalidParamException {
		boolean toCheck = true;
		boolean isInParam = false;
		boolean isEmpty = false;
		String parameter = "";
		char symbolChar = ' ';

		if (symbol.isEmpty()) {
			// For getting task description and search term
			isInParam = true;
		} else {
			symbolChar = symbol.charAt(0);
		}

		for (int i = 0; i < commandArg.length(); i++) {
			if (commandArg.charAt(i) == '"') {
				toCheck = !toCheck;
			} else {
				if (isInParam) {
					// Within the parameter
					if (toCheck) {
						if (i == 0 && paramSymbols.contains(commandArg.charAt(i) + "")) {
							// Special case for empty task description and search term
							isInParam = false;
							break;
						} else if (commandArg.charAt(i) == ' ' && i + 1 != commandArg.length()
								   && paramSymbols.contains(commandArg.charAt(i + 1) + "")) {
							
							if (commandArg.charAt(i + 1) != '-') {
								isInParam = false;
								
								if (parameter.trim().isEmpty()) {
									// Parameter is empty - remove parameter
									isEmpty = true;
								}
								
								break;
							} else {
								// Special case for event dates
								// e.g. 1 Jan - 31 Dec
								parameter += commandArg.charAt(i);
							}
						} else {
							parameter += commandArg.charAt(i);
						}
					} else {
						parameter += commandArg.charAt(i);
					}
				} else {
					// Not within the parameter
					if (toCheck && commandArg.charAt(i) == symbolChar) {
						if (i == 0 || commandArg.charAt(i - 1) == ' ') {
							isInParam = true;
						}
					}
				}
			}
		}
		
		if (!toCheck) {
			// Odd number of inverted commas - invalid
			throw new InvalidParamException(ERROR_ODD_INVERTED_COMMAS);
		} else {
			if (isEmpty) {
				// Remove parameter
				return EMPTY;
			} else if (isInParam && parameter.trim().isEmpty()) {
				// Remove parameter
				return EMPTY;
			} else {
				return parameter.trim();
			}
		}
	}
	
	/**
	 * This method converts the user entered priorities into integer priorities.
	 * (High = 1, medium = 2, low = 3, invalid priority = -1)
	 * 
	 * @param priority User entered String for priority.
	 * @return Integer corresponding to priority.
	 */
	private int convertPriority(String priority) {
		if (priority.equalsIgnoreCase("high")) {
			return 1;
		} else if (priority.equalsIgnoreCase("medium")
				   || priority.equalsIgnoreCase("med")) {
			return 2;
		} else if (priority.equalsIgnoreCase("low")) {
			return 3;
		} else {
			return -1;
		}
	}
	
	/**
	 * This method checks if the sort criteria provided by user is
	 * a valid sort criteria.
	 * 
	 * @param sortParam User entered String for sort criteria
	 * @return True is sort criteria is valid, false otherwise.
	 */
	private boolean isValidSortCrit(String sortParam) {
		return paramSymbols.substring(1).contains(sortParam);
	}
	
	/**
	 * This method checks whether user entered a range of date or time,
	 * separates the starting date and ending date, and returns both in an array.
	 * If no range is found, the original String is also returned in an length 1 array.
	 * 
	 * @param s String to check for date range.
	 * @return Array with start and end date separated.
	 */
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
		
		// Fix for relative day of week dates
		// e.g. Next mon to fri --> next mon to next fri
		if (result[0].startsWith("next") && !result[1].startsWith("next")) {
			result[1] = "next " + result[1];
		}
		
		return result;
	}
	
	/**
	 * This method extracts the first word (space delimited) from the user command.
	 * It returns the entire command if the command is a single word.
	 * 
	 * @param command User input command.
	 * @return First word in the command.
	 */
	private String getFirstWord(String command) {
		return command.split(" ")[0];
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
			return command.substring(i + 1, command.length()).trim();
		}
	}
	
	/**
	 * This method converts a command alias into its corresponding
	 * CommandType enum constant.
	 * 
	 * @param alias Command alias.
	 * @return CommandType enum constant for the command alias given, or CommandType.ERROR
	 * 		   if no such command alias found.
	 */
	private CommandType convertAliasToDefault(String alias) {
		
		CommandType type = getCommandType(alias);
		
		if (type == CommandType.ERROR && commandTable.contains(alias)) {
			// Alias is custom name for a command type
			Enumeration<String> commandTableKeys = commandTable.keys();
			
			while (commandTableKeys.hasMoreElements()) {
				String key = commandTableKeys.nextElement();
				if (commandTable.get(key).equalsIgnoreCase(alias)) {
					type = getCommandType(key);
					break;
				}
			}
		}
		
		return type;
	}
	
	/**
	 * This method converts a CommandType enum constant to its String equivalent.
	 * 
	 * @param type CommandType enum constant.
	 * @return String corresponding to CommandType.
	 */
	private String commandTypeToString(CommandType type) {
		return type.toString().toLowerCase();
	}
	
	/**
	 * This method checks if a String is an integer.
	 * 
	 * @param s String to check. 
	 * @return True if String is an integer, false otherwise.
	 */
	private boolean isInteger(String s) {
		boolean isInt = true;
		
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			isInt = false;
		}
		
		return isInt;
	}
	
	/**
	 * This method creates a ProtoTask with command set to error.
	 * Used for sending error messages back to user.
	 * 
	 * @param errorMsg Error message to be sent to user.
	 */
	private void createErrorTask(String errorMsg) {
		pt = new ProtoTask("error");
		pt.setErrorMessage(errorMsg);
	}
}