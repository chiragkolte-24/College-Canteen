
package com.canteen.servlet;

import com.canteen.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/register.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (name == null || email == null || password == null
                || name.trim().isEmpty()
                || email.trim().isEmpty()
                || password.length() < 6) {

            request.setAttribute("error",
                    "Enter valid details. Password must be at least 6 characters.");

            request.getRequestDispatcher("/register.jsp")
                   .forward(request, response);
            return;
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO users(name,email,password_hash,role) "
                     + "VALUES(?,?,?,'STUDENT')")) {

            statement.setString(1, name.trim());
            statement.setString(2, email.trim().toLowerCase());
            statement.setString(3,
                    BCrypt.hashpw(password, BCrypt.gensalt()));

            statement.executeUpdate();

            response.sendRedirect(
                    request.getContextPath() + "/login?registered=1");

        } catch (SQLIntegrityConstraintViolationException e) {

            request.setAttribute("error", "Email already registered.");

            request.getRequestDispatcher("/register.jsp")
                   .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Registration failed.", e);
        }
    }
}
