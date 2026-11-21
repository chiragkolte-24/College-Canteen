```jsp
<%@ page import="java.util.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ include file="includes/header.jspf" %>

<h1>My Orders</h1>

<div class="card">
    <table>
        <tr>
            <th>Order no.</th>
            <th>Items</th>
            <th>Total</th>
            <th>Status</th>
            <th>Placed</th>
        </tr>

        <%
            List<Map> orders = (List<Map>) request.getAttribute("orders");

            if (orders != null) {
                for (Map o : orders) {
        %>
        <tr>
            <td><%= o.get("order_number") %></td>

            <td><%= o.get("items") %></td>

            <td>&#8377;<%= o.get("total") %></td>

            <td>
                <span class="status"><%= o.get("status") %></span>

                <% if ("Ready".equals(o.get("status"))) { %>
                    <div class="success">
                        Your order is ready for pickup!
                    </div>
                <% } %>
            </td>

            <td><%= o.get("created_at") %></td>
        </tr>
        <%
                }
            }
        %>
    </table>
</div>

<p class="muted">
    Refresh this page to see the latest order status.
</p>

<%@ include file="includes/footer.jspf" %>
