package com.codecool.login;

import com.codecool.login.cookies.CookieHelper;
import com.codecool.login.helpers.LoginHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Optional<HttpCookie> cookie = getSessionIdCookie(httpExchange);
        String method = httpExchange.getRequestMethod();
        String requestURI = httpExchange.getRequestURI().toString();

        if (!cookie.isPresent()) {
            createCookie(httpExchange);
        }
        if (method.equals("POST")) {
            postForm(httpExchange);
        }
        else if (method.equals("GET")) {
            getForm(httpExchange);
        }
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
        String response;
        Map<String, String> inputs = loginHelper.getInputs(httpExchange);
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/logged-page.twig");
        JtwigModel model = JtwigModel.newModel();
        String userName = inputs.get("username");
        System.out.println(userName);
        model.with("username", userName);
        response = template.render(model);
        loginHelper.send200(httpExchange, response);
    }


    private Optional<HttpCookie> getSessionIdCookie(HttpExchange httpExchange){
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
        List<HttpCookie> cookies = cookieHelper.parseCookies(cookieStr);
        return cookieHelper.findCookieByName(SESSION_COOKIE_NAME, cookies);
    }
}
