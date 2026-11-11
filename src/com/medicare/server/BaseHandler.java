package com.medicare.server;

import com.medicare.model.User;
import com.medicare.util.Json;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract parent for all API handlers.
 * It checks login and role first, then calls doGet/doPost/doDelete.
 * Subclasses override only what they need (polymorphism).
 */
public abstract class BaseHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange ex) throws IOException {
        try {
            User user = SessionManager.getUser(ex);
            String method = ex.getRequestMethod();

            if (requiresLogin()) {
                if (user == null) { send(ex, 401, "{\"error\":\"Please log in\"}"); return; }
                if (!allowed(user, method)) {
                    send(ex, 403, "{\"error\":\"Your role can't do this\"}");
                    return;
                }
            }

            String result;
            if (method.equals("GET")) result = doGet(ex);
            else if (method.equals("POST")) result = doPost(ex);
            else if (method.equals("DELETE")) result = doDelete(ex);
            else { send(ex, 405, "{\"error\":\"Method not allowed\"}"); return; }
            send(ex, 200, result);
        } catch (UnsupportedOperationException e) {
            send(ex, 405, "{\"error\":" + Json.quote(e.getMessage()) + "}");
        } catch (SecurityException e) {
            send(ex, 401, "{\"error\":" + Json.quote(e.getMessage()) + "}");
        } catch (IllegalArgumentException e) {
            send(ex, 400, "{\"error\":" + Json.quote(e.getMessage()) + "}");
        } catch (Exception e) {
            send(ex, 500, "{\"error\":" + Json.quote(e.getMessage()) + "}");
        }
    }

    /** Login and logout handlers override this to return false. */
    protected boolean requiresLogin() { return true; }

    /**
     * Default rules: everyone can view, doctors can't add records,
     * only the admin can delete. Subclasses can override.
     */
    protected boolean allowed(User user, String method) {
        if (method.equals("DELETE")) return user.getRole().equals("ADMIN");
        if (method.equals("POST")) return !user.getRole().equals("DOCTOR");
        return true;
    }

    protected User currentUser(HttpExchange ex) { return SessionManager.getUser(ex); }

    protected String doGet(HttpExchange ex) throws Exception {
        throw new UnsupportedOperationException("GET not supported here");
    }
    protected String doPost(HttpExchange ex) throws Exception {
        throw new UnsupportedOperationException("POST not supported here");
    }
    protected String doDelete(HttpExchange ex) throws Exception {
        throw new UnsupportedOperationException("DELETE not supported here");
    }

    protected Map<String, String> readForm(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return parse(body);
    }

    protected Map<String, String> readQuery(HttpExchange ex) {
        String q = ex.getRequestURI().getRawQuery();
        return parse(q == null ? "" : q);
    }

    private Map<String, String> parse(String s) {
        Map<String, String> map = new HashMap<>();
        for (String pair : s.split("&")) {
            if (pair.isEmpty()) continue;
            String[] kv = pair.split("=", 2);
            String k = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String v = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            map.put(k, v);
        }
        return map;
    }

    protected void send(HttpExchange ex, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }
}
