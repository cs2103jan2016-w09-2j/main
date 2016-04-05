// @@author A0121352X-unused
// this was a potential implementation for a recurring task - feature was abandoned
package tucklife.storage;

import java.util.Calendar;

import tucklife.parser.ProtoTask;

public class RecurringTask {
	
	private String description;
	private String location;
	private String category;
	private int priority;
	private String additional;
	
	private int recurHourStart, recurHourEnd;
	private int recurMinuteStart, recurMinuteEnd;
	private int recurDayStart, recurDayEnd;
	private int recurDateStart, recurDateEnd;
	
	private int lastOccurringDayStart, lastOccurringDayEnd;
	private int lastOccurringMonthStart, lastOccurringMonthEnd;
	private int lastOccurringWeekStart, lastOccurringWeekEnd;
	
	private int recurID;
	private int freq;
	private static int globalID = 1;
	
	public RecurringTask(Task t, int freq){
		/*
		 * freq:
		 * 0 - day (use recurHour and recurMinute)
		 * 1 - week (use recurDay)
		 * 2 - month (use recurDate)
		 */
		
		// need a way to check if there are two dates
		Calendar cStart = t.getStartDate();
		Calendar cEnd = t.getEndDate();
		
		// these are needed regardless of recurrence type
		recurHourStart = cStart.get(Calendar.HOUR_OF_DAY);
		recurHourEnd = cEnd.get(Calendar.HOUR_OF_DAY);
			
		recurMinuteStart = cStart.get(Calendar.MINUTE);
		recurDateEnd = cEnd.get(Calendar.MINUTE);
		
		if(freq == 0){
			lastOccurringDayStart = cStart.get(Calendar.DAY_OF_YEAR);
			lastOccurringDayEnd = cEnd.get(Calendar.DAY_OF_YEAR);
			
		} else if(freq == 1){
			recurDayStart = cStart.get(Calendar.DAY_OF_WEEK);
			recurDayEnd = cEnd.get(Calendar.DAY_OF_WEEK);
			lastOccurringWeekStart = cStart.get(Calendar.WEEK_OF_YEAR);
			lastOccurringWeekEnd = cEnd.get(Calendar.WEEK_OF_YEAR);
			
		} else if(freq == 2){
			recurDateStart = cStart.get(Calendar.DAY_OF_MONTH);
			recurDateEnd = cEnd.get(Calendar.DAY_OF_MONTH);
			lastOccurringMonthStart = cStart.get(Calendar.MONTH);
			lastOccurringMonthEnd = cEnd.get(Calendar.MONTH);
		}
		
		description = t.getName();
		location = t.getLocation();
		category = t.getCategory();
		additional = t.getAdditional();
		priority = t.getPriority();
		
		this.freq = freq;
		recurID = globalID;
		globalID++;
	}
	
	public int getID(){
		return recurID;
	}
	
	private Calendar[] generateNextOccurence(){
		
		Calendar cStart = Calendar.getInstance();
		Calendar cEnd = Calendar.getInstance();
		
		cStart.set(Calendar.HOUR_OF_DAY, recurHourStart);
		cStart.set(Calendar.MINUTE, recurMinuteStart);
		
		cEnd.set(Calendar.HOUR_OF_DAY, recurHourEnd);
		cEnd.set(Calendar.MINUTE, recurMinuteEnd);
		
		// code to advance to next occurrence
		// need to find out how to handle corner cases
		if(freq == 0){
			// advance both to the next day
			cStart.set(Calendar.DAY_OF_YEAR, lastOccurringDayStart + 1);
			cEnd.set(Calendar.DAY_OF_YEAR, lastOccurringDayEnd + 1);
			lastOccurringDayStart++;
			lastOccurringDayEnd++;
			
		} else if(freq == 1){
			// advance both to next week
			cStart.set(Calendar.WEEK_OF_YEAR, lastOccurringWeekStart + 1);
			cStart.set(Calendar.DAY_OF_WEEK, recurDayStart);	
			cEnd.set(Calendar.WEEK_OF_YEAR, lastOccurringWeekEnd + 1);
			cEnd.set(Calendar.DAY_OF_WEEK, recurDayEnd);
			lastOccurringWeekStart++;
			lastOccurringWeekEnd++;
			
		} else if(freq == 2){
			// advance both to next month
			cStart.set(Calendar.MONTH, lastOccurringMonthStart + 1);
			cStart.set(Calendar.DAY_OF_MONTH, recurDateStart);	
			cEnd.set(Calendar.MONTH, lastOccurringMonthEnd + 1);
			cEnd.set(Calendar.DAY_OF_MONTH, recurDateEnd);
			lastOccurringMonthStart++;
			lastOccurringMonthEnd++;
		}
		
		return new Calendar[]{cStart, cEnd};
	}
	
	public ProtoTask createNextOccurence(){
		ProtoTask pt = new ProtoTask("add");
		
		pt.setTaskDesc(description);
		pt.setLocation(location);
		pt.setCategory(category);
		pt.setPriority(priority);
		pt.setAdditional(additional);
		
		Calendar[] nextDates = generateNextOccurence();
		
		pt.setStartDate(nextDates[0]);
		pt.setEndDate(nextDates[1]);
		
		return pt;
	}
}
