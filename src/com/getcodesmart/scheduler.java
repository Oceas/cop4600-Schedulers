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
        switch(getSchedulerType()){
            case "fcfs":
                executeFCFS();
                break;
            case "sjf":
                executeSJF();
                break;
            case "rr":
                executeRR();
                break;
        }
    }

    private void executeFCFS(){
        System.out.println("Executing FCFS");

    }

    private void executeSJF(){

    }

    private void executeRR(){

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
