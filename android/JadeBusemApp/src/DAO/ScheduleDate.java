package DAO;

/**
 * Created by Mateusz on 12.03.14.
 */

enum days {
    MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY
}

public class ScheduleDate {
    private long id;
    private long schedule_id;
    private String time;
    private days day;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getSchedule_id()
    {
        return schedule_id;
    }

    public void setSchedule_id(long schedule_id)
    {
        this.schedule_id = schedule_id;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public days getDay()
    {
        return day;
    }

    public void setDay(days day)
    {
        this.day = day;
    }

    public days toEnum(String day)
    {
        if(day.equalsIgnoreCase("MONDAY")){
            return days.MONDAY;
        }
        else if(day.equalsIgnoreCase("TUESDAY")){
            return days.TUESDAY;
        }
        else if(day.equalsIgnoreCase("WEDNESDAY")){
            return days.WEDNESDAY;
        }
        else if(day.equalsIgnoreCase("THURSDAY")){
            return days.THURSDAY;
        }
        else if(day.equalsIgnoreCase("FRIDAY")){
            return days.FRIDAY;
        }
        else if(day.equalsIgnoreCase("SATURDAY")){
            return days.SATURDAY;
        }
        else if(day.equalsIgnoreCase("SUNDAY")){
            return days.SUNDAY;
        }
        return days.MONDAY;
    }


    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return time + " " + String.valueOf(day);
    }
}
