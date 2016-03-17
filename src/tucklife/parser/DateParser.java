package tucklife.parser;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/*
 * Supported date formats:
 * - today
 * - tmr or tomorrow
 * - dd/mm or dd/mm/yy or dd/mm/yyyy
 * - dd.mm or dd.mm.yy or dd.mm.yyyy
 * - dd-mm or dd-mm-yy or dd-mm-yyyy
 *
 * Supported time formats:
 * - hh:mm am/pm (12-hour)
 * - hh am/pm (12-hour)
 * - hh:mm (24-hour)
 * - hhmm (24-hour)
 * 
 * Notes:
 * - If only time is specified and has passed for today,
 *   returned date will be for tomorrow at specified time.
 *   
 * - If only date is specified, default time is 11:59pm.
 * 
 * - If date specified is without year, and has passed for current year,
 *   return date will be for next year.
 *   
 * TODO:
 * - If 29/2 is entered, what to return:
 *   - next available leap year
 *   - next available year, but on 28/2
 *   - next available year, but on 1/3
 *   
 * - Support short/long form for month e.g. Jan / January
 */

public class DateParser {
	
	private Calendar calendar;
	
	// Format: dd/mm/yy or dd/mm/yyyy
	private static final String DATE_DMY_SLASH = "[0-3]?\\d/[01]?\\d/(\\d{2}|\\d{4})";
	
	// Format: dd.mm.yy or dd.mm.yyyy
	private static final String DATE_DMY_DOT = "[0-3]?\\d\\.[01]?\\d\\.(\\d{2}|\\d{4})";
	
	// Format: dd-mm-yy or dd-mm-yyyy
	private static final String DATE_DMY_DASH = "[0-3]?\\d-[01]?\\d-(\\d{2}|\\d{4})";
	
	// Format: dd/mm
	private static final String DATE_DM_SLASH = "[0-3]?\\d/[01]?\\d";
	
	// Format: dd.mm
	private static final String DATE_DM_DOT = "[0-3]?\\d\\.[01]?\\d";
	
	// Format: dd-mm
	private static final String DATE_DM_DASH = "[0-3]?\\d-[01]?\\d";
	
	// Format: hh:mm am/pm (12-hour)
	private static final String TIME_12HM = "[01]?\\d:[0-5]\\d\\s?[aApP][mM]";
	
	// Format: hh am/pm (12-hour)
	private static final String TIME_12H = "[01]?\\d\\s?[aApP][mM]";
	
	// Format: hh:mm (24-hour)
	private static final String TIME_24H_COLON = "[0-2]?\\d:[0-5]\\d";
	
	// Format hhmm (24-hour)
	private static final String TIME_24H_NO_COLON = "[0-2]\\d[0-5]\\d";
	
	// Indicates whether the year is included in user-entered date
	private boolean hasYear = false;
	
	// Indicates whether user-entered time is in 12-hour format
	private boolean is12Hour = false;
	
	// Hour and minutes parsed from user-entered time
	private int timeHour;
	private int timeMin;
	
	public DateParser() {
		calendar = Calendar.getInstance();
		timeHour = calendar.get(Calendar.HOUR_OF_DAY);
		timeMin = calendar.get(Calendar.MINUTE);
	}
	
	public Calendar getDate() {
		return calendar;
	}
	
	public boolean parseDate(String rawDate, String rawTime) {
		if (rawTime.isEmpty()) {
			timeHour = 23;
			timeMin = 59;
		} else {
			if (rawTime.matches(TIME_12HM)) {
				int colonPos = rawTime.indexOf(":");
				timeHour = Integer.parseInt(rawTime.substring(0, colonPos));
				timeMin = Integer.parseInt(rawTime.substring(colonPos + 1, colonPos + 3));
				is12Hour = true;
			} else if (rawTime.matches(TIME_12H)) {
				timeHour = Integer.parseInt(rawTime.split("\\h?[aApP]")[0]);
				timeMin = 0;
				is12Hour = true;
			} else if (rawTime.matches(TIME_24H_COLON)) {
				int colonPos = rawTime.indexOf(":");
				timeHour = Integer.parseInt(rawTime.substring(0, colonPos));
				timeMin = Integer.parseInt(rawTime.substring(colonPos + 1, colonPos + 3));
			} else if (rawTime.matches(TIME_24H_NO_COLON)) {
				timeHour = Integer.parseInt(rawTime.substring(0, 2));
				timeMin = Integer.parseInt(rawTime.substring(2, rawTime.length()));
			} else {
				return false;
			}
			
			if (is12Hour) {
				if (timeHour == 12) {
					timeHour = 0;
				}
				
				// Check if time is valid
				if (timeHour > 12) {
					return false;
				}

				// Conversion to 24-hour time
				if (isPm(rawTime)) {
					timeHour += 12;
				}
			} else {
				// Check if time is valid
				if (timeHour > 23) {
					return false;
				}
			}
		}
		
		if (rawDate.isEmpty()) {
			setTime();
			
			if (isPastTime()) {
				calendar.add(Calendar.DATE, 1);
			}

			return true;
		} else {
			if (rawDate.equalsIgnoreCase("today")) {
				setTime();
				return true;
			} else if (rawDate.equalsIgnoreCase("tomorrow") || rawDate.equalsIgnoreCase("tmr")) {
				calendar.add(Calendar.DATE, 1);
				setTime();
				return true;
			} else {
				try {
					SimpleDateFormat sdf;
					if (rawDate.matches(DATE_DMY_SLASH)) {
						sdf = new SimpleDateFormat("dd/M/yy");
						hasYear = true;
					} else if (rawDate.matches(DATE_DMY_DOT)) {
						sdf = new SimpleDateFormat("dd.M.yy");
						hasYear = true;
					} else if (rawDate.matches(DATE_DMY_DASH)) {
						sdf = new SimpleDateFormat("dd-M-yy");
						hasYear = true;
					} else if (rawDate.matches(DATE_DM_SLASH)) {
						rawDate += "/" + calendar.get(Calendar.YEAR);
						sdf = new SimpleDateFormat("dd/M/yy");
					} else if (rawDate.matches(DATE_DM_DOT)) {
						rawDate += "." + calendar.get(Calendar.YEAR);
						sdf = new SimpleDateFormat("dd.M.yy");
					} else if (rawDate.matches(DATE_DM_DASH)) {
						rawDate += "-" + calendar.get(Calendar.YEAR);
						sdf = new SimpleDateFormat("dd-M-yy");
					} else {
						return false;
					}

					sdf.setLenient(false);
					Date date = sdf.parse(rawDate);
					calendar.setTime(date);

					if (!hasYear && isPastDate()) {
						calendar.add(Calendar.YEAR, 1);
					}

					// Set time
					setTime();

					return true;

				} catch (Exception e) {
					return false;
				}
			}
		}
	}
	
	// Checks if date entered has passed for current year
	private boolean isPastDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		return calendar.before(c);
	}
	
	// Checks if time entered has passed for today
	private boolean isPastTime() {
		Calendar c = Calendar.getInstance();
		return calendar.before(c);
	}
	
	private boolean isPm(String time) {
		String timeOfDay = time.substring(time.length() - 2, time.length());
		return timeOfDay.equalsIgnoreCase("pm");
	}
	
	private void setTime() {
		calendar.set(Calendar.HOUR_OF_DAY, timeHour);
		calendar.set(Calendar.MINUTE, timeMin);
	}
}