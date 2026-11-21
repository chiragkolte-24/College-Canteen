package com.canteen.servlet;

import com.canteen.util.Auth;
import com.canteen.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/notifications")
public class NotificationServlet extends HttpServlet {

    // Display all notifications
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        if (!Auth.student(request)) {
            response.sendRedirect(
                request.getContextPath() + "/login"
            );
            return;
        }

        HttpSession session = request.getSession(false);

        int userId = ((Number) session.getAttribute("userId")).intValue();

        List<Map<String, Object>> notifications = new ArrayList<>();

        int unreadCount = 0;

        String sql =
            "SELECT id, order_id, message, is_read, created_at " +
            "FROM notifications " +
            "WHERE user_id=? " +
            "ORDER BY created_at DESC, id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Map<String, Object> notification = new HashMap<>();

                    int id = rs.getInt("id");
                    boolean isRead = rs.getBoolean("is_read");

                    notification.put("id", id);
                    notification.put("orderId", rs.getInt("order_id"));
                    notification.put("message", rs.getString("message"));
                    notification.put("isRead", isRead);
                    notification.put("createdAt",
                                     rs.getTimestamp("created_at"));

                    if (!isRead) {
                        unreadCount++;
                    }

                    notifications.add(notification);
                }
            }

        } catch (SQLException e) {

            throw new ServletException(
                "Unable to load notifications", e
            );
        }

        request.setAttribute("notifications", notifications);
        request.setAttribute("unreadCount", unreadCount);

        request.getRequestDispatcher("/notification.jsp")
               .forward(request, response);
    }


    // Mark notification as read
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        if (!Auth.student(request)) {
            response.sendRedirect(
                request.getContextPath() + "/login"
            );
            return;
        }

        HttpSession session = request.getSession(false);

        int userId = ((Number) session.getAttribute("userId")).intValue();

        String notificationIdText =
            request.getParameter("notificationId");

        if (notificationIdText == null ||
            notificationIdText.trim().isEmpty()) {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Notification ID is required"
            );
            return;
        }

        int notificationId;

        try {
            notificationId = Integer.parseInt(notificationIdText);
        } catch (NumberFormatException e) {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid notification ID"
            );
            return;
        }

        String sql =
            "UPDATE notifications SET is_read=TRUE " +
            "WHERE id=? AND user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, notificationId);
            ps.setInt(2, userId);

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new ServletException(
                "Unable to mark notification as read", e
            );
        }

        response.sendRedirect(
            request.getContextPath() + "/notifications"
        );
    }
}