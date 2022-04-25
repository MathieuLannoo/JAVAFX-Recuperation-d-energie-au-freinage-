package com.example.bluetoothfx;
import com.fazecast.jSerialComm.SerialPort;

class bluetooth {

    SerialPort activePort;
    SerialPort[] ports = SerialPort.getCommPorts();
    int Connection_State = 0;

    public void send_command(String command){
        String cmd_sent = command + "#";
        activePort.writeBytes(cmd_sent.getBytes(), cmd_sent.length());
    }

    public void setPort(SerialPort Port) {

        if (Port.openPort()){
            System.out.println(Port.getDescriptivePortName() + " ouvert");
            activePort = Port;
            Connection_State = 1;
        }
    }
}