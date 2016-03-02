package tucklife.parser;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DateParser {
	
	private Calendar calendar;
	
	public DateParser() {
		calendar = Calendar.getInstance();
	}
	
	public Calendar getDate() {
		return calendar;
	}
	
	public boolean parseDate(String toParse) {
		if (toParse.equalsIgnoreCase("tomorrow") || toParse.equalsIgnoreCase("tmr")) {
			calendar.add(Calendar.DATE, 1);
			return true;
		} else {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yy");
				Date d = sdf.parse(toParse);
				calendar.setTime(d);
				
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}
	
	public void parseTime(String toParse) {
		
	}
}
