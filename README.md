# College Canteen Ordering System

A web-based college canteen ordering application built using **Java, JSP, Jakarta Servlets, JDBC, MySQL, and Apache Tomcat**. Students can browse the menu, add food to a cart, place orders, make a demo payment selection, and track order progress. Administrators can manage menu items, monitor orders through an admin dashboard, and update order statuses. Students receive notifications when their order status changes.

> **Payment note:** The application provides a demo UPI/card payment interface. It does not connect to a real bank, UPI provider, or payment gateway, and it does not process actual payments.

## 1. Features

### Student
- Register and log in with a student account.
- Browse available food items, descriptions, categories, prices, and menu images.
- Add food items to the shopping cart.
- Update item quantities and remove items from the cart.
- Review the cart total before checkout.
- Continue to a demo payment page with UPI and card options.
- View the payment/order confirmation page.
- View previous orders and track order status.
- Receive notifications when an order is accepted, being prepared, ready for pickup, or picked up.

### Administrator
- Log in with an administrator account.
- View the admin dashboard and order information.
- View customer/order details and ordered items.
- Update order status through the order workflow.
- Add menu items and manage existing menu items.
- Enable or disable menu item availability.

### Order Status Workflow

`Placed → Accepted → Preparing → Ready → Picked Up`

When an administrator changes an order to **Accepted**, **Preparing**, **Ready**, or **Picked Up**, the application creates a notification for the student associated with that order.

### Payment Interface

- A demo payment page is provided after the cart/checkout step.
- Students can select UPI or card as the payment method.
- The interface is for academic demonstration only. No real transaction is initiated and no real payment gateway is integrated.

### Cancellation Policy

**No cancellation policy:** Once an order is placed, it cannot be cancelled. Students should check their selected items and quantities before proceeding with payment.

## 2. Technology Stack

| Layer | Technology |
|---|---|
| Frontend | JSP, HTML, CSS, JavaScript |
| Backend | Java Servlets (Jakarta Servlet API) |
| Database access | JDBC |
| Database | MySQL |
| Web server | Apache Tomcat 10 |
| Build tool | Apache Maven |
| IDE | NetBeans (or another Java IDE) |

**Compatibility:** The application uses `jakarta.servlet` imports and is intended for Tomcat 10 or a compatible newer version. Tomcat 9 uses the older `javax.servlet` namespace and is not directly compatible.

## 3. Requirements

Install the following:
- JDK compatible with the Java version configured in `pom.xml`
- Apache Maven
- MySQL Server
- Apache Tomcat 10
- NetBeans IDE (recommended)
- MySQL Workbench (optional)

Check your installations in a terminal:

```bash
java -version
javac -version
mvn -version
mysql --version
```

## 4. Project Structure

The main source packages and web resources are organized approximately as follows. Exact JSP filenames may vary with the current project version.

```text
CollegeCanteen/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── canteen/
│       │           ├── model/
│       │           │   └── CartItem.java
│       │           ├── servlet/
│       │           │   ├── AdminServlet.java
│       │           │   ├── AdminDashboardServlet.java
│       │           │   ├── CartServlet.java
│       │           │   ├── CheckoutServlet.java
│       │           │   ├── LoginServlet.java
│       │           │   ├── LogoutServlet.java
│       │           │   ├── MenuAdminServlet.java
│       │           │   ├── MenuManageServlet.java
│       │           │   ├── MenuServlet.java
│       │           │   ├── OrderStatusServlet.java
│       │           │   ├── OrdersServlet.java
│       │           │   ├── PaymentServlet.java
│       │           │   ├── RegisterServlet.java
│       │           │   └── UpdateOrderStatusServlet.java
│       │           └── util/
│       │               ├── Auth.java
│       │               └── DBConnection.java
│       └── webapp/
│           ├── includes/
│           │   ├── header.jspf
│           │   └── footer.jspf
│           ├── assets/
│           │   └── css/
│           ├── admin.jsp
│           ├── cart.jsp
│           ├── menu.jsp
│           ├── menu-manage.jsp
│           ├── payment.jsp
│           ├── payment-success.jsp
│           ├── order-success.jsp
│           ├── orders.jsp
│           ├── login.jsp
│           ├── register.jsp
│           └── WEB-INF/
│               └── web.xml
├── pom.xml
└── README.md
```

## 5. Database Setup

1. Start the MySQL server.
2. Open MySQL Workbench or the MySQL command-line client.
3. Create the database used by the application:

```sql
CREATE DATABASE college_canteen;
USE college_canteen;
```

4. Create the tables required by the application and ensure that the database includes the tables below. If your project contains a database SQL script, run that script instead of manually creating duplicate tables.

