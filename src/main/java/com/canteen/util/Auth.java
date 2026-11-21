
package com.canteen.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class Auth {

    public static boolean loggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        return session != null
                && session.getAttribute("userId") != null;
    }

    public static boolean admin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        return loggedIn(request)
                && "ADMIN".equals(session.getAttribute("role"));
    }

    public static boolean student(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        return loggedIn(request)
                && "STUDENT".equals(session.getAttribute("role"));
    }

    public static void requireLogin(
            HttpServletRequest request,
            HttpServletResponse response,
            boolean adminOnly) throws IOException {

        if (!loggedIn(request)) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        if (adminOnly && !admin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
    }
}
