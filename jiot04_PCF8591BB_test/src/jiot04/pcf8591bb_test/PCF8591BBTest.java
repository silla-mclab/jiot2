/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiot04.pcf8591bb_test;

import i2c_dev.I2CUtils;
import i2c_dev.drivers.PCF8591Device;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yjkim
 */
public class PCF8591BBTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            PCF8591Device adConverter = new PCF8591Device();
            
            adConverter.analogRead(0);
            I2CUtils.I2Cdelay(500);
            adConverter.analogRead(0);
            I2CUtils.I2Cdelay(500);
            System.out.println("Potentimeter Input : " + adConverter.analogRead(0));
            
            adConverter.analogRead(1);
            I2CUtils.I2Cdelay(500);
            adConverter.analogRead(1);
            I2CUtils.I2Cdelay(500);
            System.out.println("CDS Input : " + adConverter.analogRead(1));
            
            adConverter.analogRead(2);
            I2CUtils.I2Cdelay(500);
            adConverter.analogRead(2);
            I2CUtils.I2Cdelay(500);
            System.out.println("Themistor Input : " + adConverter.analogRead(2));
            
            System.out.println("LED Dimming...");
            adConverter.analogWrite(0xff);
            I2CUtils.I2Cdelay(2000);
            
            adConverter.analogWrite(0xbf);
            I2CUtils.I2Cdelay(2000);

            adConverter.analogWrite(0x9f);
            I2CUtils.I2Cdelay(2000);
            
            adConverter.analogWrite(0x4f);
            I2CUtils.I2Cdelay(2000);
            
            adConverter.analogWrite(0x00);
        } catch (IOException ex) {
            Logger.getLogger(PCF8591BBTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
