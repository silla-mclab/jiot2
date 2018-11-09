package jiot.raspi.spi_dev;

/**
 * Functions to read and write to I2C Raspberry Pi bus
 * 
 * @author jcruz
 */
public class SPIUtils {

    /**
     *
     * @param mili
     */
    public static void SPIdelay(int mili) {
        try {
            Thread.sleep(mili);
        } catch (InterruptedException ex) {
        }
    }

    /**
     *
     * @param mili
     * @param nano
     */
    public static void SPIdelayNano(int mili, int nano) {
        try {
            Thread.sleep(mili, nano);
        } catch (InterruptedException ex) {
        }
    }

    /**
     *
     * @param b
     * @return byte values from -127..128 convert 128..255
     */
    public static int asInt(byte b) {
        int i = b;
        if (i < 0) {
            i += 256;
        }
        return i;
    }

}
