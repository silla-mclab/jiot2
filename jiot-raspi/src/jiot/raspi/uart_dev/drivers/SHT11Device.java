/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiot.raspi.uart_dev.drivers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jiot.raspi.uart_dev.SHT11;
import jiot.raspi.uart_dev.UARTRPi;

/**
 *
 * @author yjkim
 */
public class SHT11Device {
    public static final String DEV_NAME = "SHT11";
    
    private UARTRPi uart = null;
    private double temperature = 0;
    private double humidity = 0;
    private boolean active = false;
    
    public SHT11Device() throws IOException {
        uart = UARTRPi.getInstance();
        uart.addDataListener(DEV_NAME, new SHT11EventListener());
    }
    
    public void close() {
        try {
            uart.removeDataListener(DEV_NAME);
            uart.close();
        } catch (IOException ex) {
            Logger.getLogger(SHT11Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isActive() {
        try {
            SHT11.CHK_ACK.send(uart);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SHT11Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return active;
    }
    
    public double getTemperature() {
        try {
            SHT11.GET_TMP.send(uart);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SHT11Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temperature;
    }
    
    public double getHumidity() {
        try {
            SHT11.GET_HMD.send(uart);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SHT11Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return humidity;
    }
    
    class SHT11EventListener implements UARTRPi.UARTDataListener {

        @Override
        public boolean processReceivedData(String data) {
            boolean processed = false;
            String[] tokens = data.split("=|\\n");
            if (tokens[0].equals("H")) {
                humidity = Double.parseDouble(tokens[1]);
                processed = true;
            }
            else if (tokens[0].equals("T")) {
                temperature = Double.parseDouble(tokens[1]);
                processed = true;
            }
            else if (tokens[0].equals("OK")) {
                active = true;
                processed = true;
            }
            return processed;
        }
        
    }
    
}
