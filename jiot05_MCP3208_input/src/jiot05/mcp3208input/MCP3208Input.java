/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiot05.mcp3208input;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.spibus.SPIDevice;

/**
 *
 * @author yjkim
 */
public class MCP3208Input {

    private static final int CMD_BIT = 0x060000;        // 24 bit command data
    
    private SPIDevice spiDev;
    private GPIOPin ssPin;

    public MCP3208Input(String spiId, String ssPinId) throws IOException {
        this.spiDev = DeviceManager.open(spiId, SPIDevice.class);
        this.ssPin = DeviceManager.open(ssPinId, GPIOPin.class);
    }

    public void run() throws InterruptedException, IOException {
        for(;;) {
            System.out.println("Channel(0): " + analogRead(0));
            System.out.println("Channel(1): " + analogRead(1));
            Thread.sleep(500);
        }
    }
    
    public int analogRead(int channel) throws IOException {
        ByteBuffer out = ByteBuffer.allocate(3);
        ByteBuffer in = ByteBuffer.allocate(3);
        
        channel = (channel << 14) | CMD_BIT;
        out.put((byte)((channel >> 16) & 0xff));
        out.put((byte)((channel >> 8) & 0xff));
        out.put((byte)(channel & 0xff));
        out.flip();
        
        try {
            ssPin.setValue(false);      // CS - Active Low
            spiDev.writeAndRead(out, in);
            ssPin.setValue(true);
        } catch (IOException ex) {
//            Logger.getLogger(MCP3208.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
        
        // extract low two bytes from three input bytes
        // and mask 12 bits
        int highByte = (int)(in.get(1) & 0x0f);
        int lowByte = (int)(in.get(2) & 0xff);
        
        return (highByte << 8) | lowByte;
    }
    
    public void close() throws IOException {
        ssPin.close();
        spiDev.close();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MCP3208Input adcDev;
        try {
            adcDev = new MCP3208Input("SPI0.1", "GPIO7");
            adcDev.run();
        } catch (IOException | InterruptedException  ex) {
            Logger.getLogger(MCP3208Input.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
