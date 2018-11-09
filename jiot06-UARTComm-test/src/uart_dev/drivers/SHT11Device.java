/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uart_dev.drivers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.uart.UARTEvent;
import jdk.dio.uart.UARTEventListener;
import uart_dev.SHT11;
import uart_dev.UARTRPi;

/**
 *
 * @author yjkim
 */
public class SHT11Device extends UARTRPi {
    private double temperature = 0;
    private double humidity = 0;
    private boolean active = false;
    
    private final Semaphore stateUpdated = new Semaphore(0, true);
    
    public SHT11Device(String controllerName) throws IOException {
        super(controllerName);
        setEventListener(UARTEvent.INPUT_DATA_AVAILABLE, new SHT11EventListener());
    }
    
    private boolean waitForUpdate() throws InterruptedException {
        return stateUpdated.tryAcquire(2, TimeUnit.SECONDS);
    }

    public boolean isActive() {
        try {
            SHT11.CHK_ACK.send(this);
            if (!waitForUpdate())   // timeout
                active = false;
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SHT11Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return active;
    }
    
    public double getTemperature() {
        try {
            SHT11.GET_TMP.send(this);
            waitForUpdate();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SHT11Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temperature;
    }
    
    public double getHumidity() {
        try {
            SHT11.GET_HMD.send(this);
            waitForUpdate();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SHT11Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return humidity;
    }
    
    class SHT11EventListener implements UARTEventListener {

        @Override
        public void eventDispatched(UARTEvent uarte) {
            if (uarte.getID() == UARTEvent.INPUT_DATA_AVAILABLE) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(100);
                try {
                    int length = readData(buffer);
                    byte[] bytes = new byte[buffer.position()];
                    buffer.flip();
                    buffer.get(bytes);
                    String response = new String(bytes);
                    System.out.print(response);
                    String[] tokens = response.split("=|\\n");
                    boolean release = false;
                    switch(tokens[0].charAt(0)) {
                        case 'H':
                            humidity = Double.parseDouble(tokens[1]);
                            release = true;
                            break;
                        case 'T':
                            temperature = Double.parseDouble(tokens[1]);
                            release = true;
                            break;
                        case 'O':
                            if (tokens[0].equals("OK")) {
                                active = true;
                                release = true;
                            }
                        default:
                            break;
                    }
                    if (release) {
                        stateUpdated.release();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }
        
    }
    
}
