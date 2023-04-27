package Threads;

import Algorithm.Algorithm;
import Algorithm.AlgorithmController;
import Processes.process;

import java.util.ArrayList;

public class SRTF extends Algorithm {
    public SRTF(ArrayList<process> processes) {
        super(processes);
        AlgorithmController.getInstance().setTitle("SRTF Simulation");
        addToReadyQueue.start();
        CPU_Schedule_SRTF.start();
        IO_Schedule_SRTF.start();
        FinishQueue.start();
    }

    Thread addToReadyQueue = new Thread(() ->
    {
       time.set(0);
       for(process process: this.processes)
       {
           if(process.getDelay() != 0)
           {
               int delay = process.getDelay();
               while(delay != 0)
               {
                   try {
                       Thread.sleep(100);
                   } catch (Exception ex)
                   {
                       ex.printStackTrace();
                   }
                   delay--;
                   time.getAndIncrement();
               }
           }
           process.setArrivalTime(time.get());
           while(adding.get())
           {
               try
               {
                   Thread.sleep(100);
               }
               catch(Exception ex)
               {
                   ex.printStackTrace();
               }
           }
           adding.set(true);
           CPUReadyQueue.enqueue(process);
           adding.set(false);
       }
    });

    Thread CPU_Schedule_SRTF = new Thread(() ->
    {
        process currentProcess;
        while (remainingNoOfProcesses.get() != 0) {
            if (CPUReadyQueue.isEmpty()) {

                time.getAndIncrement();
                System.out.print(" Waiting CPU ");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                currentProcess = CPUReadyQueue.dequeue();
                System.out.println(" CPU context switch.\n ");
                cpuInfo = cpuInfo + " CPU Context Switch.\n";
                time.getAndIncrement();
                if (!currentProcess.isStarted()) {
                    currentProcess.setStarted(true);
                    currentProcess.setStartTime(time.get());
                }
                if (!CPUReadyQueue.isEmpty()) {
                    while (currentProcess.getRemainingTime() <= CPUReadyQueue.peek().getRemainingTime() && currentProcess.getRemainingTime() != 0) {
                        currentProcess.setRemainingTime(currentProcess.getRemainingTime()-1);

                        System.out.print(" CPU " + currentProcess.getPid() + " ");
                        cpuInfo = cpuInfo + " Pid("+ currentProcess.getPid()+") ";
                        time.getAndIncrement();
                        currentProcess.setCPUBursts(currentProcess.getCPUBursts()+1);
                    }
                    if (currentProcess.getRemainingTime() > CPUReadyQueue.peek().getRemainingTime() && currentProcess.getRemainingTime() != 0) {
                        while (adding.get()) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        adding.set(true);
                        CPUReadyQueue.addForSRTF(currentProcess);
                        adding.set(false);
                    } else if (currentProcess.getRemainingTime() == 0) {
                        currentProcess.getBursts().remove(0);
                        if (!currentProcess.getBursts().isEmpty()) {
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


                        } else if (currentProcess.getBursts().isEmpty()) {
                            currentProcess.setFinishTime(time.get());
                            remainingNoOfProcesses.getAndDecrement();
                            finish.add(currentProcess);
                        }
                    }
                } else {
                    while (currentProcess.getRemainingTime() != 0) {
                        currentProcess.setRemainingTime(currentProcess.getRemainingTime()-1);

                        System.out.print(" CPU " + currentProcess.getPid() + " ");
                        cpuInfo = cpuInfo + " Pid("+ currentProcess.getPid()+") ";
                        time.getAndIncrement();
                        currentProcess.setCPUBursts(currentProcess.getCPUBursts()+1);
                    }
                    currentProcess.getBursts().remove(0);
                    if (!currentProcess.getBursts().isEmpty()) {
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


                    } else if (currentProcess.getBursts().isEmpty()) {
                        currentProcess.setFinishTime(time.get());
                        remainingNoOfProcesses.getAndDecrement();
                        finish.add(currentProcess);
                    }

                }
            }
        }
        if (remainingNoOfProcesses.get() == 0) {
            CPUDone.set(true);
            System.out.println("CPU finished all the processes");
            if (IODone.get() && CPUDone.get()) {
                CPUDone.set(false);
                finishTime.set(time.get());
            }

        }

    });

