package com.codecool.login;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class App 
{
    public static void main( String[] args ) throws IOException {
        DB db = new DB();
        HttpServer server = HttpServer.create(new InetSocketAddress(9000), 0);
        server.createContext("/login", new Login(db));
        server.createContext("/logout", new Logout(db));
        server.createContext("/static", new Static());
        server.setExecutor(null);
        server.start();
    }
}
