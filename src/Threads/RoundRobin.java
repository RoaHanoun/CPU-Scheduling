package Threads;

import Algorithm.Algorithm;
import Algorithm.AlgorithmController;
import Processes.Queue;
import Processes.process;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobin extends Algorithm {

    Thread addToReadyQueue = new Thread(() ->
    {
        time.set(0);
        for (process process : processes) {
            if (process.getDelay() != 0) {
                int delay = process.getDelay();
                while (delay != 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    delay--;
                    time.getAndIncrement();
                }
            }
            process.setArrivalTime(time.get());
            while (adding.get()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            adding.set(true);
            CPUReadyQueue.enqueue(process);
            adding.set(false);
        }
    });

    Thread RoundRobin_CPU_Schedule = new Thread(() ->
    {
        process currentProcess;
        while (remainingNoOfProcesses.get() != 0) {
            if (CPUReadyQueue.isEmpty()) {
                System.out.print(" Waiting CPU ");
                time.getAndIncrement();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                currentProcess = CPUReadyQueue.dequeue();
                cpuInfo = cpuInfo + " CPU Context Switch.\n";
                System.out.println(" CPU Context Switch. \n");
                time.getAndIncrement();
                if (!currentProcess.isStarted()) {
                    currentProcess.setStarted(true);
                    currentProcess.setStartTime(time.get());
                }
                int counter = 0;
                while (counter < timeQuantum && currentProcess.getRemainingTime() != 0) {
                    currentProcess.setRemainingTime(currentProcess.getRemainingTime()-1);
                    cpuInfo = cpuInfo + " Pid("+currentProcess.getPid()+") ";
                    System.out.print(" Pid("+currentProcess.getPid()+") ");
                    time.getAndIncrement();
                    currentProcess.setCPUBursts(currentProcess.getCPUBursts()+1);
                    counter++;
                }
                if (currentProcess.getRemainingTime() == 0) {
                    currentProcess.getBursts().remove(0);
                    if (currentProcess.getBursts().isEmpty()) {
                        currentProcess.setFinishTime(time.get());
                        remainingNoOfProcesses.getAndDecrement();
                        finish.add(currentProcess);
                    } else {
                        currentProcess.setRemainingTime(currentProcess.getBursts().get(0));
                        while (addingIO.get()) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        addingIO.set(true);
                        IOReadyQueue.enqueue(currentProcess);
                        addingIO.set(false);
                    }
                } else {
                    while (adding.get()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    adding.set(true);
                    CPUReadyQueue.enqueue(currentProcess);
                    adding.set(false);
                }

            }
        }
        if (remainingNoOfProcesses.get() == 0) {
            CPUDone.set(true);
            while(!IODone.get() && !CPUDone.get())
            { }
            cpuInfo = cpuInfo + "\nCPU is done processing\n";
            System.out.print("\nCPU is done processing\n");
            finishTime.set(time.get());
        }
});

    Thread RoundRobin_IO_Schedule = new Thread(() ->
    {
        process currentProcess;
        while (remainingNoOfProcesses.get() != 0) {
            if (IOReadyQueue.isEmpty()) {
                ioInfo = ioInfo + " Waiting IO ";
                System.out.print(" Waiting IO ");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                currentProcess = IOReadyQueue.dequeue();
                ioInfo = ioInfo + " IO Context Switch.\n";
                System.out.print(" IO Context Switch.\n");
                int counter = 0;
                while (counter < timeQuantum && currentProcess.getRemainingTime() != 0) {
                    currentProcess.setRemainingTime(currentProcess.getRemainingTime()-1);
                    ioInfo = ioInfo + " PID("+ currentProcess.getPid()+") ";
                    System.out.print(" PID("+ currentProcess.getPid()+") ");
                    counter++;
                }
                if (currentProcess.getRemainingTime() == 0) {
                    currentProcess.getBursts().remove(0);
                    if (currentProcess.getBursts().isEmpty()) {
                        currentProcess.setFinishTime(time.get());
                        remainingNoOfProcesses.getAndDecrement();
                        finish.add(currentProcess);
                    } else {
                        currentProcess.setRemainingTime(currentProcess.getBursts().get(0));
                        while (adding.get()) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        adding.set(true);
                        CPUReadyQueue.enqueue(currentProcess);
                        adding.set(false);
                    }
                } else {
                    while (addingIO.get()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    addingIO.set(true);
                    IOReadyQueue.enqueue(currentProcess);
                    addingIO.set(false);
                }
            }
        }
        if (remainingNoOfProcesses.get() == 0) {
            IODone.set(true);
            while(!IODone.get() && !CPUDone.get())
            { }
            ioInfo = ioInfo + "\nIO is done processing\n";
            System.out.print("\nIO is done processing\n");
            finishTime.set(time.get());
        }
    });

    Thread FinishQueue = new Thread(() -> {
        double utilization = 0.0;
        double avgResponseTime = 0.0;
        double throughput = 0.0;
        int index = 0;
        int allCPUBursts = 0;
        int allResponseTime = 0;
        while (index < processes.size()) {
            if (finish.isEmpty() || index >= finish.size()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                process p = finish.get(index++);
                p.setTurnAround(p.getFinishTime()-p.getArrivalTime());
                p.setResponseTime(p.getStartTime() - p.getArrivalTime());
                p.setWaitingTime(p.getTurnAround() - p.getCPUBursts());
                allResponseTime += p.getResponseTime();
                allCPUBursts += p.getCPUBursts();
            }
        }
        while(!IODone.get() && !CPUDone.get())
        {

        }
        int numberOfProcesses = processes.size();
        int TIME = Integer.parseInt(finishTime.toString());
        utilization = (double) allCPUBursts / TIME * 100;
        avgResponseTime = (double) allResponseTime / numberOfProcesses;
        throughput = (double) numberOfProcesses / TIME;

        resInfo = resInfo + "Time: "+TIME+"\n";
        resInfo = resInfo + "Utilization: "+utilization+"\n";
        resInfo = resInfo + "Average Response Time: "+avgResponseTime+"\n";
        resInfo = resInfo + "Throughput: "+throughput+"\n";

        AlgorithmController.getInstance().appendCPUInfo(cpuInfo);
        AlgorithmController.getInstance().appendIOInfo(ioInfo);
        AlgorithmController.getInstance().appendResult(resInfo);
        
    });

    public RoundRobin(ArrayList<process> processes, int timeQuantum) throws InterruptedException {
        super(processes, timeQuantum);
        AlgorithmController.getInstance().setTitle("Round Robin Simulation");
        addToReadyQueue.start();
        RoundRobin_CPU_Schedule.start();
        RoundRobin_IO_Schedule.start();
        FinishQueue.start();
    }
}
