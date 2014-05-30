package DAO;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ScheduleDate implements Serializable {

    private long id;
    private long schedule_id;
    private String time;
    private days day;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(long schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public days getDay() {
        return day;
    }

    public void setDay(days day) {
        this.day = day;
    }

    public days toEnum(String day) {
        if (day.equalsIgnoreCase("MONDAY")) {
            return days.MONDAY;
        } else if (day.equalsIgnoreCase("TUESDAY")) {
            return days.TUESDAY;
        } else if (day.equalsIgnoreCase("WEDNESDAY")) {
            return days.WEDNESDAY;
        } else if (day.equalsIgnoreCase("THURSDAY")) {
            return days.THURSDAY;
        } else if (day.equalsIgnoreCase("FRIDAY")) {
            return days.FRIDAY;
        } else if (day.equalsIgnoreCase("SATURDAY")) {
            return days.SATURDAY;
        } else if (day.equalsIgnoreCase("SUNDAY")) {
            return days.SUNDAY;
        }
        return days.MONDAY;
    }

    public days toEnumFromInt(int day)
    {
        if(day == 0){
            return days.MONDAY;
        }
        else if(day == 1){
            return days.TUESDAY;
        }
        else if(day == 2){
            return days.WEDNESDAY;
        }
        else if(day == 3){
            return days.THURSDAY;
        }
        else if(day == 4){
            return days.FRIDAY;
        }
        else if(day == 5){
            return days.SATURDAY;
        }
        else if(day == 6){
            return days.SUNDAY;
        }
        return days.MONDAY;
    }

    public int toIntFromEnum(days day)
    {
        if(day.equals(day.MONDAY))
        {
            return 0;
        }
        else if(day.equals(day.TUESDAY))
        {
            return 1;
        }
        else if(day.equals(day.WEDNESDAY))
        {
            return 2;
        }
        else if(day.equals(day.THURSDAY))
        {
            return 3;
        }
        else if(day.equals(day.FRIDAY))
        {
            return 4;
        }
        else if(day.equals(day.SATURDAY))
        {
            return 5;
        }
        else if(day.equals(day.SUNDAY))
        {
            return 6;
        }
        return 0;
    }


    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return time;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ScheduleDate))
			return false;
		ScheduleDate other = (ScheduleDate) obj;
		if (id != other.id)
			return false;
		return true;
	}
    
}
