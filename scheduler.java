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
    List<CustomProcess> processes = new ArrayList<>();
    private int processCount;
    private int timeBlock;
    private String schedulerType;
    private int timeQuantum;

    private int arrivalTimeIndex = 0;
    private int sjfProcessCounter = 0;
    private int change = 0;
    private int nextAvailableTime;
    private boolean sjfOnLastProcess = false;
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
                addProcess(new CustomProcess(processName,processArrivalTime,processBurstTime));

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
        for(int time = 0; time <= getTimeBlock(); time++){
            processFCFSCompletionTime(time);
            processArrivalTime(time);

            if(processRunning()){
                //Do not schedule a new CustomProcess
            }else{
                //Attempt to schedule a new CustomProcess
                fcfsScheduleNewProcess(time);
            }
            processIdleTime(time);
        }
    }

    private void executeSJF(){
        System.out.println("Executing SJF");
        CustomProcess currentProcess = processes.get(0);
        int j;
        for(int time = 0; time <= getTimeBlock(); time++){
            sortByArrivalTime();
            processFCFSCompletionTime(time);
            if(currentProcess.getTimeLeft() == 0){
                currentProcess.setRunning(false);
                currentProcess.setComplete(true);
            }
            processArrivalTime(time);
            sortByBurstTime();
            if(sjfProcessCounter == processCount){
                for(j = 0;j <= timeBlock - time; j++) {
                    processIdleTime(time+j);
                }
                break;
            }
            //System.out.println(currentProcess.getBurstTime());
            for(j = 0; j < processCount; j++){
                //System.out.println(j +") " + "Process in list " + processes.get(j).getName());
                if(processes.get(j).getTimeLeft() > 0 && processes.get(j).isArrived() == true){
                    if(processes.get(j).getName() == currentProcess.getName() && currentProcess.isRunning() == true)
                        break;
                    else {
                        currentProcess.setRunning(false);
                        currentProcess = processes.get(j);
                        currentProcess.setRunning(true);
                        //change = 1;
                        System.out.println("Time " + time + ": " + currentProcess.getName() + " selected (burst " + currentProcess.getTimeLeft() + ")");
                        break;
                    }
                }
            }
            //System.out.println(currentProcess.getName()+ " is working at time " + time);
            currentProcess.setCompletionTime(time + currentProcess.getTimeLeft());
            if(currentProcess.isRunning())
                currentProcess.setTimeLeft(currentProcess.getTimeLeft()-1);
            //System.out.println(currentProcess.getName()+ " has " + currentProcess.getTimeLeft() + " time left");
            processIdleTime(time);

            if(currentProcess.getTimeLeft() == 0){
                sjfProcessCounter++;
            }
        }
    }

    private void executeRR(){

    }


    private boolean processRunning(){
        boolean isProcessRunning = false;

        for(CustomProcess process : processes){
            if(process.isRunning()){
                isProcessRunning = true;
            }
        }
        return isProcessRunning;
    }

    private void processArrivalTime(int time){
        for(CustomProcess process : processes){
            if(process.getArrivalTime() == time){
                process.setArrived(true);
                System.out.println("Time " + time + ": " + process.getName() + " arrived");
            }
        }
    }

    private void SJFprocessArrivalTime(int time){
        CustomProcess nextProcess = processes.get(arrivalTimeIndex);
        if(time == nextProcess.getArrivalTime()){
            System.out.println("Time " + time + ": " + nextProcess.getName() + " arrived");
            nextProcess.setReady(true);
            if(arrivalTimeIndex < getProcessCount() - 1){
                arrivalTimeIndex++;
            }
        }
    }

    private void fcfsScheduleNewProcess(int time){
        for(CustomProcess process : processes){
            if(process.isArrived() && !process.isComplete()){
                process.setRunning(true);
                process.setCompletionTime(time + process.getBurstTime());
                System.out.println("Time " + time + ": " + process.getName() + " selected (burst " + process.getBurstTime()+")");
                break;
            }
        }
    }

    private void processFCFSCompletionTime(int time){
        for (CustomProcess process : processes){
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

    private void processCompletionTime(int time){
        for (CustomProcess process : processes){
            if (process.getCompletionTime() == time && time != 0){
                System.out.println("Time " + (time) + ": " + process.getName() + " finished");
                process.setComplete(true);
                process.setRunning(false);
            }
        }
    }

    private void calculateIdleStartTime(){
        for(CustomProcess process: processes){
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
        for(CustomProcess process : processes){
            System.out.println(process.getName() + " wait " + process.waitTime() + " turnaround " + process.getTurnaroundTime());
        }
    }

    private void sortByArrivalTime(){
        Collections.sort(processes, new ProcessArrivalComparer());
    }

    private void sortByBurstTime() {
        Collections.sort(processes, new ProcessTimeLeftComparer());
    }


    public void addProcess(CustomProcess newProcess){
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
