package com.canteen.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest q, HttpServletResponse p)
            throws IOException {

        HttpSession session = q.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        p.sendRedirect(q.getContextPath() + "/login");
    }
}