# MediCare – Hospital Management System

Stack: Core Java (OOP + JDBC) · MySQL · HTML · CSS · JavaScript
No frameworks. The web server uses `com.sun.net.httpserver`, which ships with the JDK.

## Features
- Login page with sessions (cookie token) and three roles
- Patients, doctors, appointments (book, complete, cancel)
- Billing: consultation + medicine + other charges, mark paid (Cash/Card/UPI), printable invoice
- Today's schedule view

## Demo logins
| Username  | Password  | Role         | Can do |
|-----------|-----------|--------------|--------|
| admin     | admin123  | ADMIN        | Everything, including delete |
| reception | recep123  | RECEPTIONIST | Register, book, bill. No delete |
| doctor    | doctor123 | DOCTOR       | View records, complete appointments. No billing |

Change these passwords before real use.

## OOP concepts used
| Concept        | Where |
|----------------|-------|
| Abstraction    | `Person`, `BaseHandler` (abstract classes) |
| Inheritance    | `Patient`, `Doctor`, `User` extend `Person`; all handlers extend `BaseHandler` |
| Encapsulation  | Private fields + getters; `Bill.getTotal()` calculates the total itself; `DBConnection` hides JDBC setup |
| Polymorphism   | `getRole()`, `toJson()`, and `allowed()` overridden per class (e.g. `BillHandler` blocks doctors) |
| Interfaces     | `Jsonable`, generic `CrudDAO<T>` |

## Security notes
- Passwords are stored as salted SHA-256 (`PasswordUtil`, JDK only). Production systems normally use bcrypt, which needs a library.
- Every API call checks the session and the role on the server, not only in the browser.
- All SQL uses `PreparedStatement`.
- Sessions live in memory, so restarting the server logs everyone out.

## Setup
1. Install JDK 11+ and MySQL.
2. Create the database and tables: `mysql -u root -p < database/schema.sql`
   (Already ran the old schema? Run only `database/auth_billing.sql` to add users and bills.)
3. Download MySQL Connector/J (https://dev.mysql.com/downloads/connector/j/) and put the .jar in `lib/`.
4. Compile and run: double-click `start.bat` (Windows) or run `./compile.sh` then `./run.sh`.
5. When asked, type your MySQL root password and press Enter. No file editing is needed.
6. Open http://localhost:8080 and sign in.
