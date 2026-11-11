package com.medicare.model;

import com.medicare.util.Json;
import com.medicare.util.Jsonable;

public class Appointment implements Jsonable {
    private int id;
    private int patientId;
    private int doctorId;
    private String patientName;
    private String doctorName;
    private String date;
    private String time;
    private String status;

    public Appointment(int id, int patientId, int doctorId, String patientName,
                       String doctorName, String date, String time, String status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }

    @Override
    public String toJson() {
        return "{\"id\":" + id
             + ",\"patientId\":" + patientId
             + ",\"doctorId\":" + doctorId
             + ",\"patientName\":" + Json.quote(patientName)
             + ",\"doctorName\":" + Json.quote(doctorName)
             + ",\"date\":" + Json.quote(date)
             + ",\"time\":" + Json.quote(time)
             + ",\"status\":" + Json.quote(status) + "}";
    }
}
