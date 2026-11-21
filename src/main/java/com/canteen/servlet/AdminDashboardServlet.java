package com.canteen.servlet;

import com.canteen.util.Auth;
import com.canteen.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin-dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Check admin login
        if (!Auth.admin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String mode = request.getParameter("mode");

        if (!"monthly".equals(mode)) {
            mode = "daily";
        }

        LocalDate selectedDate = LocalDate.now();
        YearMonth selectedMonth = YearMonth.now();

        try {
            if ("daily".equals(mode)) {
                String dateParam = request.getParameter("date");

                if (dateParam != null && !dateParam.trim().isEmpty()) {
                    selectedDate = LocalDate.parse(dateParam);
                }
            } else {
                String monthParam = request.getParameter("month");

                if (monthParam != null && !monthParam.trim().isEmpty()) {
                    selectedMonth = YearMonth.parse(monthParam);
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "Invalid date or month selected.");
        }

        String condition;
        int firstValue;
        int secondValue = 0;

        if ("monthly".equals(mode)) {
            condition = "YEAR(created_at) = ? AND MONTH(created_at) = ?";
            firstValue = selectedMonth.getYear();
            secondValue = selectedMonth.getMonthValue();
        } else {
            condition = "DATE(created_at) = ?";
            firstValue = 0;
        }

        try (Connection con = DBConnection.getConnection()) {

            // 1. Summary information
            String summarySql =
                    "SELECT COUNT(*) AS totalOrders, " +
                    "SUM(CASE WHEN status = 'Picked Up' THEN 1 ELSE 0 END) AS completedOrders, " +
                    "SUM(CASE WHEN status <> 'Picked Up' THEN 1 ELSE 0 END) AS pendingOrders, " +
                    "COALESCE(SUM(CASE WHEN status = 'Picked Up' THEN total ELSE 0 END), 0) AS earnings " +
                    "FROM orders WHERE " + condition;

            try (PreparedStatement ps = con.prepareStatement(summarySql)) {

                if ("monthly".equals(mode)) {
                    ps.setInt(1, firstValue);
                    ps.setInt(2, secondValue);
                } else {
                    ps.setDate(1, java.sql.Date.valueOf(selectedDate));
                }

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        request.setAttribute("totalOrders",
                                rs.getInt("totalOrders"));

                        request.setAttribute("completedOrders",
                                rs.getInt("completedOrders"));

                        request.setAttribute("pendingOrders",
                                rs.getInt("pendingOrders"));

                        request.setAttribute("earnings",
                                rs.getDouble("earnings"));
                    }
                }
            }

            // 2. Daily or monthly report
            List<Map<String, Object>> report = new ArrayList<>();

            String reportSql;

            if ("monthly".equals(mode)) {

                reportSql =
                        "SELECT DATE(created_at) AS reportDate, " +
                        "COUNT(*) AS totalOrders, " +
                        "SUM(CASE WHEN status = 'Picked Up' THEN 1 ELSE 0 END) AS completedOrders, " +
                        "SUM(CASE WHEN status <> 'Picked Up' THEN 1 ELSE 0 END) AS pendingOrders, " +
                        "COALESCE(SUM(CASE WHEN status = 'Picked Up' THEN total ELSE 0 END), 0) AS earnings " +
                        "FROM orders WHERE " + condition +
                        " GROUP BY DATE(created_at) " +
                        "ORDER BY reportDate DESC";

            } else {

                reportSql =
                        "SELECT id, order_number, total, status, created_at " +
                        "FROM orders WHERE " + condition +
                        " ORDER BY created_at DESC";
            }

            try (PreparedStatement ps = con.prepareStatement(reportSql)) {

                if ("monthly".equals(mode)) {
                    ps.setInt(1, firstValue);
                    ps.setInt(2, secondValue);
                } else {
                    ps.setDate(1, java.sql.Date.valueOf(selectedDate));
                }

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        Map<String, Object> row = new HashMap<>();

                        if ("monthly".equals(mode)) {

                            row.put("reportDate",
                                    rs.getDate("reportDate"));

                            row.put("totalOrders",
                                    rs.getInt("totalOrders"));

                            row.put("completedOrders",
                                    rs.getInt("completedOrders"));

                            row.put("pendingOrders",
                                    rs.getInt("pendingOrders"));

                            row.put("earnings",
                                    rs.getDouble("earnings"));

                        } else {

                            row.put("id", rs.getInt("id"));

                            row.put("orderNumber",
                                    rs.getString("order_number"));

                            row.put("total",
                                    rs.getDouble("total"));

                            row.put("status",
                                    rs.getString("status"));

                            row.put("createdAt",
                                    rs.getTimestamp("created_at"));
                        }

                        report.add(row);
                    }
                }
            }

            // Send information to JSP
            request.setAttribute("mode", mode);
            request.setAttribute("date", selectedDate.toString());
            request.setAttribute("month", selectedMonth.toString());
            request.setAttribute("report", report);

            request.getRequestDispatcher("/admin-dashboard.jsp")
                   .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Error loading admin dashboard data.", e);
        }
    }
}