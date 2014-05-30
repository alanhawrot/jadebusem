package DAO;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ScheduleTracePoint implements Serializable {

    private long id;
    private long schedule_id;
    private String address;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(long schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
