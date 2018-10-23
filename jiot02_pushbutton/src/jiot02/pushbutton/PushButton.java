/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiot02.pushbutton;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;

/**
 *
 * @author yjkim
 */
public class PushButton {
    public static final String LED_PIN = "GPIO17";
    public static final String BTN1_PIN = "GPIO23";
    public static final String BTN2_PIN = "GPIO24";

    private GPIOPin ledPin = null;
    private GPIOPin btnPin1 = null;
    private GPIOPin btnPin2 = null;
    
    public PushButton() throws IOException {
        ledPin = DeviceManager.open(LED_PIN, GPIOPin.class);
        btnPin1 = DeviceManager.open(BTN1_PIN, GPIOPin.class);
        btnPin2 = DeviceManager.open(BTN2_PIN, GPIOPin.class);
    }
    
    public void close() throws IOException {
        ledPin.close();
        btnPin1.close();
        btnPin2.close();
    }
    
    public void run() throws IOException, InterruptedException {
        boolean value = false;
        while (true) {
//            ledPin.setValue(!(btnPin1.getValue()));
            value = btnPin1.getValue();
            ledPin.setValue(value);
            System.out.println("LED value = " + value);
            Thread.sleep(250);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PushButton btnObj;
        try {
            btnObj = new PushButton();
            btnObj.run();
        } catch (IOException | InterruptedException ex) {
//            Logger.getLogger(PushButton.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } 
    }
    
}
