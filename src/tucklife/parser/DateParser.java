// @@author A0127835Y
package tucklife.parser;

import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/*
 * Supported date formats:
 * - Text dates:
 *   - Both short month names (jan, feb) and full month names (january, february)
 *   - ddmmm or ddmmmyy or ddmmmyyyy (15jan)
 *   - dd mmm or dd mmm yy or dd mmm yyyy (15 jan)
 *   - dd-mmm or dd-mmm-yy or dd-mmm-yyyy (15-jan)
 *   
 * - Number dates:
 *   - dd/mm or dd/mm/yy or dd/mm/yyyy (15/1)
 *   - dd.mm or dd.mm.yy or dd.mm.yyyy (15.1)
 *   - dd-mm or dd-mm-yy or dd-mm-yyyy (15-1)
 *   
 * - Others
 *   - today
 *   - tmr or tomorrow
 *
 * Supported time formats:
 * - 12-hour
 * 	- hh.mm am/pm
 * 	- hh:mm am/pm
 * 	- hh am/pm
 * 
 * - 24-hour
 *  - hh.mm
 *  - hh:mm
 *  - hhmm
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
 * - If 29/2 is entered, return date is for next available year.
 *   If it is not a leap year, return 28/2.
 *   
 * - Short form for September is 'Sep' not 'Sept'
 */

public class DateParser {
	
	private Calendar calendar;
	
	// Indicates whether the year is included in user-entered date
	private boolean hasYear = false;
	
	// Indicates whether user-entered time is in 12-hour format
	private boolean is12Hour = false;
	
	// Hour and minutes parsed from user-entered time
	private int timeHour;
	private int timeMin;
	
	/* ***************
	 * Date formats *
	 ****************/
	
	// Format: dd/mm/yy or dd/mm/yyyy
	private static final String DATE_DMY_SLASH = "[0-3]?\\d/[01]?\\d/\\d{2}(\\d{2})?";
	
	// Format: dd.mm.yy or dd.mm.yyyy
	private static final String DATE_DMY_DOT = "[0-3]?\\d\\.[01]?\\d\\.\\d{2}(\\d{2})?";
	
	// Format: dd-mm-yy or dd-mm-yyyy
	private static final String DATE_DMY_DASH = "[0-3]?\\d-[01]?\\d-\\d{2}(\\d{2})?";
	
	// Format: dd/mm
	private static final String DATE_DM_SLASH = "[0-3]?\\d/[01]?\\d";
	
	// Format: dd.mm
	private static final String DATE_DM_DOT = "[0-3]?\\d\\.[01]?\\d";
	
	// Format: dd-mm
	private static final String DATE_DM_DASH = "[0-3]?\\d-[01]?\\d";
	
	// Format: ddmmmyy (short month)
	private static final String DATE_SHORT_DMY = "[0-3]?\\d[a-zA-Z]{3}\\d{2}(\\d{2})?";
	
	// Format: dd mmm yy (short month)
	private static final String DATE_SHORT_DMY_SPACE = "[0-3]?\\d\\s[a-zA-Z]{3}\\s\\d{2}(\\d{2})?";
	
	// Format: dd-mmm (short month)
	private static final String DATE_SHORT_DMY_DASH = "[0-3]?\\d-[a-zA-Z]{3}-\\d{2}(\\d{2})?";
	
	// Format: ddmmm (short month)
	private static final String DATE_SHORT_DM = "[0-3]?\\d[a-zA-Z]{3}";
	
	// Format: dd mmm (short month)
	private static final String DATE_SHORT_DM_SPACE = "[0-3]?\\d\\s[a-zA-Z]{3}";
	
	// Format: dd-mmm (short month)
	private static final String DATE_SHORT_DM_DASH = "[0-3]?\\d-[a-zA-Z]{3}";
	
	// Format: ddmmmm (full month)
	private static final String DATE_FULL_DMY = "[0-3]?\\d[a-zA-Z]{4,9}\\d{2}(\\d{2})?";

	// Format: dd mmmm (full month)
	private static final String DATE_FULL_DMY_SPACE = "[0-3]?\\d\\s[a-zA-Z]{4,9}\\s\\d{2}(\\d{2})?";

	// Format: dd-mmmm (full month)
	private static final String DATE_FULL_DMY_DASH = "[0-3]?\\d-[a-zA-Z]{4,9}-\\d{2}(\\d{2})?";

