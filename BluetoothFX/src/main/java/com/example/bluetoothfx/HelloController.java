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
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;


public class HelloController {



    XYChart.Series<String, Number> Serie_Conso_Totale = new XYChart.Series<>();
    XYChart.Series<String, Number> Serie_Conso_Batterie1 = new XYChart.Series<>();
    XYChart.Series<String, Number> Serie_Conso_Batterie2 = new XYChart.Series<>();
    
    gamepad Xbox_gamepad;
    SerialPort selected_value;
    boolean new_tram_reception = false;
    bluetooth connection = new bluetooth();
    StringBuilder tram = new StringBuilder();
    ArrayList<Integer> list = new ArrayList<>();
    Color Blue_gauge = Color.valueOf("#456ACF");
    Color Back_blue = Color.valueOf("#dbe0ec");
    Color Red_gauge = Color.RED;
    Color Back_grey = Color.valueOf("#e2e2e2");

    int accelerate;

    float battery_Voltage;
    float condensator_Voltage;
    float Intensite_1;
    float Intensite_2;
    float Speed;

    long old_tick;
    boolean acc_dir = false;

    @FXML
    public Gauge gauge_recup_condo;
    @FXML
    public AnchorPane Linegraph_BOX;
    @FXML
    public CheckBox checkbox_conso_general;
    @FXML
    public CheckBox checkbox_conso2;
    @FXML
    public CheckBox checkbox_conso1;
    @FXML
    public ToggleButton Linegraph_button;
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

                        /*if (System.currentTimeMillis() > old_tick + 50){
                            if (acc_dir){
                                String cmd = "a|" + Xbox_gamepad.acceleration_gamepad + "|";
                                if(100-Xbox_gamepad.acceleration_gamepad <=9)
                                    cmd = "a|0"+ Xbox_gamepad.acceleration_gamepad + "|";
                                if(100-Xbox_gamepad.acceleration_gamepad == 100)
                                    cmd = "a|99|";
                                //System.out.println(cmd);

                                if(connection.Connection_State == 1)
                                    connection.send_command(cmd);

                            }
                            else if(acc_dir == false){

                                String cmd = "t|" + Xbox_gamepad.direction_gamepad + "|";
                                //System.out.println(cmd);
                                if(100-Xbox_gamepad.direction_gamepad <=9)
                                    cmd = "t|0"+ Xbox_gamepad.direction_gamepad + "|";
                                if(100-Xbox_gamepad.direction_gamepad == 100)
                                    cmd = "t|99|";
                                if(connection.Connection_State == 1)
                                    connection.send_command(cmd);
                            }
                            acc_dir = !acc_dir;
                            //System.out.println(System.currentTimeMillis() - old_tick);
                            old_tick = System.currentTimeMillis();
                        }*/
                        if(Speed > 0 & (accelerate == 50 | accelerate == 51 | accelerate == 49)){
                            acceleration_gauge.setBarColor(Color.GREEN);
                            acceleration_gauge.setValue(100);
                        }
                        else if(100-Xbox_gamepad.acceleration_gamepad > 95) {
                            acceleration_gauge.setBarColor(Color.RED);
                            acceleration_gauge.setValue(100 - Xbox_gamepad.acceleration_gamepad);
                        }
                        else{
                        acceleration_gauge.setValue(100 - Xbox_gamepad.acceleration_gamepad);
                        acceleration_gauge.setBarColor(Blue_gauge);
                        }
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
        Linegraph_BOX.setVisible(false);
        battery_level.setAnimated(false);
        acceleration_gauge.setAnimated(false);
        acceleration_gauge.setAnimationDuration(400);
        direction_gauge.setAnimated(false);
        direction_gauge.setAnimationDuration(400);
        gauge_recup_condo.setAnimated(true);
        gauge_recup_condo.setAnimationDuration(400);
        speed.setAnimated(true);
        speed.setAnimationDuration(400);
        conso_global.setAnimated(true);
        conso_global.setAnimationDuration(400);
        connection_status_gauge.setAnimated(false);
        battery_level.setBarColor(Color.GREEN);
        gauge_recup_condo.setBarColor(Color.GREEN);
        conso_batterie1.setBarColor(Back_blue);
        conso_batterie2.setBarColor(Back_blue);


        conso_batterie1.setBarBackgroundColor(Back_grey);
        conso_batterie2.setBarBackgroundColor(Back_grey);
        gauge_recup_condo.setBarBackgroundColor(Back_grey);


        conso_batterie1.setNeedleColor(Blue_gauge);
        conso_batterie2.setNeedleColor(Blue_gauge);
        speed.setBarColor(Blue_gauge);
        conso_global.setBarColor(Blue_gauge);
        speed.setForegroundBaseColor(Color.BLACK);
        conso_global.setForegroundBaseColor(Color.BLACK);

        bluetooth_portlist.getItems().addAll(connection.ports);
        conso_batterie1.setAnimated(true);
        conso_batterie1.setAnimationDuration(400);
        conso_batterie2.setAnimated(true);
        conso_batterie2.setAnimationDuration(400);
        scrollbar_forward.valueProperty().addListener(new ChangeListener<Number>() {
            int old_value = 100;
            String cmd;
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {

                accelerate = 100 - t1.intValue();
                if (accelerate <= old_value-2 || accelerate >= old_value+2){
                    cmd = "a|"+accelerate + "|";
                    if(accelerate <=9)
                        cmd = "a|0"+ accelerate + "|";
                    if(accelerate == 100)
                        cmd = "a|99|";
                    //System.out.println(cmd);
                    if(connection.Connection_State == 1)
                        connection.send_command(cmd);
                    old_value = accelerate;
                }
            }
        });

