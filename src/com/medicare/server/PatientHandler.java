package com.medicare.server;

import com.medicare.dao.PatientDAO;
import com.medicare.model.Patient;
import com.medicare.util.Json;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class PatientHandler extends BaseHandler {
    private final PatientDAO dao = new PatientDAO();

    @Override
    protected String doGet(HttpExchange ex) throws Exception {
        return Json.array(dao.findAll());
    }

    @Override
    protected String doPost(HttpExchange ex) throws Exception {
        Map<String, String> f = readForm(ex);
        dao.add(new Patient(0, f.get("name"), Integer.parseInt(f.get("age")),
                f.get("gender"), f.get("phone"), f.get("address")));
        return "{\"ok\":true}";
    }

    @Override
    protected String doDelete(HttpExchange ex) throws Exception {
        dao.delete(Integer.parseInt(readQuery(ex).get("id")));
        return "{\"ok\":true}";
    }
}
