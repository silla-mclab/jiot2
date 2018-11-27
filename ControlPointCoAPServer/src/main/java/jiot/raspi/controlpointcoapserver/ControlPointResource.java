/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiot.raspi.controlpointcoapserver;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Observable;
import java.util.Observer;
import jiot.raspi.thing.ControlPoint;
import jiot.raspi.thing.OutputControlPoint;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 *
 * @author yjkim
 */
public class ControlPointResource extends CoapResource {
    private ControlPoint point = null;
    
    public ControlPointResource(ControlPoint point) {
        super(String.valueOf(point.getId()));
        this.point = point;
        addChildResources();
    }

    private String stringifyJsonWithSingleProperty(String name, String value) {
        JsonObject object = new JsonObject();
        object.addProperty(name, value);
        return (new Gson()).toJson(object);
    }
    
    private String getPropertyFromJson(String json, String prop) {
	JsonParser parser = new JsonParser();
	JsonElement element = parser.parse(json);
	return element.getAsJsonObject().get(prop).getAsString();
    }
    
    private void addChildResources() {
        add(new CoapResource("properties") {
            private CoapResource initialize() {
                setObservable(true);
                setObserveType(CoAP.Type.CON);
                getAttributes().setObservable();

                point.addObserver(new Observer() {
                    @Override
                    public void update(Observable o, Object arg) {
                        if (arg != null) {
                                changed();
                        }
                    }
                });
                return this;
            }

            @Override
            public void handleGET(CoapExchange exchange) {
                System.out.println("[DEBUG] GET " + getURI());
                JsonObject object = new JsonObject();
                object.addProperty("Id", point.getId());
                object.addProperty("Type", point.getType().name());
                object.addProperty("Name", point.getName());
                object.addProperty("Enabled", point.isEnabled());
                String response = (new Gson()).toJson(object);
                exchange.respond(response);
                System.out.println("[DEBUG] Response: " + response);
            }

            @Override
            public void handlePOST(CoapExchange exchange) {
                String jsonStr = exchange.getRequestText();
                String response = null;
                if (jsonStr != null) {
                    System.out.println("[DEBUG] POST " + getURI() + " " + jsonStr);
                    try {
                        point.setName(getPropertyFromJson(jsonStr, "Name"));
                        response = stringifyJsonWithSingleProperty("result", "true");
                    } catch (Throwable ex) {
                        response = stringifyJsonWithSingleProperty("result", 
                                "Exception occured : " + ex.getMessage());
                    }
                }
                else {
                    response = stringifyJsonWithSingleProperty("result", "false");
                }
                exchange.respond(response);
                System.out.println("[DEBUG] Response: " + (response != null ? response : "Error"));
            }
        }.initialize());

        add(new CoapResource("value") {
            private CoapResource initialize() {
                setObservable(true);
                setObserveType(CoAP.Type.CON);
                getAttributes().setObservable();

                point.addObserver(new Observer() {
                    @Override
                    public void update(Observable o, Object arg) {
                        if (arg == null) {
                            changed();
                        }
                    }
                });
                return this;
            }

            @Override
            public void handleGET(CoapExchange exchange) {
                System.out.println("[DEBUG] GET " + getURI());
                String response = stringifyJsonWithSingleProperty("value", String.valueOf(point.getPresentValue()));
                exchange.respond(response);;
                System.out.println("[DEBUG] Response: " + response);
            }

            @Override
            public void handlePOST(CoapExchange exchange) {
                String response = null;
                String jsonStr = exchange.getRequestText();
                System.out.println("[DEBUG] POST " + getURI() + " " + jsonStr);
                if ((point instanceof OutputControlPoint) && (jsonStr != null)) {
                    try {
                        int presentValue = Integer.parseInt(getPropertyFromJson(jsonStr, "value"));
                        ((OutputControlPoint)point).setPresentValue(presentValue);
                        response = stringifyJsonWithSingleProperty("result", "true");
                    } catch (Throwable ex) {
                        response = stringifyJsonWithSingleProperty("result", 
                                "Exception occured : " + ex.getMessage());
                    }
                }
                else {
                    response = stringifyJsonWithSingleProperty("result", "false");
                }
                exchange.respond(response);
                System.out.println("[DEBUG] Response: " + (response != null ? response : "Error"));
            }
        }.initialize());
    }    
}
