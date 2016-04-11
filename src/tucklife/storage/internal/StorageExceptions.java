//@@author A0111101N
package tucklife.storage.internal;

import java.text.SimpleDateFormat;
import java.util.Calendar;

//Nested class of all exceptions that are returned to Flow Controller
public class StorageExceptions {

	//Returned when the number of tasks in a day exceeds the limit
	public static class OverloadException extends Exception {
		private int limit;
		private static final String RETURN_MESSAGE_FOR_OVERLOAD = "That day has been filled with %1$s tasks! It hit the limit! You should reschedule the task to another day. "
				+ "Alternatively, you can either change the overload limit or turn it off.";

		public OverloadException(int limit) {
			this.limit = limit;
		}

		public String getReturnMsg() {
			return String.format(RETURN_MESSAGE_FOR_OVERLOAD, this.limit);
		}
	}
	
	public static class NothingToUndoException extends Exception {
		private static final String RETURN_MESSAGE_FOR_NOTHING_TO_UNDO = "There is no previous action to undo!";

		public String getReturnMsg() {
			return RETURN_MESSAGE_FOR_NOTHING_TO_UNDO;
		}
	}

	public static class NothingToRedoException extends Exception {
		private static final String RETURN_MESSAGE_FOR_NOTHING_TO_REDO = "There is no previous action to redo!";

		public String getReturnMsg() {
			return RETURN_MESSAGE_FOR_NOTHING_TO_REDO;
		}
	}

	//Returned when start date is before end date
	public static class InvalidDateException extends Exception {

		private SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
		Calendar start;
		Calendar end;
		Calendar deadline;

		public InvalidDateException(Calendar start, Calendar end) {
			this.start = start;
			this.end = end;
		}

		public InvalidDateException(Calendar start, Calendar end, Calendar deadline) {
			this.start = start;
			this.end = end;
			this.deadline = deadline;
		}

		public String getReturnMsg() {
			String startDate = sdf.format(start.getTime());
			String endDate = sdf.format(end.getTime());
			if (this.deadline == null) {
				return startDate + " is before " + endDate + "!";
			} else {
				String deadlineDate = sdf.format(deadline.getTime());
				return "Task is currently an event from " + startDate + " to " + endDate
						+ ", unable to change it to a deadline by " + deadlineDate + "!";
			}
		}
	}

}