| Table | Purpose |
|---|---|
| `users` | Student and administrator account details, including roles and password hashes |
| `menu_items` | Food item details, prices, availability, and image URLs |
| `orders` | Order owner, total, current status, and order timestamps |
| `order_items` | Items and quantities associated with each order |
| `notifications` | Student notifications for order-status updates |

The `notifications` table used by the application has these fields:

```sql
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    order_id INT NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (order_id) REFERENCES orders(id)
);
```

If the `notifications` table already exists, do not run a second `CREATE TABLE` statement unnecessarily. Check its structure with:

```sql
DESCRIBE notifications;
```

## 6. Configure the Database Connection

Open:

```text
src/main/java/com/canteen/util/DBConnection.java
```

Set the database URL, username, and password to match your local MySQL installation. The database URL should point to `college_canteen`, for example:

```text
jdbc:mysql://localhost:3306/college_canteen?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

Use your own MySQL credentials in `DBConnection.java`. Do not commit real passwords to a public GitHub repository.

## 7. Build the Project

1. Open the project folder containing `pom.xml` in NetBeans.
2. Confirm that the MySQL connection details are correct.
3. Clean and build the project, or run this command from the project directory:

```bash
mvn clean package
```

If the build succeeds, Maven creates the WAR file under:

```text
target/CollegeCanteen.war
```

The exact output filename follows the Maven `artifactId` and `version` configured in `pom.xml`.

## 8. Deploy on Apache Tomcat

1. Make sure MySQL is running.
2. Make sure Tomcat 10 is installed and running.
3. Deploy `target/CollegeCanteen.war` to Tomcat's `webapps` directory, or deploy the project through NetBeans.
4. Start/restart Tomcat and check its logs if the application does not start.
5. Open the application using its deployed context path. For a WAR named `CollegeCanteen.war`, the usual URL is:

```text
http://localhost:8080/CollegeCanteen/
```

If you deploy through NetBeans with a different context path, use the URL shown by the IDE/server.

## 9. Account Roles

New registrations are created with the `STUDENT` role. To grant administrator access to an existing account, update that account's role in MySQL. For example:

```sql
UPDATE users
SET role = 'ADMIN'
WHERE email = 'admin@gmail.com';
```

Use the email address of the account you actually registered. Log out and log in again after changing the role so the new role is loaded into the session. Do not publish administrator credentials in the README or in a public repository.

## 10. Main Application Routes

The application uses servlet mappings for its main workflows. The following mappings are based on the servlet routes used in the project:

| Route | Purpose |
|---|---|
| `/login` | Display/login authentication |
| `/register` | Student registration |
| `/logout` | Log out |
| `/menu` | Display available menu items |
| `/cart` | View and update the cart/add items, according to the submitted action |
| `/checkout` | Continue from the cart to checkout |
| `/payment` | Process the demo payment form |
| `/order-success` | Display order confirmation |
| `/orders` | Student order history |
| `/admin` | Admin dashboard and order view |
| `/menu-manage` | Admin menu management page |
| `/menu-admin` | Admin menu actions, such as adding/toggling items |
| `/order-status` | Existing order-status route, if used by the project |
| `/update-order-status` | Admin order-status update and student notification creation |

The context path (for example, `/CollegeCanteen`) is added before these routes when opening the application in a browser.

## 11. Testing Checklist

### Student workflow
- [ ] Register a student account.
- [ ] Log in with valid credentials.
- [ ] Confirm invalid login details are rejected.
- [ ] Browse the available menu.
- [ ] Add food items to the cart.
- [ ] Update quantities and remove items.
- [ ] Confirm the cart total is correct.
- [ ] Review the no-cancellation notice.
- [ ] Open the payment page and select the UPI or card demo option.
- [ ] Complete the demo order/payment flow and view the confirmation page.
- [ ] Open order history and track an order.
- [ ] Verify notifications appear for order-status changes.

### Administrator workflow
- [ ] Log in with an account whose role is `ADMIN`.
- [ ] Open the admin dashboard.
- [ ] View customer orders and order details.
- [ ] Change an order from Placed to Accepted.
- [ ] Change the order to Preparing, Ready, and Picked Up.
- [ ] Verify a student notification is created for each of those status updates.
- [ ] Add a menu item.
- [ ] Change menu item availability.

### Verify notifications in MySQL

```sql
SELECT id, user_id, order_id, message, is_read, created_at
FROM notifications
ORDER BY created_at DESC;
```


## 12. Security and Project Limitations

This project is intended for academic/demo use. The payment interface is simulated and must not be presented as a live payment gateway. Before any production deployment, implement a real payment provider integration with server-side payment verification, HTTPS, CSRF protection, secure session-cookie settings, login rate limiting, secret management, database backups, centralized logging, and security testing. Never store raw card details or sensitive payment credentials in the application database.


