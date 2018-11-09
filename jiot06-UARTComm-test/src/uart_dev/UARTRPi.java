/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uart_dev;

import java.io.IOException;
import java.nio.ByteBuffer;
import jdk.dio.DeviceManager;
import jdk.dio.uart.UART;
import jdk.dio.uart.UARTConfig;
import jdk.dio.uart.UARTEventListener;

/**
 *
 * @author yjkim
 */
public class UARTRPi {
    public UART device = null;
    
    public UARTRPi(String controllerName) throws IOException {
        if (controllerName == null)
            controllerName = "ttyAMA0";
        
        UARTConfig config = new UARTConfig.Builder()
           .setControllerName(controllerName)
           .setChannelNumber(1)
           .setBaudRate(115200)
           .setDataBits(UARTConfig.DATABITS_8)
           .setStopBits(UARTConfig.STOPBITS_1)
           .setParity(UARTConfig.PARITY_NONE)
           .setFlowControlMode(UARTConfig.FLOWCONTROL_NONE)
           .build();        
        
        device = (UART)DeviceManager.open(config);  
    }
    
    public void close() throws IOException {
        if (device != null)
            device.close();
    }
    
    public void setEventListener(int eventType, UARTEventListener listener) throws IOException {
        device.setEventListener(eventType, listener);        
    }
    
    public void send(String data) throws IOException {
        ByteBuffer out = ByteBuffer.allocateDirect(data.length());
        out.put(data.getBytes());
        out.clear();
        device.write(out);
    }

    public void send(byte[] data, int length) throws IOException {
        ByteBuffer out = ByteBuffer.allocateDirect(length);
        out.put(data);
        out.clear();
        device.write(out);
    }

    public void send(ByteBuffer data) throws IOException {
        device.write(data);
    }
    
    public int readData(ByteBuffer buf) throws IOException {
        return device.read(buf);
    }
}
