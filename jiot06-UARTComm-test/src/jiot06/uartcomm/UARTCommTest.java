/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiot06.uartcomm;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import uart_dev.drivers.SHT11Device;

/**
 *
 * @author yjkim
 */
public class UARTCommTest {
    private SHT11Device sht11 = null;
    
    public UARTCommTest() throws IOException {
        sht11 = new SHT11Device(null);
    }
    
    public void run() throws InterruptedException, IOException {
        for (int i=0; i<10; i++) {
            System.out.println("SHT11 Active " + sht11.isActive());
            Thread.sleep(500);
            System.out.println("Temperature = " + sht11.getTemperature());
            Thread.sleep(500);
            System.out.println("Humidity = " + sht11.getHumidity());
            Thread.sleep(500);            
        }
        sht11.close();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UARTCommTest test = new UARTCommTest();
            test.run();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(UARTCommTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
