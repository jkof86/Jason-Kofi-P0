package com.revature.controllers;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


//This class holds the methods to handle http requests
public class Controller implements HttpHandler{

    //Handle method will execute once I receive the right request
    @Override
    public void handle(HttpExchange exchange) throws IOException {
       
        String someResponse = "This is a response using Strings!";
        String someResponse2 = "Sorry, we're only accepting \"GET\" requests at the moment.";

        String httpVerb = exchange.getRequestMethod();
        

        switch (httpVerb){
            case "GET":
            exchange.getRequestBody();
            exchange.sendResponseHeaders(200, someResponse.getBytes().length);

            //we have to save the string into a class that httpServer can handle
            OutputStream os = exchange.getResponseBody();
            os.write(someResponse.getBytes()); //writing inside the response body
            os.close();
            break;

            default:
            exchange.getRequestBody();
            exchange.sendResponseHeaders(400, someResponse2.getBytes().length);

            OutputStream os2 = exchange.getResponseBody();
            os2.write(someResponse2.getBytes()); //writing inside the response body
            os2.close();
            break;
        }
    }
    
}
