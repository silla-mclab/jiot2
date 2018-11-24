/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiot.raspi.simplecoapclient;

import java.util.Scanner;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

/**
 *
 * @author yjkim
 */
public class HelloCoAPClient implements CoAPClient {
    private CoapClient client = null;
    
    @Override
    public void run(String hostName) {
        client = new CoapClient();
        
        CoapResponse response;
        String uri;
        
        Scanner input = new Scanner(System.in);
        System.out.print("Enter URI path(or 'q'): ");
        for (String line = input.nextLine(); !line.equals("q"); line = input.nextLine()) {
            // send GET request to CoAP server
            uri = "coap://" + hostName + ":5683" + line + "?text=everybody";
            client.setURI(uri);
            response = client.get();
            if (response != null) {
                System.out.println("code: " + response.getCode());
                System.out.println("options: " + response.getOptions());
                System.out.println("payload: " + Utils.toHexString(response.getPayload()));
                System.out.println("text: " + response.getResponseText());
                System.out.println("advanced: " + Utils.prettyPrint(response));
            }
            
            // send PUT request to CoAP server
            uri = "coap://" + hostName + ":5683" + line;
            client.setURI(uri);
            response = client.post("everybody", MediaTypeRegistry.TEXT_PLAIN);
            if (response != null) {
                System.out.println("Post: " + response.getResponseText());
            }

            System.out.print("Enter URI path(or 'q'): ");
        }
    }
}
