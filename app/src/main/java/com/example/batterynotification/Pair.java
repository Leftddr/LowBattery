package com.example.batterynotification;

public class Pair implements Comparable{
    public long lastTimeUsed;
    public long totalTimeForeGround;
    public long lastTimeStamp;
    public String processName;
    public int idx;
    public boolean service;
    Pair(String processName, long lastTimeStamp, long lastTimeUsed, long totalTimeForeGround) {
        this.processName = processName;
        this.lastTimeStamp = lastTimeStamp;
        this.lastTimeUsed = lastTimeUsed;
        this.totalTimeForeGround = totalTimeForeGround;
    }
    @Override
    public int compareTo(Object pair) {
        Pair tpair = (Pair)pair;
        long diff1 = this.lastTimeStamp - this.lastTimeUsed, diff2 = tpair.lastTimeStamp - tpair.lastTimeUsed;
        if (diff1 <= diff2) return 1;
        else return -1;
    }
}
