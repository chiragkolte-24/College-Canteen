
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/menu-manage")
public class MenuManageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest q, HttpServletResponse p)
            throws ServletException, IOException {

        // Check admin authentication
        if (!Auth.admin(q)) {
            p.sendRedirect(q.getContextPath() + "/login");
            return;
        }

        // Retrieve menu items including internet image URLs
        String sql = "SELECT id, name, description, category, "
                   + "price, available, image_url "
                   + "FROM menu_items "
                   + "ORDER BY category, name";

        try (
            Connection c = DBConnection.getConnection();
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sql)
        ) {

            List<Map<String, Object>> a = new ArrayList<>();

            while (r.next()) {

                Map<String, Object> m = new HashMap<>();

                String[] columns = {
                    "id",
                    "name",
                    "description",
                    "category",
                    "price",
                    "available",
                    "image_url"
                };

                for (String k : columns) {
                    m.put(k, r.getObject(k));
                }

                a.add(m);
            }

            // Send menu items to JSP
            q.setAttribute("items", a);

            // Open menu management page
            q.getRequestDispatcher("/menu-manage.jsp").forward(q, p);

        } catch (SQLException e) {

            throw new ServletException(
                "Error loading menu items", e
            );
        }
    }
}
