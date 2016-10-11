/*
    Group Members: Scott Anderson, Travis Le, and Luis Velez
    COP 4600 Assignment 1 Schedulers
    October 2, 2016
 */
package com.getcodesmart;

import java.io.FileWriter;
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
    private boolean idling = false;
    int offset = 0;
    private int sjfProcessCounter = 0;

    FileWriter outputStream = null;

    public static void main(String[] args) throws IOException {
        scheduler scheduler = new scheduler();
        scheduler.initialize();
    }

    public void initialize() throws IOException {
        setFileName("processes.in");
        setFilePath(getCurrentDirectory() + getFileName());

        outputStream = new FileWriter("processes.out");

        processInputFile(getFilePath());



        for(int i = 0; i < getProcessCount(); i++){
            CustomProcess currentProcess = processes.get(i);
            currentProcess.setLastRan(0);
            currentProcess.setIndexInList(i);
        }
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
                addProcess(new CustomProcess(processName,processArrivalTime,processBurstTime));
                break;
            case "end":
                break;
        }
    }

    private void executeScheduler() throws IOException {
        outputStream.write(getProcessCount() + " processes" + "\n");
        switch(getSchedulerType()){
            case "fcfs":
                sortByArrivalTime();
                outputStream.write("Using First-Come First-Served\n\n");
                executeFCFS();
                break;
            case "sjf":
                outputStream.write("Executing SJF" + "\n\n");
                executeSJF();
                break;
            case "rr":
                outputStream.write("Using Round-Robin" + "\n");
                outputStream.write("Quantum " + getTimeQuantum() + "\n\n");
                executeRR();
                break;
        }
        outputStream.write("Finished at time " + getTimeBlock() + "\n\n");
        printWaitAndTurnTimes();

        if (outputStream != null) {
            outputStream.close();
        }
    }

    private void executeFCFS() throws IOException {
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

    private void executeSJF() throws IOException {
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
            for(j = 0; j < processCount; j++){
                if(processes.get(j).getTimeLeft() > 0 && processes.get(j).isArrived() == true){
                    if(processes.get(j).getName() == currentProcess.getName() && currentProcess.isRunning() == true)
                        break;
                    else {
                        currentProcess.setRunning(false);
                        currentProcess = processes.get(j);
                        currentProcess.setRunning(true);
                        //change = 1;
                        outputStream.write("Time " + time + ": " + currentProcess.getName() + " selected (burst " + currentProcess.getTimeLeft() + ")" + "\n");
                        break;
                    }
                }
            }
            currentProcess.setCompletionTime(time + currentProcess.getTimeLeft());
            if(currentProcess.isRunning())
                currentProcess.setTimeLeft(currentProcess.getTimeLeft()-1);
            processIdleTime(time);

            if(currentProcess.getTimeLeft() == 0){
                sjfProcessCounter++;
            }
        }

    }

    private void sortByBurstTime() {
        Collections.sort(processes, new ProcessTimeLeftComparer());
    }

    private void executeRR() throws IOException {
        for(int time = 0; time < getTimeBlock(); time++){
            //Checks to see if processes have been completed and if so, outputs that they are
            processRRCompleteOrCompletionTime(time);

            //Checks if processes have arrived at the current time and if so, outputs that they have
            processArrivalTime(time);

            //Schedule a new process if a process is not currently within a time quantum or if the system is idle
            if((time % getTimeQuantum() == (0 + offset)) || isIdling()) {
                setIdling(false);
                rrScheduleNewProcess(time);
            }
        }
    }

    private void fcfsScheduleNewProcess(int time) throws IOException {
        for(CustomProcess process : processes){
            if(process.isArrived() && !process.isComplete()){
                process.setRunning(true);
                process.setCompletionTime(time + process.getBurstTime());
                outputStream.write("Time " + time + ": " + process.getName() + " selected (burst " + process.getBurstTime() + ")" + "\n");
                break;
            }
        }
    }

    private void processRRCompleteOrCompletionTime(int time) throws IOException {
        int processesComplete = 0;

        //Marks all processes that have been completed and the time they finished and outputs so
        for (CustomProcess process : processes){
            if (process.getBurstTimeLeft() == 0 && !process.isComplete()){
                process.setComplete(true);
                process.setCompletionTime(time);
                outputStream.write("Time " + time + ": " + process.getName() + " finished" + "\n");
            }

            //Counts the total number of currently completed processes
            if(process.isComplete()){
                processesComplete++;
            }
        }

        //Sets the CPU to be idle if there are no more processes to handle
        if(processesComplete == processes.size()){
            setIdling(true);
        }
    }

    private void rrScheduleNewProcess(int time) throws IOException {
        int waitedLongestIndex = 999999;    //arbitrary large integer
        int lastRan = time;

        //Finds the process that, if it has arrived and is not yet completed, hasn't been run in the longest time and sets it to be next to run
        for(CustomProcess process : processes) {
            if (process.isArrived() && !process.isComplete()) {
                if (process.getLastRan() <= lastRan){
                    lastRan = process.getLastRan();
                    waitedLongestIndex = process.getIndexInList();
                }
            }
        }

        //Runs the next process for the duration of the time quantum
        if (waitedLongestIndex != 999999){
            outputStream.write("Time " + time + ": " + processes.get(waitedLongestIndex).getName() + " selected (burst " + processes.get(waitedLongestIndex).getBurstTimeLeft()+")" + "\n");

            processes.get(waitedLongestIndex).setLastRan(time);
            if (processes.get(waitedLongestIndex).getBurstTimeLeft() < getTimeQuantum()){   //Handles cases where the process will finish before its time quantum is up
                offset = processes.get(waitedLongestIndex).getBurstTimeLeft();
                processes.get(waitedLongestIndex).setBurstTimeLeft(0);
            }
            else{   //Decrements a process' remaining burst time by the time quantum
                processes.get(waitedLongestIndex).setBurstTimeLeft(processes.get(waitedLongestIndex).getBurstTimeLeft() - getTimeQuantum());
            }
        }else{  //If there currently aren't any processes that've arrived to be scheduled, sets the CPU to be idle and outputs so
            setIdling(true);
            outputStream.write("Time " + time + ": " + "Idle" + "\n");
            offset = 0;
        }
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

    private void processArrivalTime(int time) throws IOException {
        for(CustomProcess process : processes){
            if(process.getArrivalTime() == time){
                process.setArrived(true);
                outputStream.write("Time " + time + ": " + process.getName() + " arrived" + "\n");
            }
        }
    }

    private void processFCFSCompletionTime(int time) throws IOException {
        for (CustomProcess process : processes){
            if (process.isRunning() && process.getCompletionTime() == time){
                process.setRunning(false);
                process.setComplete(true);
                outputStream.write("Time " + (time) + ": " + process.getName() + " finished" + "\n");
            }
        }
    }

    private void processIdleTime(int time) throws IOException {

        boolean schedulerIdling = (!processRunning()) && (time != getTimeBlock());

        if(schedulerIdling){
            outputStream.write("Time "  + time + ":" + " Idle" + "\n");
        }
    }

    private void printWaitAndTurnTimes() throws IOException {
        for(CustomProcess process : processes){
            outputStream.write(process.getName() + " wait " + process.waitTime() + " turnaround " + process.getTurnaroundTime() + "\n");
        }
    }

    private void sortByArrivalTime(){
            Collections.sort(processes, new ProcessComparer());
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

    public int getTimeBlock() { return timeBlock; }

    public void setTimeBlock(int timeBlock) {
        this.timeBlock = timeBlock;
    }

    public String getSchedulerType() {
        return schedulerType;
    }

    public void setSchedulerType(String schedulerType) {
        this.schedulerType = schedulerType;
    }

    public int getTimeQuantum() { return timeQuantum; }

    public void setTimeQuantum(int timeQuantum) { this.timeQuantum = timeQuantum; }

    public boolean isIdling() { return idling; }

    public void setIdling(boolean idling) { this.idling = idling; }
}
