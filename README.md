# College Canteen Ordering & Pickup System (JSP / Servlet / MySQL)

A NetBeans Maven Web Application built with JSP, Java Servlets, JDBC, MySQL and Apache Tomcat. This is an adaptation of the supplied SRS to the requested JSP/Servlet stack (instead of the Spring Boot stack named in the SRS).

## Features
- Student registration and login (BCrypt password hashing)
- Menu browsing and availability
- Session-based cart and checkout
- Unique order number and persisted order items
- Student order history and status tracking
- Admin order dashboard: Placed → Accepted → Preparing → Ready → Picked Up
- Admin menu item creation and availability toggle
- Payment at pickup; no online payment
- Ready state is shown to the student on the order page; refresh to retrieve latest status

## Requirements
- JDK 8 or newer
- NetBeans with Maven support
- Apache Tomcat 9.x (javax.servlet API; not Tomcat 10/11 without migration)
- MySQL 8.x
- Maven

## Setup
1. Open MySQL Workbench or mysql command line and execute `database/college_canteen.sql`.
2. Open `src/main/java/com/canteen/util/DBConnection.java`. Set `USER` and `PASSWORD` to your MySQL credentials. Do not commit real credentials to public source control.
3. In NetBeans choose **File → Open Project** and select this folder (`CollegeCanteenJSP`).
4. Ensure Tomcat 9 is registered in NetBeans (**Tools → Servers**).
5. Right-click project → **Clean and Build**. Maven downloads the dependencies and creates `target/CollegeCanteen.war`.
6. Right-click project → **Run**. Or deploy the WAR to Tomcat 9.
7. Open `http://localhost:8080/CollegeCanteen/`.

## Create an admin
1. Register a normal account using the Register page.
2. Run this SQL, replacing the email:
   `UPDATE users SET role='ADMIN' WHERE email='admin@example.com';`
3. Log out and log in again. The admin dashboard and menu management links will appear.

## Project structure
- `src/main/java/com/canteen/servlet` — controllers / request handling
- `src/main/java/com/canteen/util` — database connection and session role checks
- `src/main/java/com/canteen/model` — cart model
- `src/main/webapp` — JSP views, CSS, WEB-INF
- `database` — MySQL schema and sample menu

## Notes / scope
- Uses Tomcat 9 and `javax.servlet`; Tomcat 10+ uses `jakarta.servlet` and requires migration.
- The SRS specifies Spring Boot and WebSocket. This requested version uses JSP/Servlets and simple page-refresh tracking instead of Spring Boot/WebSocket. The SRS's core ordering, roles, menu, status workflow, pickup, history, and pay-at-pickup scope are represented.
- This is an educational starter project. Before production, add CSRF protection, stricter validation, connection pooling, audit logging, and HTTPS.
