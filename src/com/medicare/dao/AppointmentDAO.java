package com.medicare.dao;

import com.medicare.model.Appointment;
import com.medicare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO implements CrudDAO<Appointment> {

    @Override
    public List<Appointment> findAll() throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.id, a.patient_id, a.doctor_id, p.name AS pname, d.name AS dname, "
                   + "a.appt_date, a.appt_time, a.status "
                   + "FROM appointments a "
                   + "JOIN patients p ON a.patient_id = p.id "
                   + "JOIN doctors d ON a.doctor_id = d.id "
                   + "ORDER BY a.appt_date DESC, a.appt_time DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String time = rs.getString("appt_time");
                list.add(new Appointment(rs.getInt("id"), rs.getInt("patient_id"), rs.getInt("doctor_id"),
                        rs.getString("pname"), rs.getString("dname"),
                        rs.getString("appt_date"), time.substring(0, 5), rs.getString("status")));
            }
        }
        return list;
    }

    @Override
    public void add(Appointment a) throws SQLException {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appt_date, appt_time, status) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String t = a.getTime().length() == 5 ? a.getTime() + ":00" : a.getTime();
            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setDate(3, Date.valueOf(a.getDate()));
            ps.setTime(4, Time.valueOf(t));
            ps.setString(5, a.getStatus());
            ps.executeUpdate();
        }
    }

    public void updateStatus(int id, String status) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE appointments SET status=? WHERE id=?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM appointments WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
