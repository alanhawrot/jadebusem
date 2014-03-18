package DAO;

public class ScheduleTracePoint {
    private long id;
    private String address;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return address;
    }
}
