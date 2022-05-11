package com.example.bluetoothfx;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import eu.hansolo.medusa.Gauge;
import javafx.scene.paint.Color;

import java.util.ArrayList;


public class HelloController {


    XYChart.Series<String, Number> Conso_Batterie1 = new XYChart.Series<>();
    XYChart.Series<String, Number> Conso_Batterie2 = new XYChart.Series<>();
    
    gamepad Xbox_gamepad;
    SerialPort selected_value;
    boolean new_tram_reception = false;
    bluetooth connection = new bluetooth();
    StringBuilder tram = new StringBuilder();
    ArrayList<Integer> list = new ArrayList<>();
    Color Blue_gauge = Color.valueOf("#456ACF");
    Color Red_gauge = Color.RED;
    float battery_Voltage;
    float condensator_Voltage;
    float Intensite_1;
    float Intensite_2;
    long old_tick;
    boolean acc_dir = false;

    @FXML
    public ImageView pedale_png;
    @FXML
    public ImageView volant_png;
    @FXML
    private Gauge speed;
    @FXML
    private Gauge conso_global;
    @FXML
    public Gauge conso_batterie1;
    @FXML
    public Gauge conso_batterie2;
    @FXML
    public Gauge test;
    @FXML
    private LineChart<String, Number> test_chart;
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
    public Gauge direction_gauge;
    @FXML
    public Gauge acceleration_gauge;
    @FXML
    private ChoiceBox<SerialPort> bluetooth_portlist;


    Thread ConnectionPortTask = new Thread() {
        public void run() {
            connection.setPort(selected_value);
            if (connection.Connection_State == 1)
                Platform.runLater(() -> {
                    connection_status_gauge.setValue(100);
                });
    }};

    Thread start_gamepad = new Thread() {
        public void run() {

            if(Xbox_gamepad.ConnectedGamepad != null){
                while(true){
                    Xbox_gamepad.RefreshControllerData();
                    Platform.runLater(() -> {

                        scrollbar_forward.setValue(Xbox_gamepad.acceleration_gamepad);
                        scrollbar_turn.setValue(Xbox_gamepad.direction_gamepad);

                        if (System.currentTimeMillis() > old_tick + 50){
                            if (acc_dir){
                                String cmd = "a|"+(100-Xbox_gamepad.acceleration_gamepad) + "|";
                                //System.out.println(cmd);
                                if(connection.Connection_State == 1)
                                    connection.send_command(cmd);

                            }
                            else if(acc_dir == false){

                                String cmd = "t|" + Xbox_gamepad.direction_gamepad + "|";
                                //System.out.println(cmd);
                                if(connection.Connection_State == 1)
                                    connection.send_command(cmd);
                            }
                            acc_dir = !acc_dir;
                            //System.out.println(System.currentTimeMillis() - old_tick);
                            old_tick = System.currentTimeMillis();
                        }

                        acceleration_gauge.setValue(100 - Xbox_gamepad.acceleration_gamepad);
                        if(100-Xbox_gamepad.acceleration_gamepad > 95)
                            acceleration_gauge.setBarColor(Color.RED);
                        else
                            acceleration_gauge.setBarColor(Blue_gauge);

                        direction_gauge.setValue(Xbox_gamepad.direction_gamepad);
                    });
                    if( !Xbox_gamepad.ConnectedGamepad.poll() ){
                        scrollbar_forward.setValue(50);
                        System.out.println("manette deconnectée");
                        break;
                    }
                    if (new_tram_reception == true){

                        new_tram_reception = false;
                    }
                }
            }
            else
                System.out.println("Pas de manette connectée!");
        }
    };

