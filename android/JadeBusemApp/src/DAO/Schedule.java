package DAO;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by alanhawrot on 19.03.14.
 */
public class Schedule implements Serializable {
    private long id;
    private String name;
    private ArrayList<ScheduleDate> scheduleDates;
    private ArrayList<ScheduleTracePoint> scheduleTracePoints;

    public Schedule() {
        scheduleDates = new ArrayList<ScheduleDate>();
        scheduleTracePoints = new ArrayList<ScheduleTracePoint>();
    }

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
        return name + ": " + scheduleTracePoints.get(0).getAddress() + " -> ... -> "
                + scheduleTracePoints.get(scheduleTracePoints.size() - 1);
    }
}
