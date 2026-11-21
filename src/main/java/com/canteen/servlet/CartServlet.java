
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

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

        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute("cart", cart);
        }

        Collection<CartItem> items = cart.values();

        double total = 0;

        for (CartItem item : items) {
            total += item.getSubtotal();
        }

        q.setAttribute("cart", items);
        q.setAttribute("total", total);

        q.getRequestDispatcher("/cart.jsp").forward(q, p);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest q, HttpServletResponse p)
            throws ServletException, IOException {

        if (!Auth.student(q)) {
            if ("update".equals(q.getParameter("action"))) {
                p.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                p.sendRedirect(q.getContextPath() + "/login");
            }
            return;
        }

        HttpSession session = q.getSession();

        Map<Integer, CartItem> cart =
                (Map<Integer, CartItem>) session.getAttribute("cart");

        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute("cart", cart);
        }

        String action = q.getParameter("action");

        try {

            int itemId = Integer.parseInt(q.getParameter("itemId"));

            // REMOVE ITEM
            if ("remove".equals(action)) {

                if (cart.remove(itemId) != null) {
                    session.setAttribute(
                            "cartMessage", "Item removed from cart.");
                } else {
                    session.setAttribute(
                            "cartMessage", "Item was not found in cart.");
                }

            }

            // AUTOMATIC QUANTITY UPDATE
            else if ("update".equals(action)) {

                CartItem item = cart.get(itemId);

                if (item == null) {
                    p.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                int quantity = Integer.parseInt(
                        q.getParameter("quantity"));

                if (quantity < 1 || quantity > 99) {
                    p.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                item.quantity = quantity;

                // Return success without redirecting
                p.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            // ADD ITEM
            else {

                String sql =
                        "SELECT id, name, price "
                        + "FROM menu_items "
                        + "WHERE id = ? AND available = 1";

                try (Connection c = DBConnection.getConnection();
                     PreparedStatement s = c.prepareStatement(sql)) {

                    s.setInt(1, itemId);

                    try (ResultSet r = s.executeQuery()) {

                        if (r.next()) {

                            String name = r.getString("name");
                            double price = r.getDouble("price");

                            CartItem existing = cart.get(itemId);

                            if (existing != null) {

                                if (existing.quantity < 99) {
                                    existing.quantity++;
                                }

                                session.setAttribute(
                                        "cartMessage",
                                        "Item quantity increased in cart.");

                            } else {

                                CartItem newItem =
                                        new CartItem(
                                                itemId,
                                                name,
                                                price,
                                                1
                                        );

                                cart.put(itemId, newItem);

                                session.setAttribute(
                                        "cartMessage",
                                        "Item added to cart successfully.");
                            }

                        } else {

                            session.setAttribute(
                                    "cartMessage",
                                    "This food item is not available.");
                        }
                    }
                }
            }

        } catch (NumberFormatException e) {

            if ("update".equals(action)) {
                p.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            session.setAttribute(
                    "cartMessage", "Invalid item or quantity.");

        } catch (SQLException e) {

            throw new ServletException(
                    "Error updating shopping cart.", e);
        }

        p.sendRedirect(q.getContextPath() + "/cart");
    }
}


