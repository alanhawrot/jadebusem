package DAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alanhawrot on 19.03.14.
 */
@SuppressWarnings("serial")
public class Schedule implements Serializable {

    private long id;
    private String name;
    private ArrayList<ScheduleDate> scheduleDates;
    private ArrayList<ScheduleTracePoint> scheduleTracePoints;

    public Schedule() {
        scheduleDates = new ArrayList<ScheduleDate>();
        scheduleTracePoints = new ArrayList<ScheduleTracePoint>();
    }

    public Schedule(int id, String name, List<ScheduleDate> scheduleDates, List<ScheduleTracePoint> scheduleTracePoints) {
        this.id = id;
        this.name = name;
        this.scheduleDates = (ArrayList<ScheduleDate>) scheduleDates;
        this.scheduleTracePoints = (ArrayList<ScheduleTracePoint>) scheduleTracePoints;
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

    public String toStringScheduleTracePoints() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < scheduleTracePoints.size() - 1; i++) {
            sb.append(scheduleTracePoints.get(i).toString());
            sb.append(" -> ");
        }
        sb.append(scheduleTracePoints.get(scheduleTracePoints.size() - 1));

        return sb.toString();
    }

    public String toStringScheduleDateTime(days day) {
        StringBuilder sb = new StringBuilder();

        for (ScheduleDate scheduleDate : scheduleDates) {
            if (scheduleDate.getDay() == day) {
                sb.append(scheduleDate.toString());
                sb.append(", ");
            }
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length() - 1);
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule)) return false;

        Schedule schedule = (Schedule) o;

        if (id != schedule.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

}
