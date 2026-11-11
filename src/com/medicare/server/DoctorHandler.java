package com.medicare.server;

import com.medicare.dao.DoctorDAO;
import com.medicare.model.Doctor;
import com.medicare.util.Json;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class DoctorHandler extends BaseHandler {
    private final DoctorDAO dao = new DoctorDAO();

    @Override
    protected String doGet(HttpExchange ex) throws Exception {
        return Json.array(dao.findAll());
    }

    @Override
    protected String doPost(HttpExchange ex) throws Exception {
        Map<String, String> f = readForm(ex);
        dao.add(new Doctor(0, f.get("name"), f.get("specialization"), f.get("phone")));
        return "{\"ok\":true}";
    }

    @Override
    protected String doDelete(HttpExchange ex) throws Exception {
        dao.delete(Integer.parseInt(readQuery(ex).get("id")));
        return "{\"ok\":true}";
    }
}
