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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/orders")
public class OrdersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest q, HttpServletResponse p)
            throws ServletException, IOException {

        // Check student authentication
        if (!Auth.student(q)) {
            p.sendRedirect(q.getContextPath() + "/login");
            return;
        }

        String sql = "SELECT o.id, o.order_number, o.total, o.status, "
                + "o.created_at, "
                + "GROUP_CONCAT(CONCAT(oi.item_name, ' x', oi.quantity) "
                + "SEPARATOR ', ') AS items "
                + "FROM orders o "
                + "JOIN order_items oi ON oi.order_id = o.id "
                + "WHERE o.user_id = ? "
                + "GROUP BY o.id "
                + "ORDER BY o.created_at DESC";

        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement s = c.prepareStatement(sql)
        ) {

            s.setInt(
                1,
                (Integer) q.getSession().getAttribute("userId")
            );

            try (ResultSet r = s.executeQuery()) {

                List<Map<String, Object>> list = new ArrayList<>();

                while (r.next()) {

                    Map<String, Object> m = new HashMap<>();

                    String[] columns = {
                        "id",
                        "order_number",
                        "total",
                        "status",
                        "created_at",
                        "items"
                    };

                    for (String k : columns) {
                        m.put(k, r.getObject(k));
                    }

                    list.add(m);
                }

                q.setAttribute("orders", list);

                q.getRequestDispatcher("/orders.jsp").forward(q, p);
            }

        } catch (SQLException e) {
            throw new ServletException("Error loading order history", e);
        }
    }
}