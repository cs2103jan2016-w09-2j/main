package tucklife.parser;

import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/*
 * Supported date formats:
 * - Text dates:
 *   - Both short month names (jan, feb) and full month names (january, february)
 *   - ddmmm or ddmmmyy or ddmmmyyyy
 *   - dd mmm or dd mmm yy or dd mmm yyyy
 *   - dd-mmm or dd-mmm-yy or dd-mmm-yyyy
 *   
 * - Number dates:
 *   - dd/mm or dd/mm/yy or dd/mm/yyyy
 *   - dd.mm or dd.mm.yy or dd.mm.yyyy
 *   - dd-mm or dd-mm-yy or dd-mm-yyyy
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
	
	// Format: ddmmmyy (short month)
	private static final String DATE_SHORT_DMY = "[0-3]?\\d[a-zA-Z]{3}(\\d{2}|\\d{4})";
	
	// Format: dd mmm yy (short month)
	private static final String DATE_SHORT_DMY_SPACE = "[0-3]?\\d\\s[a-zA-Z]{3}\\s(\\d{2}|\\d{4})";
	
	// Format: dd-mmm (short month)
	private static final String DATE_SHORT_DMY_DASH = "[0-3]?\\d-[a-zA-Z]{3}-(\\d{2}|\\d{4})";
	
	// Format: ddmmm (short month)
	private static final String DATE_SHORT_DM = "[0-3]?\\d[a-zA-Z]{3}";
	
	// Format: dd mmm (short month)
	private static final String DATE_SHORT_DM_SPACE = "[0-3]?\\d\\s[a-zA-Z]{3}";
	
	// Format: dd-mmm (short month)
	private static final String DATE_SHORT_DM_DASH = "[0-3]?\\d-[a-zA-Z]{3}";
	
	// Format: ddmmmm (full month)
	private static final String DATE_FULL_DMY = "[0-3]?\\d[a-zA-Z]{4,9}(\\d{2}|\\d{4})";

	// Format: dd mmmm (full month)
	private static final String DATE_FULL_DMY_SPACE = "[0-3]?\\d\\s[a-zA-Z]{4,9}\\s(\\d{2}|\\d{4})";

	// Format: dd-mmmm (full month)
	private static final String DATE_FULL_DMY_DASH = "[0-3]?\\d-[a-zA-Z]{4,9}-(\\d{2}|\\d{4})";

	// Format: ddmmmm (full month)
	private static final String DATE_FULL_DM = "[0-3]?\\d[a-zA-Z]{4,9}";
	
	// Format: dd mmmm (full month)
	private static final String DATE_FULL_DM_SPACE = "[0-3]?\\d\\s[a-zA-Z]{4,9}";
	
	// Format: dd-mmmm (full month)
	private static final String DATE_FULL_DM_DASH = "[0-3]?\\d-[a-zA-Z]{4,9}";
	
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
	
	public Calendar parseDate(String rawDate, String rawTime) throws InvalidDateException {
		if (rawTime.isEmpty()) {
			timeHour = 23;
			timeMin = 59;
		} else {
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
			
			if (is12Hour) {
				if (timeHour == 12) {
					timeHour = 0;
				}
				
				// Check if time is valid
				if (timeHour > 12 || timeHour == 0) {
					throw new InvalidDateException("invalid time");
				}

				// Conversion to 24-hour time
				if (isPm(rawTime)) {
					timeHour += 12;
				}
			} else {
				// Check if time is valid
				if (timeHour > 23) {
					throw new InvalidDateException("invalid time");
				}
			}
		}
		
		if (rawDate.isEmpty()) {
			setTime();
			
			if (isPastTime()) {
				calendar.add(Calendar.DATE, 1);
			}

			return calendar;
		} else {
			if (rawDate.equalsIgnoreCase("today")) {
				setTime();
				return calendar;
			} else if (rawDate.equalsIgnoreCase("tomorrow") || rawDate.equalsIgnoreCase("tmr")) {
				calendar.add(Calendar.DATE, 1);
				setTime();
				return calendar;
			} else {
				SimpleDateFormat sdf;
				
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

					// Set time
					setTime();
					
					return calendar;
				} catch (ParseException e) {
					throw new InvalidDateException("invalid date");
				}				
			}
		}
	}
	
	// Check if date entered has passed for current year
	private boolean isPastDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		return calendar.before(c);
	}
	
	// Check if time entered has passed for today
	private boolean isPastTime() {
		Calendar c = Calendar.getInstance();
		return calendar.before(c);
	}
	
	// Check if time of day is am or pm
	private boolean isPm(String time) {
		String timeOfDay = time.substring(time.length() - 2, time.length());
		return timeOfDay.equalsIgnoreCase("pm");
	}
	
	private void setTime() {
		calendar.set(Calendar.HOUR_OF_DAY, timeHour);
		calendar.set(Calendar.MINUTE, timeMin);
	}
}