package com.getcodesmart;

/**
 * Created by scottanderson on 10/2/16.
 */
public class CustomProcess {

    private String name;
    private int arrivalTime;
    private int burstTime;
    private int burstTimeLeft;
    private int completionTime;
    private boolean arrived;
    private boolean running;
    private boolean complete;
    private int lastRan;
    private int indexInList;
    private int timeLeft;

    public CustomProcess(String name, int arrivalTime, int burstTime) {
        this.setName(name);
        this.setArrivalTime(arrivalTime);
        this.setBurstTime(burstTime);
        this.setBurstTimeLeft(burstTime);
        this.setComplete(false);
        this.setRunning(false);
        this.setArrived(false);
        this.timeLeft = this.getBurstTime();

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

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isArrived() {
        return arrived;
    }

    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }

    public int getLastRan() { return lastRan; }

    public void setLastRan(int lastRan) { this.lastRan = lastRan; }

    public int getIndexInList() { return indexInList; }

    public void setIndexInList(int indexInList) { this.indexInList = indexInList; }

    public int getBurstTimeLeft() {
        return burstTimeLeft;
    }

    public void setBurstTimeLeft(int burstTimeLeft) {
        this.burstTimeLeft = burstTimeLeft;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }
}


