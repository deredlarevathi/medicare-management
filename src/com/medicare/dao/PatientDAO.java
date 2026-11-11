package com.medicare.dao;

import com.medicare.model.Patient;
import com.medicare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO implements CrudDAO<Patient> {

    @Override
    public List<Patient> findAll() throws SQLException {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Patient(rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                        rs.getString("gender"), rs.getString("phone"), rs.getString("address")));
            }
        }
        return list;
    }

    @Override
    public void add(Patient p) throws SQLException {
        String sql = "INSERT INTO patients (name, age, gender, phone, address) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setInt(2, p.getAge());
            ps.setString(3, p.getGender());
            ps.setString(4, p.getPhone());
            ps.setString(5, p.getAddress());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM patients WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
