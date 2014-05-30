package utils;

import DAO.ScheduleDate;
import DAO.ScheduleTracePoint;

import java.util.List;

/**
 * Created by alanhawrot on 30.05.14.
 */
public class Result {

    private List<ScheduleTracePoint> trace_points;
    private List<ScheduleDate> departures;

    Result() {
    }

    Result(List<ScheduleTracePoint> trace_points, List<ScheduleDate> departures) {
        this.trace_points = trace_points;
        this.departures = departures;
    }

    public List<ScheduleTracePoint> getTrace_points() {
        return trace_points;
    }

    public void setTrace_points(List<ScheduleTracePoint> trace_points) {
        this.trace_points = trace_points;
    }

    public List<ScheduleDate> getDepartures() {
        return departures;
    }

    public void setDepartures(List<ScheduleDate> departures) {
        this.departures = departures;
    }

}

