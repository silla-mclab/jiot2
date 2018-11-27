/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiot.raspi.controlpointcoapserver;

import java.util.Collection;
import jiot.raspi.thing.ControlPoint;
import jiot.raspi.thing.ControlPointContainer;
import org.eclipse.californium.core.CoapServer;

/**
 *
 * @author yjkim
 */
public class ControlPointCoAPServer extends CoapServer {
    private ControlPointContainer cpContainer = null;

    @Override
    public synchronized void stop() {
        if (cpContainer != null) 
            cpContainer.stop();
        
        super.stop(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void start() {
        cpContainer = ControlPointContainer.getInstance();
        cpContainer.start();

        Collection<ControlPoint> points = cpContainer.getControlPoints();
        for (ControlPoint point : points) {
                this.add(new ControlPointResource(point));
        }
        
        super.start(); //To change body of generated methods, choose Tools | Templates.
    } 
}
