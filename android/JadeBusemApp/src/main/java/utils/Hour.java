package utils;

/**
 * Created by alanhawrot on 30.05.14.
 */
public class Hour {

    private String hour;

    public Hour() {
    }

    public Hour(String hour) {
        this.hour = hour;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return "Hour{" +
                "hour='" + hour + '\'' +
                '}';
    }
}
