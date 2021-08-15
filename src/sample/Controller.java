package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {
    @FXML
    DatePicker datePick;
    @FXML
    TextArea inputField;
    @FXML
    TextField selectedDir;
    @FXML
    Button dirSelectionBtn;
    @FXML
    Button submitBtn;
}
