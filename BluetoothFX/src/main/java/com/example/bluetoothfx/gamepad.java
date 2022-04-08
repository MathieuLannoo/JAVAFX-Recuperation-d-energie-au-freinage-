package com.example.bluetoothfx;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import java.util.Objects;

public class gamepad {
    Controller ConnectedGamepad;
    int direction_gamepad;
    int acceleration_gamepad;


    public gamepad() {
        searchForControllers();

    }

    private void searchForControllers() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for(int i = 0; i < controllers.length; i++){
            Controller controller = controllers[i];

            if (controller.getType() == Controller.Type.GAMEPAD) {

                ConnectedGamepad = controller;

                System.out.println(controller.getName() + " - " + controller.getType().toString() + " type");
            }
        }
    }

    public void startShowingControllerData(){
        while(true)
        {

            System.out.println("direction:  " + direction_gamepad);
            System.out.println("acceleration:  " + acceleration_gamepad);
            // Currently selected controller.
            //int selectedControllerIndex = window.getSelectedControllerName();
            //Controller controller = foundControllers.get(selectedControllerIndex);
            Controller controller = ConnectedGamepad;
            // Pull controller for current data, and break while loop if controller is disconnected.
            if( !controller.poll() ){
                System.out.println("manette deconnectée");
                break;
            }

            // X axis and Y axis
            int xAxisPercentage = 0;
            int yAxisPercentage = 0;
            // JPanel for other axes.
            /*JPanel axesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 2));
            axesPanel.setBounds(0, 0, 200, 190);

            // JPanel for controller buttons
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
            buttonsPanel.setBounds(6, 19, 246, 110);*/

            // Go trough all components of the controller.
            Component[] components = controller.getComponents();
            //System.out.println(Arrays.toString(components));
            for(int i=0; i < components.length; i++)
            {
                Component component = components[i];
                Component.Identifier componentIdentifier = component.getIdentifier();

                // Buttons
                //if(component.getName().contains("Button")){ // If the language is not english, this won't work.
                if(componentIdentifier.getName().matches("^[0-9]*$")){ // If the component identifier name contains only numbers, then this is a button.
                    // Is button pressed?
                    boolean isItPressed = true;
                    if(component.getPollData() == 0.0f)
                        isItPressed = false;

                    // Button index
                    String buttonIndex;
                    buttonIndex = component.getIdentifier().toString();
                    /*
                    // Create and add new button to panel.
                    JToggleButton aToggleButton = new JToggleButton(buttonIndex, isItPressed);
                    aToggleButton.setPreferredSize(new Dimension(48, 25));
                    aToggleButton.setEnabled(false);
                    buttonsPanel.add(aToggleButton);*/

                    // We know that this component was button so we can skip to next component.
                    continue;
                }

                // Hat switch
                if(componentIdentifier == Component.Identifier.Axis.POV){
                    float hatSwitchPosition = component.getPollData();
                    // We know that this component was hat switch so we can skip to next component.
                    continue;
                }

                // Axes
                if(component.isAnalog()){
                    float axisValue = component.getPollData();
                    int axisValueInPercentage = getAxisValueInPercentage(axisValue);

                    // X axis
                    if(componentIdentifier == Component.Identifier.Axis.X){
                        xAxisPercentage = axisValueInPercentage;
                        continue; // Go to next component.
                    }
                    // Y axis
                    if(componentIdentifier == Component.Identifier.Axis.Y){
                        yAxisPercentage = axisValueInPercentage;
                        continue; // Go to next component.
                    }
                    if (Objects.equals(component.getName(), "Axe Z")) {
                        acceleration_gamepad = axisValueInPercentage;
                    }
                    /*
                    // Other axis
                    JLabel progressBarLabel = new JLabel(component.getName());
                    JProgressBar progressBar = new JProgressBar(0, 100);
                    progressBar.setValue(axisValueInPercentage);*/

                   /* axesPanel.add(progressBarLabel);
                    axesPanel.add(progressBar);*/
                }

            }
            direction_gamepad = 100 - xAxisPercentage;
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
                //Logger.getLogger(JoystickTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }



    /**
     * Given value of axis in percentage.
     * Percentages increases from left/top to right/bottom.
     * If idle (in center) returns 50, if joystick axis is pushed to the left/top
     * edge returns 0 and if it's pushed to the right/bottom returns 100.
     *
     * @return value of axis in percentage.
     */
    public int getAxisValueInPercentage(float axisValue)
    {
        return (int)(((2 - (1 - axisValue)) * 100) / 2);
    }
}
