package com.example.bluetoothfx;
import net.java.games.input.*;

public class controller {

    /* Create an event object for the underlying plugin to populate */
    Event event = new Event();
    /* Get the available controllers */
    Controller[] test = ControllerEnvironment.getDefaultEnvironment().getControllers();
}
