/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uart_dev;

import java.io.IOException;

/**
 *
 * @author yjkim
 */
public enum SHT11 {

    /**
     * communication commands for SHT11
     */
    GET_TMP("TMP\n"),
    GET_HMD("HMD\n"),
    CHK_ACK("ACK\n");
            
    /**
     *
     */
    public String cmd;

    private SHT11(String cmd) {
        this.cmd = cmd;
    }
    
    public void send(UARTRPi uart) throws IOException {
        uart.send(this.cmd);
    }
    
}
