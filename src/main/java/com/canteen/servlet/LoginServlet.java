package com.canteen.servlet;

import com.canteen.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest q, HttpServletResponse p)
            throws ServletException, IOException {

        q.getRequestDispatcher("/login.jsp").forward(q, p);
    }

    @Override
    protected void doPost(HttpServletRequest q, HttpServletResponse p)
            throws ServletException, IOException {

        String email = q.getParameter("email");
        String password = q.getParameter("password");

        String sql = "SELECT id, name, password_hash, role "
                   + "FROM users WHERE email=?";

        try (
            Connection c = DBConnection.getConnection();
            PreparedStatement s = c.prepareStatement(sql)
        ) {

            s.setString(1, email);

            try (ResultSet r = s.executeQuery()) {

                if (r.next() && BCrypt.checkpw(
                        password, r.getString("password_hash"))) {

                    HttpSession old = q.getSession(false);

                    if (old != null) {
                        old.invalidate();
                    }

                    HttpSession session = q.getSession(true);

                    session.setAttribute("userId", r.getInt("id"));
                    session.setAttribute("userName", r.getString("name"));
                    session.setAttribute("role", r.getString("role"));

                    if ("ADMIN".equals(r.getString("role"))) {
                        p.sendRedirect(q.getContextPath() + "/admin");
                    } else {
                        p.sendRedirect(q.getContextPath() + "/menu");
                    }

                    return;
                }
            }

            q.setAttribute("error", "Invalid email or password.");
            q.getRequestDispatcher("/login.jsp").forward(q, p);

        } catch (SQLException e) {
            throw new ServletException("Login database error", e);
        }
    }
}
