package com.medicare.model;

import com.medicare.util.Json;
import com.medicare.util.Jsonable;

import java.util.Locale;

public class Bill implements Jsonable {
    private int id;
    private int patientId;
    private String patientName;
    private double consultationFee;
    private double medicineCharges;
    private double otherCharges;
    private String status;
    private String paymentMethod;
    private String date;

    public Bill(int id, int patientId, String patientName, double consultationFee,
                double medicineCharges, double otherCharges, String status,
                String paymentMethod, String date) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.consultationFee = consultationFee;
        this.medicineCharges = medicineCharges;
        this.otherCharges = otherCharges;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.date = date;
    }

    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public double getConsultationFee() { return consultationFee; }
    public double getMedicineCharges() { return medicineCharges; }
    public double getOtherCharges() { return otherCharges; }
    public String getStatus() { return status; }

    /** The total is always calculated here, never trusted from the browser. */
    public double getTotal() {
        return consultationFee + medicineCharges + otherCharges;
    }

    private static String money(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    @Override
    public String toJson() {
        return "{\"id\":" + id
             + ",\"patientId\":" + patientId
             + ",\"patientName\":" + Json.quote(patientName)
             + ",\"consultation\":" + money(consultationFee)
             + ",\"medicine\":" + money(medicineCharges)
             + ",\"other\":" + money(otherCharges)
             + ",\"total\":" + money(getTotal())
             + ",\"status\":" + Json.quote(status)
             + ",\"method\":" + Json.quote(paymentMethod)
             + ",\"date\":" + Json.quote(date) + "}";
    }
}
