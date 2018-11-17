package jiot.raspi.ext_dev;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jiot.raspi.thing.ControlPoint;
import jiot.raspi.uart_dev.UARTRPi;
import jiot.raspi.uart_dev.drivers.SHT11Device;

/**
 *
 * @author yjkim
 */
public class UARTCommPoint extends ControlPoint implements CommandExecutable {
    private SHT11Device sht11 = null;
    
    @Override
    public void open() {
        try {
            if (sht11 == null) {
                sht11 = new SHT11Device();
            }
        } catch (IOException ex) {
            Logger.getLogger(UARTCommPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        setName("UARTComm");
    }

    @Override
    public void close() {
        if (sht11 != null) {
            sht11.close();
        }
    }

    @Override
    public boolean isEnabled() {
        boolean isOpen = false;
        try {
            isOpen = UARTRPi.getInstance().getPort().isOpen();
        } catch (IOException ex) {
            Logger.getLogger(UARTCommPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isOpen;
    }

    @Override
    public Type getType() {
        return Type.UART;
    }

    @Override
    public String executeCommmad(String[] command) {
        String response = null;
        
        switch (Integer.parseInt(command[0])) {
            case 1:     // get temperature
                response = String.valueOf(sht11.getTemperature());
                break;
            case 2:     // get humidity
                response = String.valueOf(sht11.getHumidity());
                break;
            case 3:     // check if sht11 is active    
                response = String.valueOf(sht11.isActive());
                break;
        }
        
        return response;
    }
    
}
