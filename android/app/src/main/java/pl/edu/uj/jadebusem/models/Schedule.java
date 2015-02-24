package pl.edu.uj.jadebusem.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alanhawrot on 07.02.15.
 */
public class Schedule implements Serializable {

    private long id; // local db id
    private long web_schedule_id; // web-service id
    private String author;
    private String companyName;
    private List<Tracepoint> tracepoints;
    private List<Departure> departures;

    public Schedule() {
    }

    public Schedule(long id, long web_schedule_id, String author, String companyName, List<Tracepoint> tracepoints,
                    List<Departure> departures) {
        this.id = id;
        this.web_schedule_id = web_schedule_id;
        this.author = author;
        this.companyName = companyName;
        this.tracepoints = tracepoints;
        this.departures = departures;
    }

    public Schedule(long id, String author, List<Tracepoint> tracepoints, String companyName,
                    List<Departure> departures) {
        this.id = id;
        this.author = author;
        this.tracepoints = tracepoints;
        this.companyName = companyName;
        this.departures = departures;
    }

    public Schedule(String author, String companyName, List<Tracepoint> tracepoints, List<Departure> departures) {
        this.author = author;
        this.companyName = companyName;
        this.tracepoints = tracepoints;
        this.departures = departures;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getWeb_schedule_id() {
        return web_schedule_id;
    }

    public void setWeb_schedule_id(long web_schedule_id) {
        this.web_schedule_id = web_schedule_id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<Tracepoint> getTracepoints() {
        return tracepoints;
    }

    public void setTracepoints(List<Tracepoint> tracepoints) {
        this.tracepoints = tracepoints;
    }

    public List<Departure> getDepartures() {
        return departures;
    }

    public void setDepartures(List<Departure> departures) {
        this.departures = departures;
    }

    @Override
    public String toString() {
        return companyName + ": " + tracepoints.get(0).getName() + "->" + tracepoints.get(tracepoints.size() - 1)
                .getName() + (web_schedule_id != 0 ? "" : " (L)");
    }
}
