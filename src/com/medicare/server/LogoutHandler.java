package com.medicare.server;

import com.sun.net.httpserver.HttpExchange;

public class LogoutHandler extends BaseHandler {

    @Override
    protected boolean requiresLogin() { return false; }

    @Override
    protected String doPost(HttpExchange ex) throws Exception {
        SessionManager.remove(SessionManager.getToken(ex));
        ex.getResponseHeaders().add("Set-Cookie",
                SessionManager.COOKIE + "=; Path=/; HttpOnly; Max-Age=0");
        return "{\"ok\":true}";
    }
}
