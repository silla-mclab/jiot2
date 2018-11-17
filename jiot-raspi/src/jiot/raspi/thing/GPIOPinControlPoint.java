package jiot.raspi.thing;

import java.io.IOException;

import jdk.dio.DeviceManager;
import jdk.dio.DeviceNotFoundException;
import jdk.dio.UnavailableDeviceException;
import jdk.dio.UnsupportedDeviceTypeException;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;

public class GPIOPinControlPoint extends ControlPoint {
    private int pinId;
    private GPIOPin pinDev;

    public GPIOPinControlPoint(int pinId) {
        super();
        this.pinId = pinId;
    }

    @Override
    public void open() {
        try {
            pinDev = (GPIOPin)DeviceManager.open(pinId, GPIOPin.class);
            presentValue.set(pinDev.getValue() ? 1 : 0);
            setName("GPIO"+pinId);
        } catch (UnsupportedDeviceTypeException | DeviceNotFoundException | UnavailableDeviceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            pinDev.setInputListener(new PinListener() {
                @Override
                public void valueChanged(PinEvent arg0) {
                    try {
                        int oldValue = getPresentValue();
                        int newValue = pinDev.getValue() ? 1 : 0;
                        presentValue.set(newValue);
                        if (oldValue != newValue) {
                                fireChanged();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (isEnabled()) {
            try {
                pinDev.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pinDev = null;
        }
    }

    @Override
    public boolean isEnabled() {
        return (pinDev != null && pinDev.isOpen());
    }

    @Override
    public Type getType() {
        return Type.DI;
    }
}
