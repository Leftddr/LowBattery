package com.example.batterynotification;

public class Pair implements Comparable{
    public Float memory;
    public Float cpu;
    public int idx;
    public boolean service;
    Pair(Float memory, Float cpu, int idx, boolean service) {
        this.memory = memory;
        this.cpu = cpu;
        this.idx = idx;
        this.service = service;
    }

    @Override
    public int compareTo(Object pair) {
        Pair t = (Pair)pair;
        Float cost1 = this.memory * this.cpu;
        Float cost2 = t.memory * t.cpu;
        if(cost1 < cost2) return -1;
        else return 1;
    }
}
