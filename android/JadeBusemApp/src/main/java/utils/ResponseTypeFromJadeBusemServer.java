package utils;

import java.util.List;

/**
 * Created by alanhawrot on 30.05.14.
 */
public class ResponseTypeFromJadeBusemServer {

    private int count;
    private String next;
    private String previous;
    private List<Result> results;

    ResponseTypeFromJadeBusemServer() {
    }

    ResponseTypeFromJadeBusemServer(int count, String next, String previous, List<Result> results) {
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

}
