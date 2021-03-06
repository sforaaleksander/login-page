package com.codecool.login.helpers;

import com.codecool.login.DB;
import com.codecool.login.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginHelper {

    public void send200(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public Map<String, String> getInputs(HttpExchange httpExchange) throws IOException {
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
            String key = URLDecoder.decode(keyValue[0], "UTF-8");
            String value = URLDecoder.decode(keyValue[1], "UTF-8");
            map.put(key, value);
        }
        return map;
    }

    public void invalidAlert(HttpExchange httpExchange) throws IOException {
        String response = "Invalid login data";
        send200(httpExchange, response);
    }

    public boolean areCredentialsValid(Map<String, String> inputs, DB db) {
        String providedName = inputs.get("username");
        String providedPassword = inputs.get("password");
        User user = db.getUserByProvidedName(providedName);
        return (user != null) && user.getPassword().equals(providedPassword);
    }
}
