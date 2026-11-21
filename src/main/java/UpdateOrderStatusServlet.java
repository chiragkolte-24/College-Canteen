
package com.canteen.servlet;

import com.canteen.util.Auth;
import com.canteen.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/update-order-status")
public class UpdateOrderStatusServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // Check admin access
        if (!Auth.admin(request)) {
            response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Admin access required"
            );
            return;
        }

        String orderIdText = request.getParameter("orderId");
        String newStatus = request.getParameter("status");

        if (orderIdText == null || newStatus == null) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Missing order information"
            );
            return;
        }

        int orderId;

        try {
            orderId = Integer.parseInt(orderIdText);
        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid order ID"
            );
            return;
        }

        // Allowed statuses
        List<String> allowedStatuses = Arrays.asList(
            "Accepted",
            "Preparing",
            "Ready",
            "Picked Up"
        );

        if (!allowedStatuses.contains(newStatus)) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid order status"
            );
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                int studentId;
                String oldStatus;

                // Get order details and lock the record
                String selectSql =
                    "SELECT user_id, status FROM orders "
                  + "WHERE id=? FOR UPDATE";

                try (PreparedStatement ps =
                        con.prepareStatement(selectSql)) {

                    ps.setInt(1, orderId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            con.rollback();

                            response.sendError(
                                HttpServletResponse.SC_NOT_FOUND,
                                "Order not found"
                            );
                            return;
                        }

                        studentId = rs.getInt("user_id");
                        oldStatus = rs.getString("status");
                    }
                }

                // Check valid status transition
                Map<String, String> nextStatus = new HashMap<>();

                nextStatus.put("Placed", "Accepted");
                nextStatus.put("Accepted", "Preparing");
                nextStatus.put("Preparing", "Ready");
                nextStatus.put("Ready", "Picked Up");

                if (!newStatus.equals(nextStatus.get(oldStatus))) {

                    con.rollback();

                    response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid status change from "
                        + oldStatus + " to " + newStatus
                    );

                    return;
                }

                // Update order status
                String updateSql;

                if ("Picked Up".equals(newStatus)) {

                    updateSql =
                        "UPDATE orders SET status=?, "
                      + "updated_at=NOW(), picked_up_at=NOW() "
                      + "WHERE id=?";

                } else {

                    updateSql =
                        "UPDATE orders SET status=?, "
                      + "updated_at=NOW() WHERE id=?";
                }

                try (PreparedStatement ps =
                        con.prepareStatement(updateSql)) {

                    ps.setString(1, newStatus);
                    ps.setInt(2, orderId);

                    ps.executeUpdate();
                }

                // Prepare notification message
                String message = null;

                if ("Accepted".equals(newStatus)) {

                    message =
                        "Your order #" + orderId
                        + " has been accepted by the canteen.";

                } else if ("Preparing".equals(newStatus)) {

                    message =
                        "Your order #" + orderId
                        + " is now being prepared. Please wait.";

                } else if ("Ready".equals(newStatus)) {

                    message =
                        "Your order #" + orderId
                        + " is ready for pickup. Please collect it.";

                } else if ("Picked Up".equals(newStatus)) {

                    message =
                        "Your order #" + orderId
                        + " has been successfully picked up. Thank you for ordering!";
                }

                // Insert notification for student
                if (message != null) {

                    String notificationSql =
                        "INSERT INTO notifications "
                      + "(user_id, order_id, message, is_read) "
                      + "VALUES (?, ?, ?, FALSE)";

                    try (PreparedStatement ps =
                            con.prepareStatement(notificationSql)) {

                        ps.setInt(1, studentId);
                        ps.setInt(2, orderId);
                        ps.setString(3, message);

                        ps.executeUpdate();
                    }
                }

                // Save order update and notification
                con.commit();

            } catch (Exception e) {

                con.rollback();
                throw e;
            }

        } catch (Exception e) {

            throw new ServletException(
                "Unable to update order status or create notification",
                e
            );
        }

        // Return to admin orders page
        response.sendRedirect(
            request.getContextPath() + "/admin"
        );
    }
}
