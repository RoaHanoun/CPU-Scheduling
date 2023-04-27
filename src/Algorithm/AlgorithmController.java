package Algorithm;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;


public class AlgorithmController {

    @FXML
    Label algorithmToRun;
    @FXML
    TextArea CPU_Bursts_info;
    @FXML
    TextArea IO_Bursts_info;
    @FXML
    TextArea Results;

    private static AlgorithmController algorithmController;

    public AlgorithmController()
    {
        this.algorithmController = this;
    }
    public static AlgorithmController getInstance()
    {
        return algorithmController;
    }

    public void setTitle(String text)
    {
        algorithmToRun.setText(text);
    }

    public void clear()
    {
        algorithmToRun.setText("Algorithm");
        CPU_Bursts_info.setText("");
        IO_Bursts_info.setText("");
        Results.setText("");
    }

    public void appendResult(String text)
    {
        Results.appendText(text);
    }

    public void appendCPUInfo(String text)
    {
        CPU_Bursts_info.appendText(text);
    }

    public void appendIOInfo(String text)
    {
        IO_Bursts_info.appendText(text);
    }

}
