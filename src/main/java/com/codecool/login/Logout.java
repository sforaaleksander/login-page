package com.codecool.login;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class Logout implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestURI = exchange.getRequestURI().toString();
        System.out.println(requestURI);

        String cookie = exchange.getRequestHeaders().getFirst("Cookie") + ";Max-age=0";
        exchange.getResponseHeaders().set("Set-Cookie", cookie);
        System.out.println("removed cookie");

        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Location", "login");
        try {
            exchange.sendResponseHeaders(302, -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        exchange.close();
    }
}

