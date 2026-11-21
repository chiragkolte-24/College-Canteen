<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes/header.jspf" %>

<%
    List<Map<String, Object>> notifications =
        (List<Map<String, Object>>) request.getAttribute("notifications");

    Integer unreadCount =
        (Integer) request.getAttribute("unreadCount");

    if (notifications == null) {
        notifications = new ArrayList<>();
    }

    if (unreadCount == null) {
        unreadCount = 0;
    }
%>

<style>
    .notification-container {
        max-width: 850px;
        margin: 35px auto;
        padding: 20px;
    }

    .notification-heading {
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-wrap: wrap;
        gap: 12px;
        margin-bottom: 20px;
    }

    .notification-heading h1 {
        margin: 0;
        color: #263238;
    }

    .unread-summary {
        background: #fff0e0;
        color: #d96b00;
        padding: 10px 18px;
        border-radius: 25px;
        font-weight: bold;
    }

    .notification-card {
        background: white;
        padding: 20px;
        margin-bottom: 15px;
        border-radius: 12px;
        border: 1px solid #e5e5e5;
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 15px;
        box-shadow: 0 3px 10px rgba(0,0,0,0.04);
        transition: 0.2s;
    }

    .notification-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(0,0,0,0.08);
    }

    .notification-card.unread {
        background: #fff8ef;
        border-left: 5px solid #ff8500;
    }

    .notification-content {
        flex: 1;
    }

    .notification-message {
        font-size: 16px;
        color: #263238;
        font-weight: 500;
        margin-bottom: 10px;
    }

    .notification-time {
        font-size: 13px;
        color: #888;
        margin-bottom: 8px;
    }

    .notification-status {
        display: inline-block;
        font-size: 11px;
        color: white;
        background: #e53935;
        padding: 4px 9px;
        border-radius: 15px;
        font-weight: bold;
    }

    .read-label {
        color: #388e3c;
        font-size: 12px;
        font-weight: bold;
    }

    .read-button {
        background: #ff8500;
        color: white;
        padding: 9px 15px;
        border: none;
        border-radius: 7px;
        cursor: pointer;
        font-weight: bold;
        white-space: nowrap;
    }

    .read-button:hover {
        background: #e87500;
    }

    .empty-notifications {
        text-align: center;
        padding: 55px 20px;
        background: white;
        border-radius: 15px;
        border: 1px solid #eee;
    }

    .empty-notifications .empty-icon {
        font-size: 55px;
        margin-bottom: 15px;
    }

    .empty-notifications h3 {
        color: #333;
    }

    .empty-notifications p {
        color: #888;
    }

    .back-menu {
        display: inline-block;
        margin-top: 15px;
        background: #ff8500;
        color: white;
        padding: 11px 20px;
        border-radius: 7px;
        text-decoration: none;
        font-weight: bold;
    }

    .back-menu:hover {
        background: #e87500;
    }

    @media(max-width:600px) {
        .notification-container {
            padding: 12px;
        }

        .notification-card {
            align-items: flex-start;
            flex-direction: column;
        }

        .notification-heading {
            align-items: flex-start;
            flex-direction: column;
        }
    }
</style>

<div class="notification-container">

    <div class="notification-heading">

        <h1>🔔 My Notifications</h1>

        <div class="unread-summary">
            <%= unreadCount %> Unread
        </div>

    </div>

    <p class="muted">
        Stay updated about your canteen orders.
    </p>

    <hr>

    <% if (notifications.isEmpty()) { %>

        <div class="empty-notifications">

            <div class="empty-icon">🔔</div>

            <h3>No Notifications Yet</h3>

            <p>
                You will receive notifications when your order
                is accepted or ready for pickup.
            </p>

        </div>

    <% } else { %>

        <% for (Map<String, Object> n : notifications) {

            boolean isRead = Boolean.TRUE.equals(n.get("isRead"));

        %>

            <div class="notification-card <%= isRead ? "" : "unread" %>">

                <div class="notification-content">

                    <div class="notification-message">
                        🔔 <%= n.get("message") %>
                    </div>

                    <div class="notification-time">

                        Order #<%= n.get("orderId") %>

                        &nbsp; | &nbsp;

                        <%= n.get("createdAt") %>

                    </div>

                    <% if (!isRead) { %>

                        <span class="notification-status">
                            ● NEW
                        </span>

                    <% } else { %>

                        <span class="read-label">
                            ✓ Read
                        </span>

                    <% } %>

                </div>

                <% if (!isRead) { %>

                    <form method="post"
                          action="<%= request.getContextPath() %>/notifications">

                        <input type="hidden"
                               name="notificationId"
                               value="<%= n.get("id") %>">

                        <button type="submit" class="read-button">
                            ✓ Mark as Read
                        </button>

                    </form>

                <% } %>

            </div>

        <% } %>

    <% } %>

    <a href="<%= request.getContextPath() %>/menu"
       class="back-menu">
        ← Back to Menu
    </a>

</div>

<%@ include file="includes/footer.jspf" %>