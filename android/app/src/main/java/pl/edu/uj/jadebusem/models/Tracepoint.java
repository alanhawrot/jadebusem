package pl.edu.uj.jadebusem.models;

import java.io.Serializable;

/**
 * Created by alanhawrot on 07.02.15.
 */
public class Tracepoint implements Serializable {

    private long id; // local db id
    private int ord;
    private String name;
    private long schedule_id; // foreign key

    public Tracepoint() {
    }

    public Tracepoint(long id, int ord, String name, long schedule_id) {
        this.id = id;
        this.ord = ord;
        this.name = name;
        this.schedule_id = schedule_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOrd() {
        return ord;
    }

    public void setOrd(int ord) {
        this.ord = ord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(long schedule_id) {
        this.schedule_id = schedule_id;
    }

    @Override
    public String toString() {
        return name;
    }
}
