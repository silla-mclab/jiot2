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
import jiot.raspi.thing.OutputControlPoint;

/**
 *
 * @author yjkim
 */
public class VantilationFan extends OutputControlPoint {
    private int in1Id, in2Id;
    private GPIOPin in1Pin = null, in2Pin = null;
    
    public VantilationFan(int in1Id, int in2Id) {
        this.in1Id = in1Id;
        this.in2Id = in2Id;
    }

    private void fanOff() {
        if (in1Pin != null && in2Pin != null) {
            try {
                in1Pin.setValue(false);
                in2Pin.setValue(false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void fanOn() {
        if (in1Pin != null && in2Pin != null) {
            try {
                in1Pin.setValue(true);
                in2Pin.setValue(false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
       
    @Override
    public void setPresentValue(int value) {
        int oldValue = getPresentValue();

        switch (value) {
            case 0:
                fanOff();
                break;
            case 1:
                fanOn();
                break;
            default:
                return;
        }
        presentValue.set(value);
        
        if (oldValue != getPresentValue()) {
                fireChanged();
        }
    }

    @Override
    public void open() {
        try {
            in1Pin = DeviceManager.open(in1Id, GPIOPin.class);
            in2Pin = DeviceManager.open(in2Id, GPIOPin.class);
            
            // fan off
            setPresentValue(0);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        setName("Fan(gpio" + in1Id + ", gpio" + in2Id +")");
    }

    @Override
    public void close() {
        if (in1Pin != null && in2Pin != null) {
            try {
                in1Pin.close();
                in2Pin.close();
                in1Pin = null;
                in2Pin = null;
            } catch (IOException ex) {
                Logger.getLogger(VantilationFan.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return (in1Pin != null && in1Pin.isOpen() &&
                in2Pin != null && in2Pin.isOpen());
    }

    @Override
    public Type getType() {
        return Type.DO;
    }
    
}
