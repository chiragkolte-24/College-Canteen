package com.canteen.servlet;

import com.canteen.util.Auth;
import com.canteen.util.DBConnection;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/order-status")
public class OrderStatusServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest q, HttpServletResponse p)
            throws IOException {

        // Check admin authentication
        if (!Auth.admin(q)) {
            p.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int id = Integer.parseInt(q.getParameter("orderId"));
        String status = q.getParameter("status");

        String sql = "UPDATE orders SET status=?, "
                + "updated_at=NOW(), "
                + "picked_up_at=IF(?='Picked Up', NOW(), picked_up_at) "
                + "WHERE id=? AND ("
                + "(status='Placed' AND ?='Accepted') OR "
                + "(status='Accepted' AND ?='Preparing') OR "
                + "(status='Preparing' AND ?='Ready') OR "
                + "(status='Ready' AND ?='Picked Up'))";

        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement s = c.prepareStatement(sql)
        ) {

            s.setString(1, status);
            s.setString(2, status);
            s.setInt(3, id);
            s.setString(4, status);
            s.setString(5, status);
            s.setString(6, status);
            s.setString(7, status);

            int updated = s.executeUpdate();

            if (updated == 0) {
                System.out.println(
                    "Order status not updated. Check order ID or status transition."
                );
            }

        } catch (SQLException e) {
            throw new IOException("Error updating order status", e);
        }

        p.sendRedirect(q.getContextPath() + "/admin");
    }
}
