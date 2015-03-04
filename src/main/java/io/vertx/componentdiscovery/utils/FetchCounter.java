package io.vertx.componentdiscovery.utils;

public class FetchCounter {
    private int total;
    private int fetched;

    public int getCount() {
        return total;
    }

    public void setCount(int count) {
        this.total = count;
    }

    public int getFetched() {
        return fetched;
    }

    public void addFetched(int fetched) {
        this.fetched += fetched;
    }

    public boolean finished() {
        return fetched >= total;
    }
}
