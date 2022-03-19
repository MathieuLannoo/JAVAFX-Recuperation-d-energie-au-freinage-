package com.example.bluetoothfx;

import com.fazecast.jSerialComm.SerialPort;
import eu.hansolo.medusa.Section;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ChoiceBox;
import eu.hansolo.medusa.Gauge;
import javafx.scene.paint.Color;


public class HelloController{


    bluetooth connection = new bluetooth();

    @FXML
    private Gauge battery_level;

    @FXML
    public Gauge connection_status_gauge;

    @FXML
    private ChoiceBox<SerialPort> bluetooth_portlist;

    @FXML
    private Gauge gauge_test;


    @FXML
    private ToggleButton toggle_chambre;

    @FXML
    private ToggleButton toggle_salon;

    public void initialize() {
        battery_level.setAnimated(true);
        connection_status_gauge.setAnimated(true);

        battery_level.setBarColor(Color.GREEN);


        bluetooth_portlist.getItems().addAll(connection.ports);
    }

    public void ConnectButton() {
        SerialPort selected_value =  bluetooth_portlist.getValue();
        connection.setPort(selected_value);
        if(connection.Connection_State == 1)
            connection_status_gauge.setValue(100);

    }

    @FXML
    public void OpenButton() {
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

        System.out.println("vive les animations :)");
        gauge_test.setValue(90);

        battery_level.setValue(99);


    }

    public void choiceport_validation() {
        System.out.println("validation du port...");

    }
}