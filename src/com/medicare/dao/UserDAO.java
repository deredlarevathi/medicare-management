package com.medicare.dao;

import com.medicare.model.User;
import com.medicare.util.DBConnection;
import com.medicare.util.PasswordUtil;

import java.sql.*;

public class UserDAO {

    /** Returns the User if the username and password match, otherwise null. */
    public User authenticate(String username, String password) throws SQLException {
        if (username == null || password == null) return null;
        String sql = "SELECT id, username, full_name, password_hash, role FROM users WHERE username=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verify(password, rs.getString("password_hash"))) {
                    return new User(rs.getInt("id"), rs.getString("username"),
                            rs.getString("full_name"), rs.getString("role"));
                }
            }
        }
        return null;
    }
}
