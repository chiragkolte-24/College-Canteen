package com.canteen.servlet;

import com.canteen.util.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest q, HttpServletResponse p)
            throws ServletException, IOException {

        if (!Auth.admin(q)) {
            p.sendRedirect(q.getContextPath() + "/login");
            return;
        }

        String sql = "SELECT o.id, o.order_number, o.total, o.status, "
                + "o.created_at, u.name AS customer, "
                + "GROUP_CONCAT(CONCAT(oi.item_name, ' x', oi.quantity) "
                + "SEPARATOR ', ') AS items "
                + "FROM orders o "
                + "JOIN users u ON u.id = o.user_id "
                + "JOIN order_items oi ON oi.order_id = o.id "
                + "GROUP BY o.id "
                + "ORDER BY o.created_at DESC";

        try (
            Connection c = DBConnection.getConnection();
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sql)
        ) {

            List<Map<String, Object>> list = new ArrayList<>();

            while (r.next()) {

                Map<String, Object> m = new HashMap<>();

                String[] columns = {
                    "id",
                    "order_number",
                    "total",
                    "status",
                    "created_at",
                    "customer",
                    "items"
                };

                for (String k : columns) {
                    m.put(k, r.getObject(k));
                }

                list.add(m);
            }

            q.setAttribute("orders", list);

            q.getRequestDispatcher("/admin.jsp").forward(q, p);

        } catch (SQLException e) {
            throw new ServletException("Error loading admin orders", e);
        }
    }
}
