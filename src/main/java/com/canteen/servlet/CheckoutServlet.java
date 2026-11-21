
package com.canteen.servlet;

import com.canteen.model.CartItem;
import com.canteen.util.Auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest q, HttpServletResponse p)
            throws ServletException, IOException {

        if (!Auth.student(q)) {
            p.sendRedirect(q.getContextPath() + "/login");
            return;
        }

        HttpSession session = q.getSession();

        Map<Integer, CartItem> cart =
                (Map<Integer, CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            p.sendRedirect(q.getContextPath() + "/menu");
            return;
        }

        // Open payment page
        p.sendRedirect(q.getContextPath() + "/payment");
    }
}

