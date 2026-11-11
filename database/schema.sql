CREATE DATABASE IF NOT EXISTS medicare_db;
USE medicare_db;

CREATE TABLE IF NOT EXISTS patients (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  age INT NOT NULL,
  gender VARCHAR(10) NOT NULL,
  phone VARCHAR(15) NOT NULL,
  address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS doctors (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  specialization VARCHAR(100) NOT NULL,
  phone VARCHAR(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS appointments (
  id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL,
  doctor_id INT NOT NULL,
  appt_date DATE NOT NULL,
  appt_time TIME NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'Scheduled',
  FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
  FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

INSERT INTO doctors (name, specialization, phone) VALUES
('Dr. Ananya Rao', 'Cardiology', '9876500001'),
('Dr. Suresh Varma', 'Orthopedics', '9876500002'),
('Dr. Lakshmi Devi', 'Pediatrics', '9876500003');
USE medicare_db;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  full_name VARCHAR(100) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS bills (
  id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL,
  consultation_fee DECIMAL(10,2) NOT NULL DEFAULT 0,
  medicine_charges DECIMAL(10,2) NOT NULL DEFAULT 0,
  other_charges DECIMAL(10,2) NOT NULL DEFAULT 0,
  total_amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(10) NOT NULL DEFAULT 'Unpaid',
  payment_method VARCHAR(20),
  bill_date DATE NOT NULL,
  FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

INSERT INTO users (username, full_name, password_hash, role) VALUES
('admin','Hospital Admin','55bda082cc4b870d$a333ca46cddf3e72a11889b112bbc9ce606693861ad0a14ded0a9bdd6b8db52d','ADMIN'),
('reception','Front Desk','93a52f1476e32b4a$94397668a282524f32d9e5cec4dcd153eb2c8223a36cf301e3dcb4bc6e3db915','RECEPTIONIST'),
('doctor','Dr. Ananya Rao','f59f9dc91ee8efc5$de13a13f4e1032f8097007258d762cf0eeed66d21ed8e91c0c0c2fe6fbd7db79','DOCTOR');
