package utils;

import DAO.ScheduleTracePoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alanhawrot on 30.05.14.
 */
public class RequestTypeForSynchronizeScheduleMethod {

    private String email;
    private String company_name;
    private List<ScheduleTracePoint> trace_points;
    private List<Hour>[] days;

    public RequestTypeForSynchronizeScheduleMethod() {
    }

    public RequestTypeForSynchronizeScheduleMethod(String email, String company_name, List<ScheduleTracePoint> trace_points) {
        this.email = email;
        this.company_name = company_name;
        this.trace_points = trace_points;
        this.days = new List[7];
        for (int i = 0; i < 7; i++) {
            days[i] = new ArrayList<>();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public List<ScheduleTracePoint> getTrace_points() {
        return trace_points;
    }

    public void setTrace_points(List<ScheduleTracePoint> trace_points) {
        this.trace_points = trace_points;
    }

    public List<Hour>[] getDays() {
        return days;
    }

    public void setDays(List<Hour>[] days) {
        this.days = days;
    }

    @Override
    public String toString() {
        return "RequestTypeForSynchronizeScheduleMethod{" +
                "email='" + email + '\'' +
                ", company_name='" + company_name + '\'' +
                ", trace_points=" + trace_points +
                ", days=" + Arrays.toString(days) +
                '}';
    }
}
