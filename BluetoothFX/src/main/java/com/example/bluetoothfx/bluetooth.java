package com.example.bluetoothfx;
import java.util.Scanner;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

class bluetooth {

    SerialPort activePort;
    SerialPort[] ports = SerialPort.getCommPorts();
    public void showAllPort() {
        int i = 0;
        for(SerialPort port : ports) {
            System.out.print(i + ". " + port.getDescriptivePortName() + " ");
            System.out.println(port.getPortDescription());
            i++;
        }
    }
    public void send_command(String command){

        activePort.writeBytes(command.getBytes(), command.length());
    }

    public void setPort(int portIndex) {
        activePort = ports[portIndex];
        if (activePort.openPort())
            System.out.println(activePort.getDescriptivePortName() + " ouvert");

        activePort.addDataListener(new SerialPortDataListener() {

            @Override
            public void serialEvent(SerialPortEvent event) {
                int size = event.getSerialPort().bytesAvailable();
                byte[] buffer = new byte[size];
                event.getSerialPort().readBytes(buffer, size);
                StringBuilder tram = new StringBuilder();
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

    public void start() {
        showAllPort();
        Scanner reader = new Scanner(System.in);
        System.out.print("Port? ");
        int p = reader.nextInt();
        setPort(p);
        /*System.out.print("Commande? ");
        String cmd = reader.next();
        send_command(cmd);*/
        reader.close();
    }
}