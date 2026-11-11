package com.medicare.server;

import com.medicare.dao.UserDAO;
import com.medicare.model.User;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class LoginHandler extends BaseHandler {
    private final UserDAO dao = new UserDAO();

    @Override
    protected boolean requiresLogin() { return false; }

    @Override
    protected String doPost(HttpExchange ex) throws Exception {
        Map<String, String> f = readForm(ex);
        User user = dao.authenticate(f.get("username"), f.get("password"));
        if (user == null) throw new SecurityException("Wrong username or password");

        String token = SessionManager.create(user);
        ex.getResponseHeaders().add("Set-Cookie",
                SessionManager.COOKIE + "=" + token + "; Path=/; HttpOnly; SameSite=Strict");
        return user.toJson();
    }
}
