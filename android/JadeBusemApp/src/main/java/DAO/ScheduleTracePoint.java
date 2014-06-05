package DAO;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ScheduleTracePoint implements Serializable {

    private long id;
    private long schedule;
    private String address;
    private int position;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSchedule() {
        return schedule;
    }

    public void setSchedule(long schedule) {
        this.schedule = schedule;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return address;
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
		if (!(obj instanceof ScheduleTracePoint))
			return false;
		ScheduleTracePoint other = (ScheduleTracePoint) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
