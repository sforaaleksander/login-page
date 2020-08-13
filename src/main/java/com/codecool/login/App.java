package com.codecool.login;

//import com.codecool.login.cookies.CookieHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class App 
{
    public static void main( String[] args ) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(9000), 0);
        server.createContext("/login", new Login());
        server.createContext("/logout", new Logout());
//        server.createContext("/cookie", new CookieHandler());
        server.createContext("/static", new Static());
        server.setExecutor(null);
        server.start();

    }
}
