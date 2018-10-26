/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiot03.buttonevent;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;

/**
 *
 * @author yjkim
 */
public class PushButtonEventEx implements Runnable {
    public static final String LED_PIN = "GPIO17";
    public static final String BTN1_PIN = "GPIO23";
    public static final String BTN2_PIN = "GPIO24";
    public static final String PIR_PIN = "GPIO25";

    private GPIOPin ledPin = null;
    private GPIOPin btn1Pin = null;
    private GPIOPin btn2Pin = null;
    private GPIOPin pirPin = null;
    
    private volatile boolean togglingStop = true, exit = false;
    
    public PushButtonEventEx() throws IOException {
        ledPin = DeviceManager.open(LED_PIN, GPIOPin.class);
        btn1Pin = DeviceManager.open(BTN1_PIN, GPIOPin.class);
        btn2Pin = DeviceManager.open(BTN2_PIN, GPIOPin.class);
        pirPin = DeviceManager.open(PIR_PIN, GPIOPin.class);
        
        btn1Pin.setInputListener(new PinListener() {
            @Override
            public void valueChanged(PinEvent pe) {
                if (!pe.getValue()) {
                    togglingStop = !togglingStop;
                }
            }
        });
        
        btn2Pin.setInputListener(new PinListener() {
            @Override
            public void valueChanged(PinEvent pe) {
                if (!pe.getValue()) {
                    exit = true;
                }
            }
        });
        
        pirPin.setInputListener(new PIRListener());
        
        System.out.println("LED & Button devices successfully opened...");
    }
    
    public void close() throws IOException {
        ledPin.close();
        btn1Pin.close();
        btn2Pin.close();
        pirPin.close();
        System.out.println("All devices successfully closed...");
    }
    
    public void run() {
        System.out.println("LED Toggling...");
        while (!exit) {
            if (!togglingStop) {
                try {
                    ledPin.setValue(true);
                    Thread.sleep(500);
                    
                    ledPin.setValue(false);
                    Thread.sleep(500);
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(PushButtonEventEx.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        try {
            close();
        } catch (IOException ex) {
            Logger.getLogger(PushButtonEventEx.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    class PIRListener implements PinListener {
        @Override
        public void valueChanged(PinEvent pe) {
                togglingStop = !pe.getValue();
        }        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Thread t = new Thread(new PushButtonEventEx());
            t.start();
        } catch (IOException ex) {
            Logger.getLogger(PushButtonEventEx.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
