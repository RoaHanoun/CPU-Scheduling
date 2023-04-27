package Processes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileGenerator {
    private String file;
    private static int index;
    private int numberOfProcesses;
    private int numberOfBursts;

    public FileGenerator()
    {
        this.file = "CPU_Schedule"+ index +".txt";
        index++;
        this.numberOfProcesses = (int) (Math.random() * 100 + 20);
        this.numberOfBursts = (int) (Math.random() * 20 + 5);
        this.generateNewFile();
    }

    private void generateNewFile()
    {
        try {
            FileWriter fileWriter = new FileWriter(this.file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print("Delay    PID");
            for(int i = 0; i < this.numberOfBursts; i++)
            {
                if(i%2 == 0)
                    printWriter.print("    CPUBurst");
                else
                    printWriter.print("    IOBurst");
            }
            printWriter.print(" \n");
            for(int i = 0; i < this.numberOfProcesses; i++) {
                int delay = (int) (Math.random() * 5);
                printWriter.print(delay + "          ");
                printWriter.print(i);
                for (int j = 0; j < this.numberOfBursts; j++) {
                    int burst = (int) (Math.random() * 118 + 2);
                    printWriter.printf("%11d", burst);
                }
                printWriter.println(" ");
            }
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
