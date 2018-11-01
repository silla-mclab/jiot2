/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiot05.mcp3208polling;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.spibus.SPIDevice;

/**
 *
 * @author yjkim
 */
public class MCP3208Polling implements Runnable {

    private static final int CMD_BIT = 0x060000;        // 24 bit command data
    
    private SPIDevice spiDev;
    private GPIOPin ssPin;

    public MCP3208Polling(String spiId, String ssPinId) throws IOException {
        this.spiDev = DeviceManager.open(spiId, SPIDevice.class);
        this.ssPin = DeviceManager.open(ssPinId, GPIOPin.class);
    }

    @Override
    public void run() {
        try {
            System.out.println("Channel(0): " + analogRead(0));
            System.out.println("Channel(1): " + analogRead(1));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }
    
    public int analogRead(int channel) {
        ByteBuffer out = ByteBuffer.allocate(3);
        ByteBuffer in = ByteBuffer.allocate(3);
        
        channel = (channel << 14) | CMD_BIT;
        out.put((byte)((channel >> 16) & 0xff));
        out.put((byte)((channel >> 8) & 0xff));
        out.put((byte)(channel & 0xff));
        out.flip();
        
        try {
//            ssPin.setValue(false);      // CS - Active Low
            spiDev.writeAndRead(out, in);
//            ssPin.setValue(true);
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
    
    public void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture future = executor.scheduleWithFixedDelay(this, 0, 1, TimeUnit.SECONDS);
        executor.schedule(new Runnable() { 
            @Override
            public void run() {
                future.cancel(false);
                executor.shutdown();
                try {
                    close();
                } catch (IOException ex) {
                    Logger.getLogger(MCP3208Polling.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 20, TimeUnit.SECONDS);
    }
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MCP3208Polling adcDev;
        try {
            adcDev = new MCP3208Polling("SPI0.1", "GPIO7");
            adcDev.start();
        } catch (IOException ex) {
            Logger.getLogger(MCP3208Polling.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
