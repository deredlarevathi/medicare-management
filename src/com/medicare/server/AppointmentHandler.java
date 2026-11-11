package com.medicare.server;

import com.medicare.dao.AppointmentDAO;
import com.medicare.model.Appointment;
import com.medicare.model.User;
import com.medicare.util.Json;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class AppointmentHandler extends BaseHandler {
    private final AppointmentDAO dao = new AppointmentDAO();

    /** Doctors may also mark appointments completed, so only delete is admin-only. */
    @Override
    protected boolean allowed(User user, String method) {
        return !method.equals("DELETE") || user.getRole().equals("ADMIN");
    }

    @Override
    protected String doGet(HttpExchange ex) throws Exception {
        return Json.array(dao.findAll());
    }

    /** POST with an "id" field updates the status; otherwise it books a new appointment. */
    @Override
    protected String doPost(HttpExchange ex) throws Exception {
        Map<String, String> f = readForm(ex);
        if (f.containsKey("id")) {
            dao.updateStatus(Integer.parseInt(f.get("id")), f.get("status"));
        } else {
            dao.add(new Appointment(0, Integer.parseInt(f.get("patientId")),
                    Integer.parseInt(f.get("doctorId")), null, null,
                    f.get("date"), f.get("time"), "Scheduled"));
        }
        return "{\"ok\":true}";
    }

    @Override
    protected String doDelete(HttpExchange ex) throws Exception {
        dao.delete(Integer.parseInt(readQuery(ex).get("id")));
        return "{\"ok\":true}";
    }
}