	// Format: ddmmmm (full month)
	private static final String DATE_FULL_DM = "[0-3]?\\d[a-zA-Z]{4,9}";
	
	// Format: dd mmmm (full month)
	private static final String DATE_FULL_DM_SPACE = "[0-3]?\\d\\s[a-zA-Z]{4,9}";
	
	// Format: dd-mmmm (full month)
	private static final String DATE_FULL_DM_DASH = "[0-3]?\\d-[a-zA-Z]{4,9}";
	
	// Format: Short day name
	private static final String DAY_SHORT = "(next\\s)?[a-zA-Z]{3,5}";
	
	// Format: Full day name
	private static final String DAY_FULL = "(next\\s)?[a-zA-Z]{6,9}";
	
	/* *************
	 * Time formats *
	 ****************/
	
	// Format: hh.mm am/pm (12-hour)
	private static final String TIME_12HM_DOT = "[01]?\\d\\.[0-5]\\d\\s?[aApP][mM]";
	
	// Format: hh:mm am/pm (12-hour)
	private static final String TIME_12HM_COLON = "[01]?\\d:[0-5]\\d\\s?[aApP][mM]";
	
	// Format: hh am/pm (12-hour)
	private static final String TIME_12H = "[01]?\\d\\s?[aApP][mM]";
	
	// Format: hh.mm (24-hour)
	private static final String TIME_24H_DOT = "[0-2]?\\d\\.[0-5]\\d";
	
	// Format: hh:mm (24-hour)
	private static final String TIME_24H_COLON = "[0-2]?\\d:[0-5]\\d";
	
	// Format hhmm (24-hour)
	private static final String TIME_24H = "[0-2]\\d[0-5]\\d";
	
	public DateParser() {
		calendar = Calendar.getInstance();
		timeHour = calendar.get(Calendar.HOUR_OF_DAY);
		timeMin = calendar.get(Calendar.MINUTE);
	}
	
	public Calendar getDate() {
		return calendar;
	}
	
