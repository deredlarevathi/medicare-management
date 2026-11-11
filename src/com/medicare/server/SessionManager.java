package com.medicare.server;

import com.medicare.model.User;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Keeps logged-in users in memory, keyed by a random token stored in a cookie. */
public class SessionManager {
    public static final String COOKIE = "MEDISESSION";
    private static final Map<String, User> sessions = new ConcurrentHashMap<>();

    private SessionManager() { }

    public static String create(User user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }

    public static void remove(String token) {
        if (token != null) sessions.remove(token);
    }

    public static User getUser(HttpExchange ex) {
        String token = getToken(ex);
        return token == null ? null : sessions.get(token);
    }

    public static String getToken(HttpExchange ex) {
        List<String> headers = ex.getRequestHeaders().get("Cookie");
        if (headers == null) return null;
        for (String header : headers) {
            for (String part : header.split(";")) {
                String[] kv = part.trim().split("=", 2);
                if (kv.length == 2 && kv[0].equals(COOKIE)) return kv[1];
            }
        }
        return null;
    }
}
