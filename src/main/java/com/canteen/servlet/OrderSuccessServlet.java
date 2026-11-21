
package com.canteen.servlet;

import com.canteen.util.Auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/order-success")
public class OrderSuccessServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest q, HttpServletResponse p)
            throws ServletException, IOException {

        // Check student login
        if (!Auth.student(q)) {
            p.sendRedirect(q.getContextPath() + "/login");
            return;
        }

        // Get existing session
        HttpSession session = q.getSession(false);

        if (session == null) {
            p.sendRedirect(q.getContextPath() + "/login");
            return;
        }

        // Get order number saved by CheckoutServlet
        String orderNumber =
                (String) session.getAttribute("lastOrder");

        // Check order confirmation
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            p.sendRedirect(q.getContextPath() + "/menu");
            return;
        }

        // Send order number to JSP
        q.setAttribute("orderNumber", orderNumber);

        // Display confirmation page
        q.getRequestDispatcher("/order-success.jsp")
                .forward(q, p);
    }
}
