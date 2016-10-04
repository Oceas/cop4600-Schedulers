/*
    Group Members: Scott Anderson, Travis Le, and Luis Velez
    COP 4600 Assignment 1 Schedulers
    October 2, 2016
 */
package com.getcodesmart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class scheduler {

    //Variables for reading in the input data
    private String currentDirectory = System.getProperty("user.dir") + "/src/com/getcodesmart/";
    private String fileName;
    private String filePath;

    //Variables for the schedulers execution
    List<process> processes = new ArrayList<>();
    private int processCount;
    private int timeBlock;
    private String schedulerType;
    private int timeQuantum;

    public static void main(String[] args) {
        scheduler scheduler = new scheduler();
        scheduler.initialize();
    }

    public void initialize(){
        setFileName("processes.in");
        setFilePath(getCurrentDirectory() + getFileName());
        processInputFile(getFilePath());
        executeScheduler();
    }

    private void processInputFile(String filePath){
        try {
            for (String line : Files.readAllLines(Paths.get(filePath))) {
                line = line.split("#")[0];
                evaluateFileLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void evaluateFileLine(String line){
        String command = line.split(" ")[0];
        int parameterValue;

        switch(command){
            case "processcount":
                parameterValue = Integer.parseInt(line.split(" ")[1]);
                setProcessCount(parameterValue);
                break;
            case "runfor":
                parameterValue = Integer.parseInt(line.split(" ")[1]);
                setTimeBlock(parameterValue);
                break;
            case "use":
                String commandValue = line.split(" ")[1];
                setSchedulerType(commandValue);
                break;
            case "quantum":
                parameterValue = Integer.parseInt(line.split(" ")[1]);
                setTimeQuantum(parameterValue);
                break;
            case "process":
                String processName = line.split(" ")[2];
                int processArrivalTime = Integer.parseInt(line.split(" ")[4]);
                int processBurstTime = Integer.parseInt(line.split(" ")[6]);
                addProcess(new process(processName,processArrivalTime,processBurstTime));
                break;
            case "end":
                break;
        }
    }

    private void executeScheduler(){
        System.out.println(getProcessCount() + " processes");
        switch(getSchedulerType()){
            case "fcfs":
                sortByArrivalTime();
                System.out.println("Using First-Come First-Served\n");
                executeFCFS();
                break;
            case "sjf":
                executeSJF();
                break;
            case "rr":
                executeRR();
                break;
        }
        System.out.println("Finished at time " + getTimeBlock() + "\n");
        printWaitAndTurnTimes();
    }

    private void executeFCFS(){
        for(int time = 0; time <= getTimeBlock(); time++){
            processFCFSCompletionTime(time);
            processArrivalTime(time);

            if(processRunning()){
                //Do not schedule a new process
            }else{
                //Attempt to schedule a new process
                fcfsScheduleNewProcess(time);
            }
            processIdleTime(time);
        }
    }

    private void executeSJF(){

    }

    private void executeRR(){

    }

    private void fcfsScheduleNewProcess(int time){
        for(process process : processes){
            if(process.isArrived() && !process.isComplete()){
                process.setRunning(true);
                process.setCompletionTime(time + process.getBurstTime());
                System.out.println("Time " + time + ": " + process.getName() + " selected (burst " + process.getBurstTime()+")");
                break;
            }
        }
    }

    private boolean processRunning(){
        boolean isProcessRunning = false;

        for(process process : processes){
            if(process.isRunning()){
                isProcessRunning = true;
            }
        }
        return isProcessRunning;
    }

    private void processArrivalTime(int time){
        for(process process : processes){
            if(process.getArrivalTime() == time){
                process.setArrived(true);
                System.out.println("Time " + time + ": " + process.getName() + " arrived");
            }
        }
    }

    private void processFCFSCompletionTime(int time){
        for (process process : processes){
            if (process.isRunning() && process.getCompletionTime() == time){
                process.setRunning(false);
                process.setComplete(true);
                System.out.println("Time " + (time) + ": " + process.getName() + " finished");
            }
        }
    }

    private void processIdleTime(int time){

        boolean schedulerIdling = (!processRunning()) && (time != getTimeBlock());

        if(schedulerIdling){
            System.out.println("Time "  + time + ":" + " Idle");
        }
    }

    private void printWaitAndTurnTimes() {
        for(process process : processes){
            System.out.println(process.getName() + " wait " + process.waitTime() + " turnaround " + process.getTurnaroundTime());
        }
    }

    private void sortByArrivalTime(){
        Collections.sort(processes, new ProcessComparer());
    }

    public void addProcess(process newProcess){
        processes.add(newProcess);
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getProcessCount() {
        return processCount;
    }

    public void setProcessCount(int processCount) {
        this.processCount = processCount;
    }

    public int getTimeBlock() {
        return timeBlock;
    }

    public void setTimeBlock(int timeBlock) {
        this.timeBlock = timeBlock;
    }

    public String getSchedulerType() {
        return schedulerType;
    }

    public void setSchedulerType(String schedulerType) {
        this.schedulerType = schedulerType;
    }

    public int getTimeQuantum() {
        return timeQuantum;
    }

    public void setTimeQuantum(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }
}
