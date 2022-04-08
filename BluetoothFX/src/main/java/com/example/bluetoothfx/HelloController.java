package com.example.bluetoothfx;

import com.fazecast.jSerialComm.SerialPort;
import eu.hansolo.medusa.Section;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import eu.hansolo.medusa.Gauge;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


public class HelloController {
    gamepad Xbox_gamepad = new gamepad();
    SerialPort selected_value;
    bluetooth connection = new bluetooth();

    @FXML
    public ScrollBar scrollbar_turn;
    @FXML
    public ScrollBar scrollbar_forward;
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



    Thread ConnectionPortTask = new Thread() {
        public void run() {
            connection.setPort(selected_value);
            if (connection.Connection_State == 1)
                connection_status_gauge.setValue(100);
            gauge_test.setValue(90);
            battery_level.setValue(99);
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gauge_test.setValue(0);
            battery_level.setValue(0);
        }
    };

    Thread start_gamepad = new Thread() {
        public void run() {
            if(Xbox_gamepad.ConnectedGamepad != null){
                while(true){
                    Xbox_gamepad.RefreshControllerData();
                    scrollbar_forward.setValue(Xbox_gamepad.acceleration_gamepad);
                    scrollbar_turn.setValue(Xbox_gamepad.direction_gamepad);

                    if( !Xbox_gamepad.ConnectedGamepad.poll() ){
                        scrollbar_forward.setValue(50);
                        System.out.println("manette deconnectée");
                        break;
                    }
                }
            }
            else
                System.out.println("Pas de manette connectée!");
        }

    };
    public void initialize() {
        start_gamepad.start();
        battery_level.setAnimated(true);
        connection_status_gauge.setAnimated(true);
        battery_level.setBarColor(Color.GREEN);
        bluetooth_portlist.getItems().addAll(connection.ports);
        scrollbar_forward.valueProperty().addListener(new ChangeListener<Number>() {
            int old_value = 100;
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                int accelerate = 100 - t1.intValue();
                if (accelerate != old_value){
                    String cmd = "a|"+accelerate + "|";
                    System.out.println(cmd);
                    connection.send_command(cmd);
                    old_value = accelerate;
                }
            }
        });

        scrollbar_turn.valueProperty().addListener(new ChangeListener<Number>() {
            int old_value = 100;
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                int turn = t1.intValue();
                if (turn<= old_value-3 || turn >=old_value+3){
                    String cmd = "t|" + turn + "|";
                    System.out.println(cmd);
                    connection.send_command(cmd);
                    old_value = turn;
                }
            }
        });
    }

    public void ConnectButton() {
        selected_value =  bluetooth_portlist.getValue();
        ConnectionPortTask.start();
    }

    public void mouse_released() {
        scrollbar_forward.setValue(50);
        scrollbar_turn.setValue(50);
    }
}