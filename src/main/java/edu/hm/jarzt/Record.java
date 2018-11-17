package edu.hm.jarzt;

public class Record {
    private int time;
    private long dataAmount;

     Record(int time, long dataAmount) {
        this.time = time;
        this.dataAmount = dataAmount;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

     long getDataAmount() {
        return dataAmount;
    }

    public void setDataAmount(int dataAmount) {
        this.dataAmount = dataAmount;
    }
}
