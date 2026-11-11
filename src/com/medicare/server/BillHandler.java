package com.medicare.server;

import com.medicare.dao.BillDAO;
import com.medicare.model.Bill;
import com.medicare.model.User;
import com.medicare.util.Json;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class BillHandler extends BaseHandler {
    private final BillDAO dao = new BillDAO();

    /** Billing is for the admin and reception only; just the admin may delete. */
    @Override
    protected boolean allowed(User user, String method) {
        if (user.getRole().equals("DOCTOR")) return false;
        if (method.equals("DELETE")) return user.getRole().equals("ADMIN");
        return true;
    }

    @Override
    protected String doGet(HttpExchange ex) throws Exception {
        return Json.array(dao.findAll());
    }

    /** POST with an "id" marks that bill paid; otherwise it creates a new bill. */
    @Override
    protected String doPost(HttpExchange ex) throws Exception {
        Map<String, String> f = readForm(ex);
        if (f.containsKey("id")) {
            dao.markPaid(Integer.parseInt(f.get("id")), f.get("method"));
        } else {
            dao.add(new Bill(0, Integer.parseInt(f.get("patientId")), null,
                    amount(f.get("consultation")), amount(f.get("medicine")),
                    amount(f.get("other")), "Unpaid", null, null));
        }
        return "{\"ok\":true}";
    }

    @Override
    protected String doDelete(HttpExchange ex) throws Exception {
        dao.delete(Integer.parseInt(readQuery(ex).get("id")));
        return "{\"ok\":true}";
    }

    private double amount(String s) {
        double v = (s == null || s.isEmpty()) ? 0 : Double.parseDouble(s);
        if (v < 0) throw new IllegalArgumentException("Amounts can't be negative");
        return v;
    }
}
