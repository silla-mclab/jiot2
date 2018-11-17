/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiot.raspi.ext_dev;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jiot.raspi.thing.AnalogInputPoint;

/**
 *
 * @author yjkim
 */
public class DustSensor extends AnalogInputPoint implements ExtendedInput {
    private int irGpioId;
    private GPIOPin irLed = null;
    
    public DustSensor(int channel, int gpioId) {
        super(channel);
        irGpioId = gpioId;
    }
    
    public void open() {
        try {
            super.open(false);      // open without polling
            irLed = DeviceManager.open(irGpioId, GPIOPin.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        setName("Dust(analog#" + getChannel() + ", gpio" + irGpioId +")");
    }
    
    public void close() {
        try {
            super.close();
            irLed.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean isEnabled() {
        return (super.isEnabled() && irLed.isOpen());
    }
    
    public Type getType() {
        return Type.AIE;
    }
    
    public double getDustDensity() {
        double voltage = 0;
        try {
            irLed.setValue(false);      // irLED on
            Thread.sleep(0, 280000);    // delay during 280 usec
            voltage = read();
            Thread.sleep(0, 40000);     // delay during 40 usec
            irLed.setValue(true);       // irLED off
            Thread.sleep(9, 680000);    // delay during 9680 usec
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(DustSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
        voltage = (voltage * 5.0) / 4095;
        return 0.172*voltage - 0.1;
    }

    @Override
    public double getValue() {
        return getDustDensity();
    }
    
}
