package com.codecool.login;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class Logout implements HttpHandler {
    private final DB db;

    public Logout(DB db) {
        this.db = db;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestURI = exchange.getRequestURI().toString();
        System.out.println(requestURI);

        String cookieValue = exchange.getRequestHeaders().getFirst("Cookie")
                            .replace("\"", "")
                            .replace("sessionId=", "");
        System.out.println(cookieValue);
        int sessionId = Integer.parseInt(cookieValue);
        System.out.println(sessionId);
        db.getSessionUserMap().remove(sessionId);
        String cookie = exchange.getRequestHeaders().getFirst("Cookie") + ";Max-age=0";
        exchange.getResponseHeaders().set("Set-Cookie", cookie);
        System.out.println("removed cookie");

        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Location", "login");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }
}

