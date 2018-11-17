/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiot.raspi.HttpService;

import java.util.Observable;
import java.util.Observer;
import javax.annotation.PreDestroy;
import jiot.raspi.thing.ControlPoint;
import jiot.raspi.thing.ControlPointContainer;
import jiot.raspi.thing.OutputControlPoint;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author yjkim
 */
@RestController
public class RaspiIotController implements Observer {
    private ControlPointContainer cpContainer = null;
    
    public RaspiIotController() {
    	cpContainer = ControlPointContainer.getInstance();
        cpContainer.start();

        for(ControlPoint cp:cpContainer.getControlPoints()){
            cp.addObserver(this);
        }
        
        System.out.println(">>> Start ControlPointContainer and register Observer to Control Points...");
    }
   
    @PreDestroy
    public void close() {
        if (cpContainer != null) {
            cpContainer.stop();
            System.out.println(">>> Stop ControlPointContainer...");
        }
    }

    @Override
    public void update(Observable obj, Object arg) {
        if (obj instanceof ControlPoint) {
            ControlPoint point = (ControlPoint) obj;
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
    
    private void write(String msg) {
        System.out.println(msg);
    }
    
    @RequestMapping(value="/ledOn/{ledId}", method=RequestMethod.GET)
    @ResponseBody
    public String turnOnLed(@PathVariable("ledId") String ledId) {
        int ledNum = Integer.parseInt(ledId);
        if (ledNum < 1 || ledNum > 3) {
            return "LED id parameter is wrong. LED id must be from 1 to 3.";
        }
        else {
            int ledCPId = ControlPointContainer.ControlPoints.LED1.getId() + (ledNum-1);
            OutputControlPoint ledCP = (OutputControlPoint)cpContainer.getControlPoint(ledCPId);
            ledCP.setPresentValue(1);
            return ("LED #" + ledId + " is turn on.");
        }
    }
    
    @RequestMapping(value="/ledOff/{ledId}", method=RequestMethod.GET)
    @ResponseBody
    public String turnOffLed(@PathVariable("ledId") String ledId) {
        int ledNum = Integer.parseInt(ledId);
        if (ledNum < 1 || ledNum > 3) {
            return "LED id parameter is wrong. LED id must be from 1 to 3.";
        }
        else {
            int ledCPId = ControlPointContainer.ControlPoints.LED1.getId() + (ledNum-1);
            OutputControlPoint ledCP = (OutputControlPoint)cpContainer.getControlPoint(ledCPId);
            ledCP.setPresentValue(2);
            return ("LED #" + ledId + " is turn off.");
        }
    }
    
}
