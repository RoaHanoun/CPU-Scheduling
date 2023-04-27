package Main;

import Algorithm.Algorithm;
import Processes.FileGenerator;
import Processes.process;
import Threads.RoundRobin;
import Threads.SRTF;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {
    @FXML
    Button GenerateProcess;
    @FXML
    Button chooseFile;
    @FXML
    ComboBox algorithmChoice;
    @FXML
    TextField timeQuantum;
    @FXML
    Button Go;

    private ArrayList<process> processes = new ArrayList<>();
    private int timeQ;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeQuantum.setVisible(false);
        ArrayList<String> algorithms = new ArrayList<>();
        algorithms.add("Round Robin");
        algorithms.add("SRTF");
        algorithmChoice.getItems().addAll(algorithms);
        algorithmChoice.setOnAction(e -> {
            if(algorithmChoice.getSelectionModel().getSelectedItem().equals("Round Robin"))
            {
                timeQuantum.setVisible(true);
                return;
            }
            timeQuantum.setVisible(false);
        });
    }

    public void generateNewFile()
    {
        try
        {
            new FileGenerator();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void chooseFileToRead() throws FileNotFoundException {
        int time = 0, i = 0;
        this.processes = new ArrayList<>();
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        File path = new File(System.getProperty("user.dir"));
        fileChooser.setInitialDirectory(path);
        File file = fileChooser.showOpenDialog(stage);
        if(file == null)
            return;
        Scanner scanner = new Scanner(file);
        scanner.nextLine();
        while(scanner.hasNextLine())
        {
            ArrayList<Integer> bursts = new ArrayList<>();
            String line = scanner.nextLine();
            String [] process = line.split("\\s+");
            process process1 = new process(Integer.parseInt(process[1]));
            process1.setDelay(Integer.parseInt(process[0]));
            for(int j = 2; j < process.length; j++)
            {
                bursts.add(Integer.parseInt(process[j]));
            }
            time += process1.getDelay();
            process1.setArrivalTime(process1.getArrivalTime()+time);
            process1.setBursts(bursts);
            process1.setRemainingTime(bursts.get(0));
            this.processes.add(process1);
        }
    }

    public void startScheduling() throws InterruptedException {
        if(this.processes.isEmpty() || this.processes == null)
            return;
        if(algorithmChoice.getSelectionModel().getSelectedItem() == null)
            return;
        if(algorithmChoice.getSelectionModel().getSelectedItem().equals("Round Robin"))
        {
            if(timeQuantum.getText() == "" || timeQuantum.getText() == null)
            {
                timeQuantum.setText("4");
            }
            try{
                timeQ = Integer.parseInt(timeQuantum.getText());
            } catch(Exception ex)
            {
                ex.printStackTrace();
                return;
            }

            Stage stage = new Stage();
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../Resources/algorithm_view.fxml"));
                stage.setTitle("Round Robin");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

            new RoundRobin(this.processes, this.timeQ);
        }
        else if(algorithmChoice.getSelectionModel().getSelectedItem().equals("SRTF"))
        {
            Stage stage = new Stage();
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../Resources/algorithm_view.fxml"));
                stage.setTitle("SRTF");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new SRTF(this.processes);
        }
    }

    public void exit()
    {
        System.exit(1);
    }

}

