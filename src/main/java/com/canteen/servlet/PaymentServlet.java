
package com.canteen.servlet;

import com.canteen.model.CartItem;
import com.canteen.util.Auth;
import com.canteen.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@WebServlet("/payment")
public class PaymentServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest q, HttpServletResponse p)
            throws ServletException, IOException {

        if (!Auth.student(q)) {
            p.sendRedirect(q.getContextPath() + "/login");
            return;
        }

        HttpSession session = q.getSession();

        Map<Integer, CartItem> cart =
                (Map<Integer, CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            p.sendRedirect(q.getContextPath() + "/menu");
            return;
        }

        double total = 0;

        for (CartItem item : cart.values()) {
            total += item.getSubtotal();
        }

        q.setAttribute("total", total);

        q.getRequestDispatcher("/payment.jsp").forward(q, p);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest q, HttpServletResponse p)
            throws ServletException, IOException {

        if (!Auth.student(q)) {
            p.sendRedirect(q.getContextPath() + "/login");
            return;
        }

        HttpSession session = q.getSession();

        Map<Integer, CartItem> cart =
                (Map<Integer, CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            p.sendRedirect(q.getContextPath() + "/menu");
            return;
        }

        Object userIdObject = session.getAttribute("userId");

        if (!(userIdObject instanceof Number)) {
            p.sendRedirect(q.getContextPath() + "/login");
            return;
        }

        int userId = ((Number) userIdObject).intValue();

        if (userId <= 0) {
            p.sendRedirect(q.getContextPath() + "/login");
            return;
        }

        String method = q.getParameter("paymentMethod");

        // Validate selected payment method
        if ("UPI".equals(method)) {

            String upiId = q.getParameter("upiId");

            if (upiId == null ||
                    !upiId.trim().matches(
                            "^[A-Za-z0-9._-]{2,}@[A-Za-z]{2,}$")) {

                showError(q, p, cart,
                        "Please enter a valid demo UPI ID.");
                return;
            }

        } else if ("CARD".equals(method)) {

            String cardName = q.getParameter("cardName");
            String cardNumber = q.getParameter("cardNumber");
            String expiry = q.getParameter("expiry");
            String cvv = q.getParameter("cvv");

            String digits = cardNumber == null
                    ? ""
                    : cardNumber.replaceAll("\\s+", "");

            if (cardName == null || cardName.trim().isEmpty()
                    || !digits.matches("[0-9]{12,19}")
                    || expiry == null
                    || !expiry.matches("(0[1-9]|1[0-2])/[0-9]{2}")
                    || cvv == null
                    || !cvv.matches("[0-9]{3,4}")) {

                showError(q, p, cart,
                        "Please enter valid fictional card details.");
                return;
            }

        } else {

            showError(q, p, cart,
                    "Please select a valid payment method.");
            return;
        }

        // Simulated payment: no actual money is transferred.
        BigDecimal total = BigDecimal.ZERO;

        try (Connection c = DBConnection.getConnection()) {

            c.setAutoCommit(false);

            try {

                // Recheck food availability and current prices
                String priceSql =
                        "SELECT price FROM menu_items "
                        + "WHERE id = ? AND available = 1";

                try (PreparedStatement s =
                        c.prepareStatement(priceSql)) {

                    for (CartItem item : cart.values()) {

                        if (item.quantity <= 0) {
                            throw new Exception("Invalid item quantity.");
                        }

                        s.setInt(1, item.id);

                        try (ResultSet r = s.executeQuery()) {

                            if (!r.next()) {
                                throw new Exception(
                                        "An item is no longer available: "
                                        + item.name
                                );
                            }

                            item.price = r.getDouble("price");

                            BigDecimal subtotal =
                                    BigDecimal.valueOf(item.price)
                                    .multiply(
                                            BigDecimal.valueOf(item.quantity)
                                    );

                            total = total.add(subtotal);
                        }
                    }
                }

                // Generate demo order and transaction numbers
                String orderNumber =
                        "CC" + System.currentTimeMillis();

                String transactionId =
                        "TXN" + UUID.randomUUID()
                                .toString()
                                .replace("-", "")
                                .substring(0, 12)
                                .toUpperCase();

                int orderId;

                // Save order
                String orderSql =
                        "INSERT INTO orders "
                        + "(order_number, user_id, total, status, "
                        + "payment_method, payment_status, transaction_id) "
                        + "VALUES (?, ?, ?, 'Placed', ?, 'SUCCESS', ?)";

                try (PreparedStatement s = c.prepareStatement(
                        orderSql, Statement.RETURN_GENERATED_KEYS)) {

                    s.setString(1, orderNumber);
                    s.setInt(2, userId);
                    s.setBigDecimal(3, total);
                    s.setString(4, method);
                    s.setString(5, transactionId);

                    s.executeUpdate();

                    try (ResultSet r = s.getGeneratedKeys()) {

                        if (!r.next()) {
                            throw new Exception(
                                    "Unable to create order."
                            );
                        }

                        orderId = r.getInt(1);
                    }
                }

                // Save ordered food items
                String itemSql =
                        "INSERT INTO order_items "
                        + "(order_id, menu_item_id, item_name, "
                        + "quantity, unit_price) "
                        + "VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement s =
                        c.prepareStatement(itemSql)) {

                    for (CartItem item : cart.values()) {

                        s.setInt(1, orderId);
                        s.setInt(2, item.id);
                        s.setString(3, item.name);
                        s.setInt(4, item.quantity);
                        s.setBigDecimal(
                                5,
                                BigDecimal.valueOf(item.price)
                        );

                        s.addBatch();
                    }

                    s.executeBatch();
                }

                // Save all database changes
                c.commit();

                // Clear cart after successful database commit
                cart.clear();

                // Store confirmation details in session
                session.setAttribute("lastOrder", orderNumber);
                session.setAttribute("paymentMethod", method);
                session.setAttribute(
                        "paymentTransactionId", transactionId);
                session.setAttribute("lastOrderTotal", total);

                // Open payment confirmation page
                p.sendRedirect(
                        q.getContextPath() + "/payment-success.jsp"
                );

            } catch (Exception e) {

                try {
                    c.rollback();
                } catch (Exception rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }

        } catch (Exception e) {

            showError(q, p, cart,
                    "Payment could not be completed. "
                    + "Please check your cart and try again.");
        }
    }

    private void showError(
            HttpServletRequest q,
            HttpServletResponse p,
            Map<Integer, CartItem> cart,
            String message)
            throws ServletException, IOException {

        double total = 0;

        for (CartItem item : cart.values()) {
            total += item.getSubtotal();
        }

        q.setAttribute("total", total);
        q.setAttribute("error", message);

        q.getRequestDispatcher("/payment.jsp").forward(q, p);
    }
}