        scrollbar_turn.valueProperty().addListener(new ChangeListener<Number>() {
            int old_value = 100;
            String cmd;
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                int turn = t1.intValue();
                if (turn<= old_value-2 || turn >=old_value+2){
                    cmd = "t|"+turn+ "|";
                    if(turn <=9)
                        cmd = "t|0"+turn+ "|";
                    if(turn == 100)
                        cmd = "t|99|";
                    if(connection.Connection_State == 1)
                        connection.send_command(cmd);
                    old_value = turn;
                }
            }
        });
        //graph settings
        Serie_Conso_Batterie1.setName("Puissance Moteur 1 en Watt");
        Serie_Conso_Batterie2.setName("Puissance Moteur 2 en Watt");
        Serie_Conso_Totale.setName("Puissance Totale en Watt");
        test_chart.setAnimated(false);
        test_chart.setCreateSymbols(false); //cache les points
        test_chart.setHorizontalGridLinesVisible(false);
        test_chart.setVerticalGridLinesVisible(false);

    }

    public void ConnectButton() {
        long initial_time = System.currentTimeMillis();

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

                if (tram.indexOf("\n") != -1 & System.currentTimeMillis() > initial_time+3000 & tram.indexOf("&")==0){
                    compteur++;
                    System.out.print(tram);
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
                        else if (tram.charAt(i) == '*') {
                            list.add(i);
                        }
                    }
                    if(list.size() == 10 & System.currentTimeMillis() > initial_time+1000){

                        battery_Voltage = Float.parseFloat(tram.substring(list.get(0)+1,list.get(1)));
                        condensator_Voltage = Float.parseFloat(tram.substring(list.get(2)+1,list.get(3)));
                        Intensite_1 = Float.parseFloat(tram.substring(list.get(4)+1,list.get(5)));
                        Intensite_2 = Float.parseFloat(tram.substring(list.get(6)+1,list.get(7)));
                        Speed = Float.parseFloat(tram.substring(list.get(8)+1,list.get(9)));
                        //System.out.println("Batterie: "+battery_Voltage);
                        //System.out.println("Condo: " + condensator_Voltage);
                        //System.out.println("Ampere1: " + Intensite_1);
                        //System.out.println("Ampere2: " + Intensite_2);
                        //System.out.println(Speed);

                        list.clear();

                    Platform.runLater(() -> {
                        if (Serie_Conso_Batterie1.getData().size() > WINDOW_SIZE) //Effet de translation au niveau du graph
                            Serie_Conso_Batterie1.getData().remove(0);
                        if(Serie_Conso_Batterie2.getData().size() > WINDOW_SIZE) //Effet de translation au niveau du graph
                            Serie_Conso_Batterie2.getData().remove(0);
                        if(Serie_Conso_Totale.getData().size() > WINDOW_SIZE)
                            Serie_Conso_Totale.getData().remove(0);
                        speed.setValue(Speed);
                        if (battery_Voltage > 1){
                            battery_level.setValue((battery_Voltage - 7) * (100) / (9.25 - 7));
                        }
                        else{
                            battery_level.setValue(0);
                        }

                        Serie_Conso_Batterie1.getData().add(new XYChart.Data<>(String.valueOf(compteur), Intensite_1));
                        Serie_Conso_Batterie2.getData().add(new XYChart.Data<>(String.valueOf(compteur), Intensite_2));
                        Serie_Conso_Totale.getData().add(new XYChart.Data<>(String.valueOf(compteur), Intensite_2+Intensite_1));
                        conso_batterie1.setValue(Intensite_1);
                        conso_batterie2.setValue(Intensite_2);

                        if(accelerate == 50 | accelerate == 51 | accelerate == 49){
                            gauge_recup_condo.setValue(condensator_Voltage);

                        }
                        else
                            gauge_recup_condo.setValue(0);

                        if(Intensite_1*-1 + Intensite_2*-1 <= 50){
                            conso_global.setValue(Intensite_1 + Intensite_2);
                            if(Intensite_1*-1 + Intensite_2*-1 < 34)
                                conso_global.setBarColor(Blue_gauge);
                            else {
                                conso_global.setBarColor(Red_gauge);
                            }}
                        else{
                            conso_global.setValue(50);
                        }
                    });
                    }

                    tram.setLength(0);
                }
                else if(tram.indexOf("\n") != -1){
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

    public void toggle_button(ActionEvent actionEvent) {
        if(actionEvent.getSource() == Linegraph_button){
            if(Linegraph_button.isSelected())
                Linegraph_BOX.setVisible(true);
            else
                Linegraph_BOX.setVisible(false);
        }

    }
    public void graph_checklist(ActionEvent actionEvent){
        if(actionEvent.getSource() == checkbox_conso1){
            if(checkbox_conso1.isSelected())
                test_chart.getData().add(Serie_Conso_Batterie1);
            else
                test_chart.getData().removeAll(Serie_Conso_Batterie1);
            }
        else if(actionEvent.getSource() == checkbox_conso2){
            if(checkbox_conso2.isSelected())
                test_chart.getData().add(Serie_Conso_Batterie2);
            else
                test_chart.getData().removeAll(Serie_Conso_Batterie2);

            }
        else if(actionEvent.getSource() == checkbox_conso_general){
            if(checkbox_conso_general.isSelected())
                test_chart.getData().add(Serie_Conso_Totale);
            else
                test_chart.getData().removeAll(Serie_Conso_Totale);
            }

    }

}