    Thread IO_Schedule_SRTF = new Thread(() ->
    {
        process currentProcess;
        while (remainingNoOfProcesses.get() != 0) {
            if (IOReadyQueue.isEmpty()) {
                System.out.print(" Waiting IO ");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                currentProcess = IOReadyQueue.dequeue();
                System.out.println(" IO Context Switch.\n ");
                ioInfo = ioInfo + " IO Context Switch. \n";
                if (!IOReadyQueue.isEmpty()) {
                    while (currentProcess.getRemainingTime() <= IOReadyQueue.peek().getRemainingTime() && currentProcess.getRemainingTime() != 0) {
                        currentProcess.setRemainingTime(currentProcess.getRemainingTime()-1);
                        System.out.print(" IO " + currentProcess.getPid() + " ");
                        ioInfo = ioInfo + " PID("+currentProcess.getPid()+" ";

                    }
                    if (currentProcess.getRemainingTime() > IOReadyQueue.peek().getRemainingTime() && currentProcess.getRemainingTime() != 0) {
                        IOReadyQueue.enqueue(currentProcess);
                    } else if (currentProcess.getRemainingTime() == 0) {
                        currentProcess.getBursts().remove(0);
                        if (!currentProcess.getBursts().isEmpty()) {
                            currentProcess.setRemainingTime(currentProcess.getBursts().get(0));
                            CPUReadyQueue.addForSRTF(currentProcess);
                        } else if (currentProcess.getBursts().isEmpty()) {
                            currentProcess.setFinishTime(time.get());
                            remainingNoOfProcesses.getAndDecrement();
                            finish.add(currentProcess);
                        }
                    }
                } else {
                    while (currentProcess.getRemainingTime() != 0) {
                        currentProcess.setRemainingTime(currentProcess.getRemainingTime()-1);
                        System.out.print(" IO " + currentProcess.getPid() + " ");
                        ioInfo = ioInfo + " PID("+currentProcess.getPid()+" ";
                        time.getAndIncrement();
                    }
                    currentProcess.getBursts().remove(0);
                    if (!currentProcess.getBursts().isEmpty()) {
                        currentProcess.setRemainingTime(currentProcess.getBursts().get(0));
                        CPUReadyQueue.addForSRTF(currentProcess);
                    } else if (currentProcess.getBursts().isEmpty()) {
                        currentProcess.setFinishTime(time.get());
                        remainingNoOfProcesses.getAndDecrement();
                        finish.add(currentProcess);
                    }

                }
            }
        }
        if (remainingNoOfProcesses.get() == 0) {
            IODone.set(true);
            System.out.println("IO finished all the processes");
            if (IODone.get() && CPUDone.get()) {
                IODone.set(false);
                finishTime.set(time.get());
            }
        }
    });

    Thread FinishQueue = new Thread(() -> {
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
                p.setTurnAround(p.getFinishTime() - p.getArrivalTime());
                p.setResponseTime(p.getStartTime() - p.getArrivalTime());
                p.setWaitingTime(p.getTurnAround() - p.getCPUBursts());
                allResponseTime += p.getResponseTime();
                allCPUBursts += p.getCPUBursts();
            }
        }
        int numberOfProcesses = processes.size();
        int TIME = Integer.parseInt(finishTime.toString());
        double utilization = (double) allCPUBursts / TIME * 100;
        double avgResponseTime = (double) allResponseTime / numberOfProcesses;
        double throughput = (double) numberOfProcesses / TIME;

        resInfo = resInfo + "Finish time is: "+TIME+"\n";
        resInfo = resInfo + "Utilization is: "+utilization+"\n";
        resInfo = resInfo + "Average response time is: "+avgResponseTime+"\n";
        resInfo = resInfo + "Throughput is: "+throughput+"\n";

        AlgorithmController.getInstance().appendCPUInfo(cpuInfo);
        AlgorithmController.getInstance().appendIOInfo(ioInfo);
        AlgorithmController.getInstance().appendResult(resInfo);
    });


}
