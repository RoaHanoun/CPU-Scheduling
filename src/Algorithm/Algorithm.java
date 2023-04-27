package Algorithm;

import Processes.Queue;
import Processes.process;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Algorithm {

    public ArrayList<process> processes;
    public int timeQuantum;
    public AtomicBoolean adding = new AtomicBoolean(false);
    public AtomicBoolean addingIO = new AtomicBoolean(false);
    public AtomicBoolean IODone = new AtomicBoolean(false);
    public AtomicBoolean CPUDone = new AtomicBoolean(false);
    public Queue CPUReadyQueue;
    public Queue IOReadyQueue;
    public ArrayList<process> finish;
    public AtomicInteger remainingNoOfProcesses;
    public AtomicInteger time = new AtomicInteger();
    public AtomicInteger finishTime = new AtomicInteger();
    public String cpuInfo = "";
    public String ioInfo = "";
    public String resInfo = "";

    public Algorithm(ArrayList<process> processes)
    {
        this.processes = processes;
        this.CPUReadyQueue = new Queue(this.processes.size());
        this.IOReadyQueue = new Queue(this.processes.size());
        this.finish = new ArrayList<>(this.processes.size());
        this.remainingNoOfProcesses = new AtomicInteger(this.processes.size());
    }

    public Algorithm(ArrayList<process> processes, int timeQuantum) throws InterruptedException {
        this.processes = processes;
        this.timeQuantum = timeQuantum;
        this.CPUReadyQueue = new Queue(this.processes.size());
        this.IOReadyQueue = new Queue(this.processes.size());
        this.finish = new ArrayList<>(this.processes.size());
        this.remainingNoOfProcesses = new AtomicInteger(this.processes.size());
    }

}
