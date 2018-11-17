/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiot07.iotcliapp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jiot.raspi.cli.CLIConsole;
import jiot.raspi.thing.ControlPoint;
import jiot.raspi.thing.ControlPointContainer;

/**
 *
 * @author yjkim
 */
public class IotCliApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	ControlPointContainer pointContainer = ControlPointContainer.getInstance();
        pointContainer.start();
        
//        UARTConfig config = new UARTConfig( 
//        	  "ttyAMA0", 1, 9600,
//            UARTConfig.DATABITS_8,
//            UARTConfig.PARITY_NONE,
//            UARTConfig.STOPBITS_1,
//            UARTConfig.FLOWCONTROL_NONE
//         );
//        
//        UARTConsole console = new UARTConsole(config);

        try {
            CLIConsole console = new CLIConsole(null);
            
            for(ControlPoint point: pointContainer.getControlPoints()){
                point.addObserver(console);
             }

            console.setChangeLogDisplsy(false);
            console.run();
        } catch (IOException ex) {
            Logger.getLogger(IotCliApp.class.getName()).log(Level.SEVERE, null, ex);
        }
               
        pointContainer.stop();
    }
    
}
