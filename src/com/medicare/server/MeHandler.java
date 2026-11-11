package com.medicare.server;

import com.sun.net.httpserver.HttpExchange;

/** Tells the browser who is logged in. */
public class MeHandler extends BaseHandler {

    @Override
    protected String doGet(HttpExchange ex) {
        return currentUser(ex).toJson();
    }
}