	public Calendar parseDate(String rawDate) throws InvalidDateException {
		SimpleDateFormat sdf;
		calendar = Calendar.getInstance();
		
		if (rawDate.equalsIgnoreCase("today")) {
			return calendar;
			
		} else if (rawDate.equalsIgnoreCase("tomorrow") || rawDate.equalsIgnoreCase("tmr")) {
			calendar.add(Calendar.DATE, 1);
			return calendar;
			
		} else if (rawDate.matches(DAY_SHORT) || rawDate.matches(DAY_FULL)) {
			String[] d = rawDate.split(" ");
			String dayOfWeek;
			boolean isNextWeek = false;
			
			if (d.length == 1) {
				dayOfWeek = rawDate;
			} else if (d.length == 2 && d[0].equalsIgnoreCase("next")) {
				dayOfWeek = d[1].toLowerCase();
				isNextWeek = true;
			} else {
				throw new InvalidDateException("invalid date");
			}

			int taskDay = -1;
			int today = calendar.get(Calendar.DAY_OF_WEEK);

			if (dayOfWeek.equals("mon") || dayOfWeek.equals("monday")) {
				taskDay = Calendar.MONDAY;
			} else if (dayOfWeek.equals("tue") || dayOfWeek.equals("tues")
					   || dayOfWeek.equals("tuesday")) {
				taskDay = Calendar.TUESDAY;
			} else if (dayOfWeek.equals("wed") || dayOfWeek.equals("wednesday")) {
				taskDay = Calendar.WEDNESDAY;
			} else if (dayOfWeek.equals("thu") || dayOfWeek.equals("thur")
					   || dayOfWeek.equals("thurs") || dayOfWeek.equals("thursday")) {
				taskDay = Calendar.THURSDAY;
			} else if (dayOfWeek.equals("fri") || dayOfWeek.equals("friday")) {
				taskDay = Calendar.FRIDAY;
			} else if (dayOfWeek.equals("sat") || dayOfWeek.equals("saturday")) {
				taskDay = Calendar.SATURDAY;
			} else if (dayOfWeek.equals("sun") || dayOfWeek.equals("sunday")) {
				taskDay = Calendar.SUNDAY;
			}

			if (taskDay == -1) {
				throw new InvalidDateException("invalid date");
			} else {
				if (today > taskDay || isNextWeek) {
					calendar.add(Calendar.DATE, 7);
				}

				calendar.set(Calendar.DAY_OF_WEEK, taskDay);

				return calendar;
			}
			
		} else {
			/* ***********************
			 * Variations of dd mm yy *
			 *************************/
			if (rawDate.matches(DATE_DMY_SLASH)) {
				sdf = new SimpleDateFormat("dd/M/yy");
				hasYear = true;
			} else if (rawDate.matches(DATE_DMY_DOT)) {
				sdf = new SimpleDateFormat("dd.M.yy");
				hasYear = true;
			} else if (rawDate.matches(DATE_DMY_DASH)) {
				sdf = new SimpleDateFormat("dd-M-yy");
				hasYear = true;
			
			/* ********************
			 * Variations of dd mm *
			 **********************/ 
			} else if (rawDate.matches(DATE_DM_SLASH)) {
				rawDate += "/" + calendar.get(Calendar.YEAR);
				sdf = new SimpleDateFormat("dd/M/yy");
			} else if (rawDate.matches(DATE_DM_DOT)) {
				rawDate += "." + calendar.get(Calendar.YEAR);
				sdf = new SimpleDateFormat("dd.M.yy");
			} else if (rawDate.matches(DATE_DM_DASH)) {
				rawDate += "-" + calendar.get(Calendar.YEAR);
				sdf = new SimpleDateFormat("dd-M-yy");
			
			/* ***************************************
			 * Variations of dd mmm yy (short month) *
			 *****************************************/
			} else if (rawDate.matches(DATE_SHORT_DMY)) {
				sdf = new SimpleDateFormat("ddMMMyy");
				hasYear = true;
			} else if (rawDate.matches(DATE_SHORT_DMY_SPACE)) {
				sdf = new SimpleDateFormat("dd MMM yy");
				hasYear = true;
			} else if (rawDate.matches(DATE_SHORT_DMY_DASH)) {
				sdf = new SimpleDateFormat("dd-MMM-yy");
				hasYear = true;
			
			/* ************************************
			 * Variations of dd mmm (short month) *
			 **************************************/
			} else if (rawDate.matches(DATE_SHORT_DM)) {
				rawDate += calendar.get(Calendar.YEAR);
				sdf = new SimpleDateFormat("ddMMMyy");
			} else if (rawDate.matches(DATE_SHORT_DM_SPACE)) {
				rawDate += " " + calendar.get(Calendar.YEAR);
				sdf = new SimpleDateFormat("dd MMM yy");
			} else if (rawDate.matches(DATE_SHORT_DM_DASH)) {
				rawDate += "-" + calendar.get(Calendar.YEAR);
				sdf = new SimpleDateFormat("dd-MMM-yy");
				
			/* ***************************************
			 * Variations of dd mmmm yy (long month) *
			 *****************************************/
			} else if (rawDate.matches(DATE_FULL_DMY)) {
				sdf = new SimpleDateFormat("ddMMMMyy");
				hasYear = true;
			} else if (rawDate.matches(DATE_FULL_DMY_SPACE)) {
				sdf = new SimpleDateFormat("dd MMMM yy");
				hasYear = true;
			} else if (rawDate.matches(DATE_FULL_DMY_DASH)) {
				sdf = new SimpleDateFormat("dd-MMMM-yy");
				hasYear = true;
				
			/* ************************************
			 * Variations of dd mmmm (long month) *
			 **************************************/
			} else if (rawDate.matches(DATE_FULL_DM)) {
				rawDate += calendar.get(Calendar.YEAR);
				sdf = new SimpleDateFormat("ddMMMMyy");
			} else if (rawDate.matches(DATE_FULL_DM_SPACE)) {
				rawDate += " " + calendar.get(Calendar.YEAR);
				sdf = new SimpleDateFormat("dd MMMM yy");
			} else if (rawDate.matches(DATE_FULL_DM_DASH)) {
				rawDate += "-" + calendar.get(Calendar.YEAR);
				sdf = new SimpleDateFormat("dd-MMMM-yy");
				
			// Unrecognised date
			} else {
				throw new InvalidDateException("invalid date");
			}

			sdf.setLenient(false);
			
			try {
				Date date = sdf.parse(rawDate);
				calendar.setTime(date);
				
				if (!hasYear && isPastDate()) {
					calendar.add(Calendar.YEAR, 1);
				}
				
				return calendar;
			} catch (ParseException e) {
				throw new InvalidDateException("invalid date");
			}				
		}
	}
	
