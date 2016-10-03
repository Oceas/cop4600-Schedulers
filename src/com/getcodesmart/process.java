package com.getcodesmart;

/**
 * Created by scottanderson on 10/2/16.
 */
public class process {

    private String name;
    private int arrivalTime;
    private int burstTime;
    private int timeLeft;
    private int completionTime;

    public process(String name, int arrivalTime, int burstTime) {
        this.setName(name);
        this.setArrivalTime(arrivalTime);
        this.setBurstTime(burstTime);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public int waitTime (){
        return this.getCompletionTime() - this.getArrivalTime() - this.getBurstTime();
    }

    public int getTurnaroundTime () {
        return this.getCompletionTime() - this.getArrivalTime();
    }

}
