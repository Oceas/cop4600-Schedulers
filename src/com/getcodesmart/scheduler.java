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

    private int arrivalTimeIndex = 0;
    private int fcfsProcessCounter = 0;
    private int nextAvailableTime;
    private boolean fcfsOnLastProcess = false;
    private int idleStartTime = 0;

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
                System.out.println("This is the processcount");
                parameterValue = Integer.parseInt(line.split(" ")[1]);
                setProcessCount(parameterValue);
                break;
            case "runfor":
                System.out.println("This is the timeblock");
                parameterValue = Integer.parseInt(line.split(" ")[1]);
                setTimeBlock(parameterValue);
                break;
            case "use":
                System.out.println("This is the command");
                String commandValue = line.split(" ")[1];
                setSchedulerType(commandValue);
                break;
            case "quantum":
                System.out.println("This is the time quantum");
                parameterValue = Integer.parseInt(line.split(" ")[1]);
                setTimeQuantum(parameterValue);
                break;
            case "process":
                System.out.println("This is a process");
                String processName = line.split(" ")[2];
                int processArrivalTime = Integer.parseInt(line.split(" ")[4]);
                int processBurstTime = Integer.parseInt(line.split(" ")[6]);
                addProcess(new process(processName,processArrivalTime,processBurstTime));

                break;
            case "end":
                System.out.println("This is the end of the file");
                break;
        }
    }

    private void executeScheduler(){
        sortByArrivalTime();
        calculateIdleStartTime();

        System.out.println(getProcessCount() + " processes");
        switch(getSchedulerType()){
            case "fcfs":
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

        printWaitAndTurnTimes();
    }

    private void executeFCFS(){
        System.out.println("Executing FCFS");
        for(int time = 0; time <= getTimeBlock(); time++){
            processCompletionTime(time);
            processArrivalTime(time);
            processIdleAndCompletionTime(time);
            process currentProcess = processes.get(fcfsProcessCounter);
            if(time == nextAvailableTime){
                System.out.println("Time " + time + ": " + currentProcess.getName() + " selected (burst " + currentProcess.getBurstTime() + ")");
                currentProcess.setCompletionTime(time + currentProcess.getBurstTime());
                if(fcfsOnLastProcess != true){
                    nextAvailableTime = time + currentProcess.getBurstTime();
                }
                if(fcfsProcessCounter < getProcessCount() - 1){
                    fcfsProcessCounter++;
                    if(fcfsProcessCounter == getProcessCount() - 1){
                        fcfsOnLastProcess = true;
                    }
                }
            }
        }

    }

    private void executeSJF(){

    }

    private void executeRR(){

    }

    private void processArrivalTime(int time){
        process nextProcess = processes.get(arrivalTimeIndex);
        if(time == nextProcess.getArrivalTime()){
            System.out.println("Time " + time + ": " + nextProcess.getName() + " arrived");
            if(arrivalTimeIndex < getProcessCount() - 1){
                arrivalTimeIndex++;
            }
        }
    }

    private void processCompletionTime(int time){
        for (process process : processes){
            if (process.getCompletionTime() == time && time != 0){
                System.out.println("Time " + (time-1) + ": " + process.getName() + " finished");
            }
        }
    }

    private void calculateIdleStartTime(){
        for(process process: processes){
            idleStartTime += process.getBurstTime();
        }
    }

    private void processIdleAndCompletionTime(int time){
        if(time < getTimeBlock() && time >= idleStartTime){
            System.out.println("Time "  + time + ":" + " Idle");
        }else if (time == getTimeBlock()){
            System.out.println("Finished at time " + time + "\n");
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