	public Calendar parseTime(String rawTime) throws InvalidDateException {
		calendar = Calendar.getInstance();
		is12Hour = false;
		
		/* ****************************
		 * Variations of 12-hour time *
		 ******************************/
		if (rawTime.matches(TIME_12HM_DOT)) {
			int dotPos = rawTime.indexOf(".");
			timeHour = Integer.parseInt(rawTime.substring(0, dotPos));
			timeMin = Integer.parseInt(rawTime.substring(dotPos + 1, dotPos + 3));
			is12Hour = true;
		} else if (rawTime.matches(TIME_12HM_COLON)) {
			int colonPos = rawTime.indexOf(":");
			timeHour = Integer.parseInt(rawTime.substring(0, colonPos));
			timeMin = Integer.parseInt(rawTime.substring(colonPos + 1, colonPos + 3));
			is12Hour = true;
		} else if (rawTime.matches(TIME_12H)) {
			timeHour = Integer.parseInt(rawTime.split("\\s?[aApP]")[0]);
			timeMin = 0;
			is12Hour = true;
			
		/* ****************************
		 * Variations of 24-hour time *
		 ******************************/
		} else if (rawTime.matches(TIME_24H_DOT)) {
			int dotPos = rawTime.indexOf(".");
			timeHour = Integer.parseInt(rawTime.substring(0, dotPos));
			timeMin = Integer.parseInt(rawTime.substring(dotPos + 1, dotPos + 3));
		} else if (rawTime.matches(TIME_24H_COLON)) {
			int colonPos = rawTime.indexOf(":");
			timeHour = Integer.parseInt(rawTime.substring(0, colonPos));
			timeMin = Integer.parseInt(rawTime.substring(colonPos + 1, colonPos + 3));
		} else if (rawTime.matches(TIME_24H)) {
			timeHour = Integer.parseInt(rawTime.substring(0, 2));
			timeMin = Integer.parseInt(rawTime.substring(2, rawTime.length()));
			
		// Unrecognised time
		} else {
			throw new InvalidDateException("invalid time");
		}
		
		// Conversion to 24-hour time
		if (is12Hour) {
			// Check if time is valid
			if (timeHour == 0) {
				throw new InvalidDateException("invalid time");
			}
			
			if (isPm(rawTime)) {
				if (timeHour != 12) {
					timeHour += 12;
				}
			} else {
				if (timeHour == 12) {
					timeHour = 0;
				}
			}
		}
		
		// Check if time is valid
		if (timeHour > 23) {
			throw new InvalidDateException("invalid time");
		}
		
		calendar.set(Calendar.HOUR_OF_DAY, timeHour);
		calendar.set(Calendar.MINUTE, timeMin);
		return calendar;
	}
	
	// Check if date entered has passed for current year
	private boolean isPastDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		return calendar.before(c);
	}

	
	// Check if time of day is am or pm
	private boolean isPm(String time) {
		String timeOfDay = time.substring(time.length() - 2, time.length());
		return timeOfDay.equalsIgnoreCase("pm");
	}
	
	// Check if date has passed
	public boolean hasDatePassed(Calendar date, Calendar time) {
		Calendar curr = Calendar.getInstance();
		Calendar combined = combineDateTime(date, time);
		return combined.before(curr);
	}
	
	// Combines date and time into a single Calendar
	public Calendar combineDateTime(Calendar date, Calendar time) {
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.YEAR, date.get(Calendar.YEAR));
		c.set(Calendar.MONTH, date.get(Calendar.MONTH));
		c.set(Calendar.DATE, date.get(Calendar.DATE));
		
		c.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		
		return c;
	}
	
	public Calendar getDefaultStartTime() {
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		
		return c;
	}
	
	public Calendar getDefaultEndTime() {
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		
		return c;
	}
	
	public Calendar getDefaultDate() {
		return Calendar.getInstance();
	}
	
	public Calendar getNextDay(Calendar c) {
		Calendar next = c;
		next.add(Calendar.DATE, 1);
		
		return next;
	}
	
	public Calendar getNextYear(Calendar c) {
		Calendar next = c;
		next.add(Calendar.YEAR, 1);
		
		return next;
	}
}