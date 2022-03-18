package com.example.bluetoothfx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import eu.hansolo.medusa.Gauge;
public class HelloController {



    bluetooth connection = new bluetooth();

    @FXML public Button test_button;
    @FXML public Label welcomeText;
    @FXML public ToggleButton toggle_salon;
    @FXML public ToggleButton toggle_chambre;
    @FXML public Gauge gauge_test;
    @FXML public Gauge battery_level;


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

    public void onpress_test() {
        System.out.println("test");
        gauge_test.setValue(90);
        battery_level.setAnimated(true);
        battery_level.setValue(65);


    }
}