package jiot.raspi.cli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.Channels;
import java.util.Observable;
import java.util.Observer;
import jdk.dio.DeviceManager;

import jdk.dio.uart.UART;
import jdk.dio.uart.UARTConfig;
import jiot.raspi.thing.ControlPoint;

public class CLIConsole implements Observer {

    private UART uart = null;
    private BufferedReader in;
    private BufferedWriter out;
    private boolean change_log = false;

    public CLIConsole(UARTConfig config) throws IOException {
        if (config != null) {
            uart = (UART) DeviceManager.open(config);
            in = new BufferedReader(Channels.newReader(uart, "UTF-8"));
            out = new BufferedWriter(Channels.newWriter(uart, "UTF-8"));
            uart.setReceiveTimeout(100);
        }
        else {
            in = new BufferedReader(new InputStreamReader(System.in));
            out = new BufferedWriter(new OutputStreamWriter(System.out));
        }
    }

    public void run() throws IOException {
        System.out.println("Waiting command...");
        write("Please input command: ");
        
        CommandInterpreter interpreter = CommandInterpreter.getInstance();
        
        for (String line = in.readLine();
                line == null || (!line.equals("quit") && !line.equals("exit"));
                line = in.readLine()) {
            if(line == null)
                continue;
            
            System.out.println("Received message: " + line);
            String[] command = line.split(" ");
            String result;
            try {
                result = interpreter.execute(command);
            } catch (Throwable ex) {
                result = "Exception happend: " + ex.getMessage();
            }
            
            if(result != null)
                write(result);
            else 
            	write("");
        }

        write("Good bye!");
        close();
    }
    
    public void setChangeLogDisplsy(boolean display) {
        change_log = display;
    }

    @Override
    public void update(Observable ob, Object arg) {
        if (ob instanceof ControlPoint && change_log == true) {
            ControlPoint point = (ControlPoint) ob;
            if (arg == null) {
                write("[Observer] Changed value (" + point.getName() + "): "
                    + point.getPresentValue());
            }
            else {
                if (arg.toString().equals("name")) {
                    write("[Observer] Changed name (" + point.getName() + "): " + point.getName());
                }
                else {
                    write("[Observer] Changed (" + point.getName() + "): " + arg);
                }
            }
        }
    }

    private void write(String result) {
        try {
            out.write(result);
            out.newLine();
            out.write("Console >> ");
            out.flush();
        } catch (IOException ex) {

        }
    }

    private void close() throws IOException {
        in.close();
        out.close();
        if (uart != null) uart.close();
    }
}
