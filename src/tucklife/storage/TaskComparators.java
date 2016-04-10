//@@author A0111101N
package tucklife.storage;

import java.util.Comparator;
import java.util.Calendar;

public class TaskComparators {
	
	private static int handleNullCases(Task task1, Task task2, String param1, String param2) {
		if(param1 == null && param2 == null) {
			return new ComparatorName().compare(task1, task2);
		}
		else{
			if(param1 == null) {
				return 1;
			}
			if(param2 == null) {
				return -1;
			}
			if(param1.compareTo(param2)==0) {
				return new ComparatorName().compare(task1, task2);
			}
		}
		return param1.compareTo(param2);
	}
	
	public static class ComparatorLocation implements Comparator<Task> {

     @Override
     public int compare(Task t1, Task t2) {
            String location1 = t1.getLocation();
            String location2 = t2.getLocation();
            return handleNullCases(t1, t2, location1, location2);
         }
    }
	
	public static class ComparatorPriority implements Comparator<Task> {

     @Override
     public int compare(Task t1, Task t2) {
            int priority1 = t1.getPriority();
            int priority2 = t2.getPriority();

            if (priority1 - priority2 == 0) {
            	return new ComparatorName().compare(t1, t2);
            } else {
            	return priority1 - priority2;
            }
         }
    }
	
	public static class ComparatorCategory implements Comparator<Task> {

     @Override
     public int compare(Task t1, Task t2) {
            String cat1 = t1.getCategory();
            String cat2 = t2.getCategory();
            return handleNullCases(t1, t2, cat1, cat2);
         }
    }
	
	public static class ComparatorAdditional implements Comparator<Task>
    {

     @Override
     public int compare(Task t1, Task t2) {
            String add1 = t1.getAdditional();
            String add2 = t2.getAdditional();

            return handleNullCases(t1, t2, add1, add2);
         }
    }
	
	public static class ComparatorName implements Comparator<Task> {

     @Override
     public int compare(Task t1, Task t2) {
            String name1 = t1.getName();
            String name2 = t2.getName();

            return name1.compareTo(name2);
         }
    }
	
	public static class ComparatorDefault implements Comparator<Task> {

     @Override
     public int compare(Task t1, Task t2) {
            int qid1 = t1.getQueueID();
            int qid2 = t2.getQueueID();
            if(qid1 == qid2) {
            	return new ComparatorTime().compare(t1, t2);
            }
            else{
            	if(qid1 == -1) {
            		return -qid1;
            	}
            	
            	if(qid2 == -1) {
            		return qid2;
            	}
            	return qid1 - qid2;
            }
         }
    }
	
	public static class ComparatorTime implements Comparator<Task> {

     @Override
     public int compare(Task t1, Task t2) {
            Calendar sd1 = t1.getStartDate();
            Calendar sd2 = t2.getStartDate();
            Calendar ed1 = t1.getEndDate();
            Calendar ed2 = t2.getEndDate();
            
            Calendar d1;
            Calendar d2;
            
            if(sd1 == null) {
            	d1 = ed1;
            } else {
            	d1 = sd1;
            }
            
            if(sd2 == null) {
            	d2 = ed2;
            } else {
            	d2 = sd2;
            }
            
            if(t1.isFloating() && t2.isFloating()){
            	return new ComparatorPriority().compare(t1, t2);
            } else {
            	if(d1 == null) {
            		return 1;
            	}
            	if(d2 == null) {
            		return -1;
            	}
            	return d1.compareTo(d2);
            }
         }
    }
}
