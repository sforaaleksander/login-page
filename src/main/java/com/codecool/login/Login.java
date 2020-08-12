package com.codecool.login;

import com.codecool.login.cookies.CookieHelper;
import com.codecool.login.helpers.LoginHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Login implements HttpHandler {
    private static final String SESSION_COOKIE_NAME = "sessionId";
    int counter = 0;
    CookieHelper cookieHelper = new CookieHelper();
    LoginHelper loginHelper = new LoginHelper();
    DB db = new DB();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Optional<HttpCookie> cookie = getSessionIdCookie(httpExchange);
        String method = httpExchange.getRequestMethod();
        String requestURI = httpExchange.getRequestURI().toString();

//        if (!cookie.isPresent()) {
//            createCookie(httpExchange);
//        } else {
//            sayHello(httpExchange);
//        }
        System.out.println(requestURI);
        if(requestURI.contains("logout")){
           cookieHelper.removeCookieByName(SESSION_COOKIE_NAME, httpExchange);
           loginHelper.redirectHome(httpExchange);
        }
        if (method.equals("POST")) {
            postForm(httpExchange);
        }
        else if (method.equals("GET") && cookie.isPresent()) {
            int sessionId = Integer.parseInt(cookie.get().getValue());
            System.out.println(sessionId);
            sayHello(httpExchange, sessionId);
        }
        else {
            getForm(httpExchange);
        }
    }

    private void sayHello(HttpExchange httpExchange, int sessionId) throws IOException {
        User user = getUserBySessionId(sessionId);
        modelPageWithName(httpExchange, user);
    }

    private void createCookie(HttpExchange httpExchange) {
        Optional<HttpCookie> cookie;
        String sessionId = String.valueOf(counter);
        cookie = Optional.of(new HttpCookie(SESSION_COOKIE_NAME, sessionId));
        httpExchange.getResponseHeaders().add("Set-Cookie", cookie.get().toString());
    }

    private void getForm(HttpExchange httpExchange) throws IOException {
        String response;
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/main-page.twig");
        JtwigModel model = JtwigModel.newModel();
        response = template.render(model);
        loginHelper.send200(httpExchange, response);
    }

    private void postForm(HttpExchange httpExchange) throws IOException {
        Map<String, String> inputs = loginHelper.getInputs(httpExchange);
        if (!areCredentialsValid(inputs)){
            invalidAlert(httpExchange);
            return;
        }
        User user = getUserByProvidedName(inputs.get("username"));
        modelPageWithName(httpExchange, user);
    }

    private void modelPageWithName(HttpExchange httpExchange, User user) throws IOException {
        String response;
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/logged-page.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("username", user.getUserName());
        createCookie(httpExchange);
        response = template.render(model);
        loginHelper.send200(httpExchange, response);
    }

    private void invalidAlert(HttpExchange httpExchange) throws IOException {
        String response = "Invalid login data";
        loginHelper.send200(httpExchange, response);
    }

    private boolean areCredentialsValid(Map<String, String> inputs) {
        String providedName = inputs.get("username");
        String providedPassword = inputs.get("password");
        User user = getUserByProvidedName(providedName);
        return (user != null) && user.getPassword().equals(providedPassword);
    }

    private User getUserByProvidedName(String providedName) {
        List<User> users = db.getUserList();
        return users.stream().filter(u -> u.getUserName().equals(providedName)).findFirst().orElse(null);
    }

    private User getUserBySessionId(int sessionId) {
        List<User> users = db.getUserList();
        return users.stream().filter(u -> u.getSessionId() == sessionId).findFirst().orElse(null);
    }


    private Optional<HttpCookie> getSessionIdCookie(HttpExchange httpExchange){
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
        List<HttpCookie> cookies = cookieHelper.parseCookies(cookieStr);
        return cookieHelper.findCookieByName(SESSION_COOKIE_NAME, cookies);
    }
}
