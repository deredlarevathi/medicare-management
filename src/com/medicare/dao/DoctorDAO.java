package com.medicare.dao;

import com.medicare.model.Doctor;
import com.medicare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO implements CrudDAO<Doctor> {

    @Override
    public List<Doctor> findAll() throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Doctor(rs.getInt("id"), rs.getString("name"),
                        rs.getString("specialization"), rs.getString("phone")));
            }
        }
        return list;
    }

    @Override
    public void add(Doctor d) throws SQLException {
        String sql = "INSERT INTO doctors (name, specialization, phone) VALUES (?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getSpecialization());
            ps.setString(3, d.getPhone());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM doctors WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
