package com.example.bluetoothfx;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.util.Arrays;

class bluetooth {


    SerialPort activePort;
    StringBuilder tram = new StringBuilder();
    SerialPort[] ports = SerialPort.getCommPorts();
    int Connection_State = 0;

    public void send_command(String command){
        String cmd_sent = command + "#";
        activePort.writeBytes(cmd_sent.getBytes(), cmd_sent.length());
    }

    public void setPort(SerialPort Port) {

        if (Port.openPort())
            System.out.println(Port.getDescriptivePortName() + " ouvert");
            activePort = Port;
            Connection_State = 1;

        Port.addDataListener(new SerialPortDataListener() {

            @Override
            public void serialEvent(SerialPortEvent event) {
                int size = event.getSerialPort().bytesAvailable();
                byte[] buffer = new byte[size];
                event.getSerialPort().readBytes(buffer, size);
                tram.setLength(0);
                for(byte b:buffer)
                    if(b != 0)
                        tram.append((char)b);
                System.out.print(tram);
            }

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }
        });
    }
}