    public void ConnectGamepad() {
        Xbox_gamepad = new gamepad();
        Xbox_gamepad.searchForControllers();
        if(Xbox_gamepad.ConnectedGamepad != null)
            start_gamepad.start();
    }
    public void initialize() {
        battery_level.setAnimated(false);
        acceleration_gauge.setAnimated(false);
        direction_gauge.setAnimated(false);
        speed.setAnimated(false);
        conso_global.setAnimated(false);
        connection_status_gauge.setAnimated(false);
        battery_level.setBarColor(Color.GREEN);
        conso_batterie1.setBarColor(Blue_gauge);
        conso_batterie2.setBarColor(Blue_gauge);
        speed.setBarColor(Blue_gauge);
        conso_global.setBarColor(Blue_gauge);
        speed.setForegroundBaseColor(Color.BLACK);
        conso_global.setForegroundBaseColor(Color.BLACK);

        bluetooth_portlist.getItems().addAll(connection.ports);
        conso_batterie1.setAnimated(false);
        conso_batterie2.setAnimated(false);

        /*scrollbar_forward.valueProperty().addListener(new ChangeListener<Number>() {
            int old_value = 100;
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                int accelerate = 100 - t1.intValue();
                if (accelerate <= old_value-3 || accelerate >= old_value+3){
                    String cmd = "a|"+accelerate + "|";
                    //System.out.println(cmd);
                    if(connection.Connection_State == 1)
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
                    //System.out.println(cmd);
                    if(connection.Connection_State == 1)
                        connection.send_command(cmd);
                    old_value = turn;
                }
            }
        });*/
        //graph settings
        Conso_Batterie1.setName("conso real time");
        test_chart.getData().add(Conso_Batterie1);
        test_chart.getData().add(Conso_Batterie2);
        test_chart.setAnimated(false);
        test_chart.setCreateSymbols(false); //cache les points
        test_chart.setHorizontalGridLinesVisible(false);
        test_chart.setVerticalGridLinesVisible(false);

    }

    public void ConnectButton() {
        selected_value =  bluetooth_portlist.getValue();
        ConnectionPortTask.start();
        selected_value.addDataListener(new SerialPortDataListener() {

            int compteur = 0;
            final int WINDOW_SIZE = 50;
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
                    //System.out.print(compteur + " -> "+ tram);
                    for (int i = 0; i < tram.length(); i++) {
                        if (tram.charAt(i) == '&') {
                            list.add(i);
                        } else if (tram.charAt(i) == '#') {
                            list.add(i);
                        } else if (tram.charAt(i) == '@') {
                            list.add(i);
                        } else if (tram.charAt(i) == '^') {
                            list.add(i);
                        }
                    }
                    if(list.size() == 8){

                        battery_Voltage = Float.parseFloat(tram.substring(list.get(0)+1,list.get(1)));
                        condensator_Voltage = Float.parseFloat(tram.substring(list.get(2)+1,list.get(3)));
                        Intensite_1 = Float.parseFloat(tram.substring(list.get(4)+1,list.get(5)));
                        Intensite_2 = Float.parseFloat(tram.substring(list.get(6)+1,list.get(7)));
                        //System.out.println("Batterie: "+battery_Voltage);
                        /*System.out.println("Condo: " + condensator_Voltage);
                        System.out.println("Ampere1: " + Intensite_1);
                        System.out.println("Ampere2: " + Intensite_2);*/
                        list.clear();
                    }
                    Platform.runLater(() -> {
                        if (Conso_Batterie1.getData().size() > WINDOW_SIZE) //Effet de translation au niveau du graph
                            Conso_Batterie1.getData().remove(0);
                        if(Conso_Batterie2.getData().size() > WINDOW_SIZE) //Effet de translation au niveau du graph
                            Conso_Batterie2.getData().remove(0);

                        if (battery_Voltage > 1){
                            battery_level.setValue((battery_Voltage - 7) * (100) / (9.25 - 7));
                        }
                        else{
                            battery_level.setValue(0);
                        }

                        Conso_Batterie1.getData().add(new XYChart.Data<>(String.valueOf(compteur), Intensite_1*-1));
                        Conso_Batterie2.getData().add(new XYChart.Data<>(String.valueOf(compteur), Intensite_2*-1));
                        conso_batterie1.setValue(Intensite_1*-1);
                        conso_batterie2.setValue(Intensite_2*-1);
                        if(Intensite_1*-1 + Intensite_2*-1 <= 50){
                            conso_global.setValue(Intensite_1*-1 + Intensite_2*-1);
                            if(Intensite_1*-1 + Intensite_2*-1 < 34)
                                conso_global.setBarColor(Blue_gauge);
                            else {
                                conso_global.setBarColor(Red_gauge);
                            }}
                        else{
                            conso_global.setValue(50);
                        }
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