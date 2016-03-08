package tucklife.parser;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/*
 * Supported date formats:
 * - dd/mm or dd/mm/yy or dd/mm/yyyy
 * - dd.mm or dd.mm.yy or dd.mm.yyyy
 * - dd-mm or dd-mm-yy or dd-mm-yyyy
 *
 * Supported time formats:
 * - Not yet ):
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
 * Issues:
 * - TIME
 * - If 29/2 is entered, what to return:
 *   - next available leap year
 *   - next available year, but on 28/2
 *   - next available year, but on 1/3
 */

public class DateParser {
	
	private Calendar calendar;
	
	// Format: dd/mm/yy or dd/mm/yyyy
	private final String DATE_DMY_SLASH = "[0-3]?\\d/[01]?\\d/(\\d{2}|\\d{4})";
	
	// Format: dd.mm.yy or dd.mm.yyyy
	private final String DATE_DMY_DOT = "[0-3]?\\d\\.[01]?\\d\\.(\\d{2}|\\d{4})";
	
	// Format: dd-mm-yy or dd-mm-yyyy
	private final String DATE_DMY_DASH = "[0-3]?\\d-[01]?\\d-(\\d{2}|\\d{4})";
	
	// Format: dd/mm
	private final String DATE_DM_SLASH = "[0-3]?\\d/[01]?\\d";
	
	// Format: dd.mm
	private final String DATE_DM_DOT = "[0-3]?\\d\\.[01]?\\d";
	
	// Format: dd-mm
	private final String DATE_DM_DASH = "[0-3]?\\d-[01]?\\d";
	
	// Indicates whether the year was included in user-entered date
	private boolean hasYear = false;
	
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
		}
		
		if (rawDate.equalsIgnoreCase("tomorrow") || rawDate.equalsIgnoreCase("tmr")) {
			calendar.add(Calendar.DATE, 1);
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
				
				calendar.set(Calendar.HOUR_OF_DAY, timeHour);
				calendar.set(Calendar.MINUTE, timeMin);
				
				return true;
				
			} catch (Exception e) {
				return false;
			}
		}
	}
	
	// Checks if date entered has passed for current year
	private boolean isPastDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		return calendar.before(c);
	}
}