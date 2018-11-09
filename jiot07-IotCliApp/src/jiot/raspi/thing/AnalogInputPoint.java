package jiot.raspi.thing;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import jiot.raspi.spi_dev.SPIRPi;
import jiot.raspi.spi_dev.drivers.MCP3208Device;

/**
 *
 * @author yjkim
 */
public class AnalogInputPoint extends ControlPoint {

    private static AtomicReference<MCP3208Device> adcDevice = 
            new AtomicReference<MCP3208Device>();

    private static MCP3208Device getAdcDevice() {
        try {
            if (adcDevice.get() == null)
                adcDevice.set(new MCP3208Device(SPIRPi.CE1));			
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return adcDevice.get();
    }

    private static final AtomicInteger OPEN_COUNT = new AtomicInteger(0);
    private int channel;
    private Future pollingFuture;
    
    public AnalogInputPoint(int channel) {
        super();
        this.channel = channel;
    }
    
    public int read() {
        int value = -1;
        try {
            value = getAdcDevice().analogRead(channel);
            presentValue.set(value);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return value;
    }
    
    @Override
    public void open() {
        OPEN_COUNT.incrementAndGet();

        pollingFuture = POLLING.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    int oldValue = presentValue.get();
                    int newValue = getAdcDevice().analogRead(channel);
                    presentValue.set(newValue);
                    if (oldValue != newValue) {
                        fireChanged();
                    }					
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        setName("AI_channel_" + channel);
    }

    @Override
    public void close() {
        int ref_count = OPEN_COUNT.decrementAndGet();
        if (ref_count > 0) {
            pollingFuture.cancel(false);
        }
        else if (ref_count == 0) {
            getAdcDevice().close();
            adcDevice.set(null);
        }
        else {
            OPEN_COUNT.set(0);
        }
    }

    @Override
    public boolean isEnabled() {
        return (getAdcDevice().device.isOpen());
    }

    @Override
    public Type getType() {
        return Type.AI;
    }
    
}
