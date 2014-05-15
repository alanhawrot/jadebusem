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
