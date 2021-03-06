package com.codecool.login;

import com.codecool.login.helpers.CookieHelper;
import com.codecool.login.helpers.LoginHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.HttpCookie;
import java.util.Map;
import java.util.Optional;

public class Login implements HttpHandler {
    private static final String SESSION_COOKIE_NAME = "sessionId";
    private static int counter = 0;
    private final CookieHelper cookieHelper;
    private final LoginHelper loginHelper;
    private final DB db;

    public Login(DB db) {
        this.db = db;
        this.cookieHelper = new CookieHelper();
        this.loginHelper = new LoginHelper();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals("POST")) {
            postForm(httpExchange);
        } else if (method.equals("GET")) {
            handleGet(httpExchange);
        }
    }

    private void handleGet(HttpExchange httpExchange) throws IOException {
        Optional<HttpCookie> optionalCookie = cookieHelper.getSessionIdCookie(httpExchange, SESSION_COOKIE_NAME);
        if (optionalCookie.isPresent()) {
            int sessionId = cookieHelper.getSessionIdFromCookie(optionalCookie.get());
            sayHello(httpExchange, sessionId);
        } else {
            getForm(httpExchange);
        }
    }

    private void sayHello(HttpExchange httpExchange, int sessionId) throws IOException {
        User user = db.getUserBySessionId(sessionId);
        modelPageWithName(httpExchange, user);
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
        if (!loginHelper.areCredentialsValid(inputs, db)) {
            loginHelper.invalidAlert(httpExchange);
            return;
        }
        String sessionId = String.valueOf(counter);
        cookieHelper.createCookie(httpExchange, SESSION_COOKIE_NAME, sessionId);
        User user = db.getUserByProvidedName(inputs.get("username"));
        db.getSessionUserMap().put(counter, user);
        modelPageWithName(httpExchange, user);
        counter++;
    }

    private void modelPageWithName(HttpExchange httpExchange, User user) throws IOException {
        String response;
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/logged-page.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("username", user.getUserName());
        response = template.render(model);
        loginHelper.send200(httpExchange, response);
    }
}
