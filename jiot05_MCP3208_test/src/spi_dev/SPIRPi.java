package spi_dev;

import java.io.IOException;
import java.nio.ByteBuffer;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.spibus.SPIDevice;
import jdk.dio.spibus.SPIDeviceConfig;

/**
 * Base definitions for create a device and its config
 *
 */
public class SPIRPi {
    /**
     * Save device address establishing
     */
    private SPIDeviceConfig spiConfig;
    private GPIOPinConfig gpioConfig;

    public SPIDevice device = null;
    public GPIOPin ssPin = null;
    
    public static final int CE0=0; // SPI address 0 CE0
    public static final int CE1=1; // SPI address 1 CE1
    
    /**
     * Define device and config it
     *
     * @param address
     * @param bitOrder
     * @throws IOException
     */
    public SPIRPi(int address, int bitOrder) throws IOException {
        //depredicated:
        //public SPIDeviceConfig(int controllerNumber, int address, int csActive, int clockFrequency, int clockMode, int wordLength, int bitOrdering) {
        //config = new SPIDeviceConfig(0, address, SPIDeviceConfig.CS_ACTIVE_LOW, 2000000, 0, 8, Device.BIG_ENDIAN);
        spiConfig = new SPIDeviceConfig.Builder()
           .setControllerNumber(0)
           .setAddress(address)
           .setCSActiveLevel(SPIDeviceConfig.CS_ACTIVE_LOW)
           .setClockFrequency(2000000)
           .setClockMode(1)
           .setWordLength(8)
           .setBitOrdering(bitOrder)
           .build();
        device = DeviceManager.open(spiConfig);
        
        gpioConfig = new GPIOPinConfig.Builder()
           .setControllerNumber(0)
           .setPinNumber((address == CE0) ? 8 : 7)
           .setDirection(GPIOPinConfig.DIR_OUTPUT_ONLY)
           .setDriveMode(GPIOPinConfig.MODE_OUTPUT_PUSH_PULL)
           .setInitValue(true)
           .build();
        ssPin = DeviceManager.open(gpioConfig);
    }

    /**
     * free device resource
     *
     */
    public void close() {
        try {
            device.close();
            ssPin.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void writeAndRead(ByteBuffer out, ByteBuffer in) throws IOException {
        ssPin.setValue(false);
        device.writeAndRead(out, in);
        ssPin.setValue(false);
    }
}
