package jiot.raspi.thing;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import jiot.raspi.ext_dev.UARTCommPoint;
import jiot.raspi.ext_dev.DustSensor;
import jiot.raspi.ext_dev.VantilationFan;

public class ControlPointContainer {

    private static AtomicReference<ControlPointContainer> instance = 
                new AtomicReference<ControlPointContainer>();

    public static ControlPointContainer getInstance() {
        if (instance.get() == null) {
                instance.set(new ControlPointContainer());
        }
        return instance.get();
    }

    private Map<Integer, ControlPoint> controlPoints = new HashMap<Integer, ControlPoint>();

    protected ControlPointContainer() {		
    }
	
    private void createControlPoints() {
        ControlPoint point = new GPIOPinOutputControlPoint(17);     // LED1
        controlPoints.put(point.getId(), point);

        point = new GPIOPinOutputControlPoint(27);      // LED2
        controlPoints.put(point.getId(), point);
        
        point = new GPIOPinOutputControlPoint(22);      // LED3
        controlPoints.put(point.getId(), point);
        
        point = new VantilationFan(5, 6);               // Valtilation Fan
        controlPoints.put(point.getId(), point);
        
        point = new GPIOPinControlPoint(23);            // Button1
        controlPoints.put(point.getId(), point);
        
        point = new GPIOPinControlPoint(24);            // Button2
        controlPoints.put(point.getId(), point);
        
        point = new GPIOPinControlPoint(25);            // PIR Motion Sensor
        controlPoints.put(point.getId(), point);

        point = new AnalogInputPoint(0);                // CDR sensor
        controlPoints.put(point.getId(), point);

        point = new DustSensor(1, 18);                  // Dust Sensor(Analog#1, GPIO18)
        controlPoints.put(point.getId(), point);

        point = new UARTCommPoint();                    // SHT11
        controlPoints.put(point.getId(), point);
    }

    public void start() {
        createControlPoints();

        for (ControlPoint cp : controlPoints.values()) {
            cp.open();
        }
    }
	
    public void stop() {
        for (ControlPoint cp : controlPoints.values()) {
            cp.close();
        }
        controlPoints.clear();
        ControlPoint.POLLING.shutdown();
    }
	
    public Collection<ControlPoint> getControlPoints() {
        return Collections.unmodifiableCollection(controlPoints.values());
    }

    public ControlPoint getControlPoint(int pointId) {
        return controlPoints.get(pointId);
    }
	
    public void addControlPoint(ControlPoint cp) {
        controlPoints.put(cp.getId(), cp);
        if (!cp.isEnabled()) {
            cp.open();
        }        
    }
    
    public enum ControlPoints {
        LED1(0),
        LED2(1),
        LED3(2),
        FAN(3),
        BTN1(4),
        BTN2(5),
        PIR(6),
        CDR(7),
        DUST(8),
        SHT11(9);

        /**
         *
         */
        private int id;

        private ControlPoints(int id) {
            this.id = id;
        }
        
        public int getId() {
            return this.id;
        }
    }
}