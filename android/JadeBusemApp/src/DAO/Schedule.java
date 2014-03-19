package DAO;

import java.util.ArrayList;

/**
 * Created by alanhawrot on 19.03.14.
 */
public class Schedule {
    private long id;
    private String name;
    private ArrayList<ScheduleDate> scheduleDates;
    private ArrayList<ScheduleTracePoint> scheduleTracePoints;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ScheduleDate> getScheduleDates() {
        return scheduleDates;
    }

    public void setScheduleDates(ArrayList<ScheduleDate> scheduleDates) {
        this.scheduleDates = scheduleDates;
    }

    public ArrayList<ScheduleTracePoint> getScheduleTracePoints() {
        return scheduleTracePoints;
    }

    public void setScheduleTracePoints(ArrayList<ScheduleTracePoint> scheduleTracePoints) {
        this.scheduleTracePoints = scheduleTracePoints;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "name='" + name + '\'' +
                '}';
    }
}
