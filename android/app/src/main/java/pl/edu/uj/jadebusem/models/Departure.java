package pl.edu.uj.jadebusem.models;

import java.io.Serializable;

/**
 * Created by alanhawrot on 07.02.15.
 */
public class Departure implements Serializable {

    private long id; // local db id
    private Days day;
    private String hour;
    private long schedule_id; // foreign key

    public Departure() {
    }

    public Departure(long id, Days day, String hour, long schedule_id) {
        this.id = id;
        this.day = day;
        this.hour = hour;
        this.schedule_id = schedule_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Days getDay() {
        return day;
    }

    public int getDayInt() {
        return day.ordinal();
    }

    public void setDay(Days day) {
        this.day = day;
    }

    public void setDayByInt(int ordinal) {
        day = Days.values()[ordinal];
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public long getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(long schedule_id) {
        this.schedule_id = schedule_id;
    }

    @Override
    public String toString() {
        return hour;
    }
}
