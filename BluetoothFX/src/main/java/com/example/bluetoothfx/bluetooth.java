package com.example.bluetoothfx;
import com.fazecast.jSerialComm.SerialPort;

class bluetooth {

    SerialPort activePort;

    SerialPort[] ports = SerialPort.getCommPorts();
    int Connection_State = 0;
    StringBuilder cmd_sent = new StringBuilder();
    public void send_command(String command){
        cmd_sent.setLength(0);
        cmd_sent.append(command);
        cmd_sent.append("#");
        activePort.writeBytes(cmd_sent.toString().getBytes(), cmd_sent.length());
    }

    public void setPort(SerialPort Port) {

        if (Port.openPort()){
            System.out.println(Port.getDescriptivePortName() + " ouvert");
            activePort = Port;
            activePort.setComPortParameters(9600,8,1,0);
            Connection_State = 1;
        }
    }
}