package com.example.bluetoothfx;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import eu.hansolo.medusa.Gauge;
import javafx.scene.paint.Color;
import java.util.Random;

import java.text.SimpleDateFormat;


public class HelloController {
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    gamepad Xbox_gamepad = new gamepad();
    SerialPort selected_value;
    boolean new_tram_reception = false;
    bluetooth connection = new bluetooth();
    StringBuilder tram = new StringBuilder();

    @FXML
    private LineChart<String, Number> test_chart;

    @FXML
    private Label test_label;
    @FXML
    private CategoryAxis x;
    @FXML
    private NumberAxis y;

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
            ConnectionPortTask.stop();
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
                    if (new_tram_reception == true){
                        //test_label.setText(tram.toString());
                        System.out.println(tram);
                        new_tram_reception = false;
                    }
                }
            }
            else
                System.out.println("Pas de manette connectée!");
        }

    };
    public void initialize() {
        test_label.setText("");
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
        //graph settings
        series.setName("conso real time");
        test_chart.getData().add(series);
        test_chart.setAnimated(false);
        test_chart.setCreateSymbols(false); //cache les points
        test_chart.setHorizontalGridLinesVisible(false);
        test_chart.setVerticalGridLinesVisible(false);
        //series.getData().add(new XYChart.Data<>("2", 70));
    }

    public void ConnectButton() {
        selected_value =  bluetooth_portlist.getValue();
        ConnectionPortTask.start();
        selected_value.addDataListener(new SerialPortDataListener() {

            int compteur = 0;
            final int WINDOW_SIZE = 50;
            Random rand = new Random();
            @Override
            public void serialEvent(SerialPortEvent event) {
                int size = event.getSerialPort().bytesAvailable();
                byte[] buffer = new byte[size];
                event.getSerialPort().readBytes(buffer, size);
                for(byte b:buffer){
                    if(b != 0)
                        tram.append((char)b);
                }

                if (tram.indexOf("\n") != -1){
                    compteur++;
                    System.out.print(String.valueOf(compteur)+" -> "+ tram);

                    Platform.runLater(() -> {
                        if (series.getData().size() > WINDOW_SIZE)
                            series.getData().remove(0);
                        series.getData().add(new XYChart.Data<>(String.valueOf(compteur), rand.nextInt(100)));
                        test_label.setText(String.valueOf(rand.nextInt(100)));
                    });

                    tram.setLength(0);
                }
            }

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }
        });
    }

    public void mouse_released() {
        scrollbar_forward.setValue(50);
        scrollbar_turn.setValue(50);
    }
}