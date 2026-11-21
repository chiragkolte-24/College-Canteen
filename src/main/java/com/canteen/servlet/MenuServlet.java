
package com.canteen.servlet;

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

@WebServlet("/menu")
public class MenuServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest q, HttpServletResponse p)
            throws ServletException, IOException {

        // Retrieve available menu items including image URLs
        String sql = "SELECT id, name, description, category, "
                   + "price, image_url "
                   + "FROM menu_items "
                   + "WHERE available = 1 "
                   + "ORDER BY category, name";

        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement s = c.prepareStatement(sql);
            ResultSet r = s.executeQuery()
        ) {

            List<Map<String, Object>> items = new ArrayList<>();

            while (r.next()) {

                Map<String, Object> m = new HashMap<>();

                m.put("id", r.getInt("id"));
                m.put("name", r.getString("name"));
                m.put("description", r.getString("description"));
                m.put("category", r.getString("category"));
                m.put("price", r.getDouble("price"));

                // Retrieve internet image URL
                m.put("image_url", r.getString("image_url"));

                items.add(m);
            }

            // Send menu items to JSP
            q.setAttribute("items", items);

            // Display customer menu
            q.getRequestDispatcher("/menu.jsp").forward(q, p);

        } catch (SQLException e) {

            throw new ServletException(
                "Error loading menu items", e
            );
        }
    }
}
