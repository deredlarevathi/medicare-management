package com.medicare.server;

import com.medicare.util.DBConnection;
import com.sun.net.httpserver.HttpServer;

import java.io.Console;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Scanner;

public class MediCareServer {
    public static void main(String[] args) throws Exception {
        System.out.print("Enter your MySQL root password (typing may stay hidden), then press Enter: ");
        Console console = System.console();
        String password;
        if (console != null) {
            password = new String(console.readPassword());
        } else {
            password = new Scanner(System.in).nextLine();
        }
        DBConnection.setPassword(password);

        try {
            DBConnection.testConnection();
            System.out.println("Database connected.");
        } catch (SQLException e) {
            System.out.println();
            System.out.println("COULD NOT CONNECT TO MYSQL: " + e.getMessage());
            System.out.println("Check: 1) the password is right  2) MySQL is running  3) the .jar file is inside the lib folder");
            return;
        }

        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/patients", new PatientHandler());
        server.createContext("/api/doctors", new DoctorHandler());
        server.createContext("/api/appointments", new AppointmentHandler());
        server.createContext("/api/bills", new BillHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/logout", new LogoutHandler());
        server.createContext("/api/me", new MeHandler());
        server.createContext("/", new StaticHandler());
        server.start();
        System.out.println("MediCare is running. Open http://localhost:" + port + " in your browser.");
        System.out.println("Keep this window open. Press Ctrl+C to stop.");
    }
}
