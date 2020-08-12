package com.codecool.login.cookies;

import com.codecool.login.helpers.LoginHelper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CookieHelper {

    public List<HttpCookie> parseCookies(String cookieString){
        List<HttpCookie> cookies = new ArrayList<>();
        if(cookieString == null || cookieString.isEmpty()){ // what happens if cookieString = null?
            return cookies;
        }
        for(String cookie : cookieString.split(";")){
            int indexOfEq = cookie.indexOf('=');
            String cookieName = cookie.substring(0, indexOfEq);
            String cookieValue = cookie.substring(indexOfEq + 1, cookie.length());
            cookies.add(new HttpCookie(cookieName, cookieValue));
        }
        return cookies;
    }

    public Optional<HttpCookie> findCookieByName(String name, List<HttpCookie> cookies){
        for(HttpCookie cookie : cookies){
            if(cookie.getName().equals(name))
                return Optional.ofNullable(cookie);
        }
        return Optional.empty();
    }

    public void removeCookieByName(String cookieName, HttpExchange httpExchange) throws IOException {
        String response = "logged out";
        HttpCookie cookie;
        cookie = new HttpCookie(cookieName, "");
        System.out.println(cookieName);
        cookie.setMaxAge(0);
        httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
        System.out.println("removed cookie");
        new LoginHelper().send200(httpExchange, response);
    }
}