package com.canteen.servlet;

import com.canteen.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/notification-feed")
public class NotificationFeedServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        // Check login
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                "{\"count\":0,\"notifications\":[]}"
            );
            return;
        }

        // Get logged-in student ID
        int userId = ((Number) session.getAttribute("userId")).intValue();

        StringBuilder json = new StringBuilder();

        String countSql =
            "SELECT COUNT(*) FROM notifications " +
            "WHERE user_id = ? AND is_read = FALSE";

        String notificationSql =
            "SELECT id, message, is_read FROM notifications " +
            "WHERE user_id = ? ORDER BY id DESC LIMIT 10";

        try (Connection con = DBConnection.getConnection()) {

            int unreadCount = 0;

            // Get unread notification count
            try (PreparedStatement ps = con.prepareStatement(countSql)) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        unreadCount = rs.getInt(1);
                    }
                }
            }

            json.append("{");
            json.append("\"count\":").append(unreadCount);
            json.append(",\"notifications\":[");

            boolean first = true;

            // Get latest notifications
            try (PreparedStatement ps =
                    con.prepareStatement(notificationSql)) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        if (!first) {
                            json.append(",");
                        }

                        first = false;

                        int id = rs.getInt("id");
                        String message = rs.getString("message");
                        boolean isRead = rs.getBoolean("is_read");

                        if (message == null) {
                            message = "";
                        }

                        // Escape JSON special characters
                        message = message
                            .replace("\\", "\\\\")
                            .replace("\"", "\\\"")
                            .replace("\n", "\\n")
                            .replace("\r", "\\r")
                            .replace("\t", "\\t");

                        json.append("{")
                            .append("\"id\":").append(id).append(",")
                            .append("\"message\":\"")
                            .append(message).append("\",")
                            .append("\"read\":").append(isRead)
                            .append("}");
                    }
                }
            }

            json.append("]}");

            response.getWriter().write(json.toString());

        } catch (SQLException e) {

            throw new ServletException(
                "Error loading student notifications", e
            );
        }
    }
}