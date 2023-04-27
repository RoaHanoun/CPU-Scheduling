package Processes;

import java.util.ArrayList;

public class process {
    private int pid;
    private int delay;
    private int remainingTime;
    private int arrivalTime;
    private int startTime;
    private int turnAround;
    private int finishTime;
    private int waitingTime;
    private int CPUBursts;
    private int responseTime;
    private boolean started;
    private ArrayList<Integer> Bursts = new ArrayList<>();

    public process(int pid)
    {
        this.started = false;
        this.pid = pid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getTurnAround() {
        return turnAround;
    }

    public void setTurnAround(int turnAround) {
        this.turnAround = turnAround;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getCPUBursts() {
        return CPUBursts;
    }

    public void setCPUBursts(int CPUBursts) {
        this.CPUBursts = CPUBursts;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public ArrayList<Integer> getBursts() {
        return Bursts;
    }

    public void setBursts(ArrayList<Integer> bursts) {
        Bursts = bursts;
    }
}
