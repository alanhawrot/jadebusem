package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alanhawrot on 30.05.14.
 */
public class Day {

    private List<Hour> hours;

    public Day() {
        this.hours = new ArrayList<>();
    }

    public Day(List<Hour> hours) {
        this.hours = hours;
    }

    public List<Hour> getHours() {
        return hours;
    }

    public void setHours(List<Hour> hours) {
        this.hours = hours;
    }

    @Override
    public String toString() {
        return "Day{" +
                "hours=" + hours +
                '}';
    }
}
