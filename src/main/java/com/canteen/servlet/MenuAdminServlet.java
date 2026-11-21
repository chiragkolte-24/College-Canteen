
package com.canteen.servlet;

import com.canteen.util.Auth;
import com.canteen.util.DBConnection;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/menu-admin")
public class MenuAdminServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest q, HttpServletResponse p)
            throws IOException {

        // Check admin login
        if (!Auth.admin(q)) {
            p.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = q.getParameter("action");

        try (Connection c = DBConnection.getConnection()) {

            if ("toggle".equals(action)) {

                // Enable or disable menu item
                String sql = "UPDATE menu_items "
                           + "SET available = 1 - available WHERE id = ?";

                try (PreparedStatement s = c.prepareStatement(sql)) {

                    s.setInt(1, Integer.parseInt(q.getParameter("id")));
                    s.executeUpdate();
                }

            } else if ("add".equals(action)) {

                // Get menu item details
                String name = q.getParameter("name");
                String description = q.getParameter("description");
                String category = q.getParameter("category");
                String priceValue = q.getParameter("price");
                String imageUrl = q.getParameter("image_url");

                // Validate required fields
                if (name == null || name.trim().isEmpty()
                        || category == null || category.trim().isEmpty()
                        || priceValue == null || imageUrl == null
                        || imageUrl.trim().isEmpty()) {

                    throw new IllegalArgumentException(
                        "Please fill all required fields."
                    );
                }

                // Validate image URL
                URI uri = URI.create(imageUrl.trim());

                String scheme = uri.getScheme();

                if (!uri.isAbsolute()
                        || scheme == null
                        || !("http".equalsIgnoreCase(scheme)
                        || "https".equalsIgnoreCase(scheme))
                        || uri.getHost() == null
                        || uri.getUserInfo() != null) {

                    throw new IllegalArgumentException(
                        "Please enter a valid HTTP or HTTPS image URL."
                    );
                }

                // Validate price
                BigDecimal price = new BigDecimal(priceValue);

                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException(
                        "Price must be greater than zero."
                    );
                }

                // Insert menu item into database
                String sql = "INSERT INTO menu_items "
                           + "(name, description, category, price, available, image_url) "
                           + "VALUES (?, ?, ?, ?, 1, ?)";

                try (PreparedStatement s = c.prepareStatement(sql)) {

                    s.setString(1, name.trim());
                    s.setString(2, description);
                    s.setString(3, category.trim());
                    s.setBigDecimal(4, price);
                    s.setString(5, imageUrl.trim());

                    s.executeUpdate();
                }

            } else {

                p.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid action"
                );

                return;
            }

        } catch (IllegalArgumentException e) {

            throw new IOException("Invalid menu item details: " + e.getMessage(), e);

        } catch (Exception e) {

            throw new IOException("Error managing menu items", e);
        }

        p.sendRedirect(q.getContextPath() + "/menu-manage");
    }
}
