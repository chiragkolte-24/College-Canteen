CREATE DATABASE IF NOT EXISTS college_canteen CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE college_canteen;
CREATE TABLE users(id INT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(100) NOT NULL,email VARCHAR(150) NOT NULL UNIQUE,password_hash VARCHAR(100) NOT NULL,role ENUM('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT',created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE menu_items(id INT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(120) NOT NULL,description VARCHAR(255),category VARCHAR(80) NOT NULL,price DECIMAL(10,2) NOT NULL,available TINYINT(1) NOT NULL DEFAULT 1,created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE orders(id INT AUTO_INCREMENT PRIMARY KEY,order_number VARCHAR(40) NOT NULL UNIQUE,user_id INT NOT NULL,total DECIMAL(10,2) NOT NULL,status ENUM('Placed','Accepted','Preparing','Ready','Picked Up') NOT NULL DEFAULT 'Placed',created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,updated_at TIMESTAMP NULL,picked_up_at TIMESTAMP NULL,CONSTRAINT fk_orders_user FOREIGN KEY(user_id) REFERENCES users(id));
CREATE TABLE order_items(id INT AUTO_INCREMENT PRIMARY KEY,order_id INT NOT NULL,menu_item_id INT NULL,item_name VARCHAR(120) NOT NULL,quantity INT NOT NULL,unit_price DECIMAL(10,2) NOT NULL,CONSTRAINT fk_items_order FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE,CONSTRAINT fk_items_menu FOREIGN KEY(menu_item_id) REFERENCES menu_items(id) ON DELETE SET NULL);
INSERT INTO menu_items(name,description,category,price) VALUES
('Veg Sandwich','Fresh vegetable sandwich','Snacks',45.00),('Masala Dosa','Crispy dosa with chutney','South Indian',70.00),('Veg Thali','Daily meal combo','Meals',100.00),('Tea','Hot milk tea','Beverages',15.00),('Cold Coffee','Chilled coffee','Beverages',50.00);
-- Create an admin account by registering through the app, then run:
-- UPDATE users SET role='ADMIN' WHERE email='your-admin-email@example.com';
