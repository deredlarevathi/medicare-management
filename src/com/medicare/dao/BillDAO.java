package com.medicare.dao;

import com.medicare.model.Bill;
import com.medicare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO implements CrudDAO<Bill> {

    @Override
    public List<Bill> findAll() throws SQLException {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT b.*, p.name AS pname FROM bills b "
                   + "JOIN patients p ON b.patient_id = p.id ORDER BY b.id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Bill(rs.getInt("id"), rs.getInt("patient_id"), rs.getString("pname"),
                        rs.getDouble("consultation_fee"), rs.getDouble("medicine_charges"),
                        rs.getDouble("other_charges"), rs.getString("status"),
                        rs.getString("payment_method"), rs.getString("bill_date")));
            }
        }
        return list;
    }

    @Override
    public void add(Bill b) throws SQLException {
        String sql = "INSERT INTO bills (patient_id, consultation_fee, medicine_charges, other_charges, "
                   + "total_amount, status, bill_date) VALUES (?,?,?,?,?, 'Unpaid', CURDATE())";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getPatientId());
            ps.setDouble(2, b.getConsultationFee());
            ps.setDouble(3, b.getMedicineCharges());
            ps.setDouble(4, b.getOtherCharges());
            ps.setDouble(5, b.getTotal());
            ps.executeUpdate();
        }
    }

    public void markPaid(int id, String method) throws SQLException {
        String sql = "UPDATE bills SET status='Paid', payment_method=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, method);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM bills WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
