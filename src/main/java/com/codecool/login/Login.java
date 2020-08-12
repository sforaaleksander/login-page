package com.codecool.login;

import com.codecool.login.cookies.CookieHelper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Login implements HttpHandler {
    private static final String SESSION_COOKIE_NAME = "sessionId";
    int counter = 0;
    CookieHelper cookieHelper = new CookieHelper();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Optional<HttpCookie> cookie = getSessionIdCookie(httpExchange);
        String method = httpExchange.getRequestMethod();
        String requestURI = httpExchange.getRequestURI().toString();

        if (!cookie.isPresent()) {
            String sessionId = String.valueOf(counter); // This isn't a good way to create sessionId. Find a better one!
            cookie = Optional.of(new HttpCookie(SESSION_COOKIE_NAME, sessionId));
            httpExchange.getResponseHeaders().add("Set-Cookie", cookie.get().toString());
        }

        if (method.equals("POST")) {
            postForm(httpExchange);
        }
        else if (method.equals("GET")) {
            getForm(httpExchange);
        }
    }

    private void getForm(HttpExchange httpExchange) throws IOException {
        String response;
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/main-page.twig");
        JtwigModel model = JtwigModel.newModel();
        response = template.render(model);
        send200(httpExchange, response);
    }

    private void postForm(HttpExchange httpExchange) throws IOException {
        String response;
        Map<String, String> inputs = getInputs(httpExchange);
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/logged-page.twig");
        JtwigModel model = JtwigModel.newModel();
        String userName = inputs.get("username");
        System.out.println(userName);
        model.with("username", userName);
        response = template.render(model);
        send200(httpExchange, response);
    }


    private void send200(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private Map<String, String> getInputs(HttpExchange httpExchange) throws IOException {
        String formData = getFormData(httpExchange);
        return parseFormData(formData);
    }

    private String getFormData(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        return br.readLine();
    }

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            // We have to decode the value because it's urlencoded. see: https://en.wikipedia.org/wiki/POST_(HTTP)#Use_for_submitting_web_forms
            String key = URLDecoder.decode(keyValue[0], "UTF-8");
            String value = URLDecoder.decode(keyValue[1], "UTF-8");
            map.put(key, value);
        }
        return map;
    }

    private Optional<HttpCookie> getSessionIdCookie(HttpExchange httpExchange){
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
        List<HttpCookie> cookies = cookieHelper.parseCookies(cookieStr);
        return cookieHelper.findCookieByName(SESSION_COOKIE_NAME, cookies);
    }

    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
