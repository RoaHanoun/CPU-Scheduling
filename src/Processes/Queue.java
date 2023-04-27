package Processes;

import java.util.ArrayList;

public class Queue {
    private int front;
    private int rear;
    private int count;
    private ArrayList<process> processes;
    private int size;

    public Queue(int size)
    {
        this.size = size;
        this.processes = new ArrayList<>();
        this.front = 0;
        this.rear = -1;
        this.count = 0;
    }
    public void addForSRTF(process process)
    {
        process process1 = null;
        int i = 0;
        for(i = 0; i < this.processes.size(); i++)
        {
            if(this.processes.get(i).getRemainingTime() > process.getRemainingTime())
            {
                process1 = this.processes.get(i);
                break;
            }
        }

        if(process1 != null)
            this.processes.add(i, process);
        else
            this.processes.add(process);

        this.count+=1;
        this.rear = (this.rear+1)%this.size;
    }
    public void enqueue(process process)
    {
        this.rear = (this.rear+1)%this.size;
        this.processes.add(process);
        this.count++;
    }
    public process peek()
    {
        if(this.count == 0)
            System.exit(0);
        return this.processes.get(0);
    }
    public process dequeue() {
        if(this.count == 0)
            System.exit(0);
        process process = this.processes.get(0);
        this.processes.remove(0);
        this.front = (this.front+1)%this.size;
        this.count--;
        return process;
    }

    public boolean isEmpty()
    {
        return this.count == 0;
    }
}
