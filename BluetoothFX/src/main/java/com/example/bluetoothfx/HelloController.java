package com.example.bluetoothfx;

//import eu.hansolo.medusa.Gauge;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

public class HelloController {



    bluetooth connection = new bluetooth();
    //@FXML public Gauge kmh;
    @FXML public Button portail_bouton;
    @FXML public Label welcomeText;
    @FXML public ToggleButton toggle_salon;
    @FXML public ToggleButton toggle_chambre;

    public void ConnectButton() {

        connection.start();
    }

    @FXML
    public void OpenButton() {
        welcomeText.setText("");
        connection.send_command("4");
    }
    @FXML
    public void Toggle_Button_Salon() {
        if (toggle_salon.isSelected()){
            connection.send_command("1");
        }
        else{
            connection.send_command("0");
        }
    }
    @FXML
    public void toogle_button_Chambre() {
        if (toggle_chambre.isSelected()){
            connection.send_command("2");
        }
        else{
            connection.send_command("3");
        }
    }
    /*@FXML
    public void test_def() {
        kmh.setAnimated(true);
        kmh.setValue(90.0);
    }*/
}