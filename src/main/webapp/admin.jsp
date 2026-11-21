<
<%@ page import="java.util.*" %>
<%@ include file="includes/header.jspf" %>

<h1>Canteen Dashboard</h1>
<p class="muted">
    Review incoming orders and update each order through the preparation workflow.
</p>

<div class="card">
    <table>
        <tr>
            <th>Order</th>
            <th>Student</th>
            <th>Items</th>
            <th>Total</th>
            <th>Status</th>
            <th>Action</th>
        </tr>

        <%
            List<Map> orders =
                (List<Map>) request.getAttribute("orders");

            if (orders != null) {
                for (Map o : orders) {

                    String st = (String) o.get("status");

                    String next =
                        "Placed".equals(st) ? "Accepted" :
                        "Accepted".equals(st) ? "Preparing" :
                        "Preparing".equals(st) ? "Ready" :
                        "Ready".equals(st) ? "Picked Up" : null;
        %>

        <tr>
            <td>
                <b><%= o.get("order_number") %></b>
                <br>
                <small><%= o.get("created_at") %></small>
            </td>

            <td><%= o.get("customer") %></td>

            <td><%= o.get("items") %></td>

            <td>&#8377;&nbsp;<%= o.get("total") %></td>

            <td>
                <span class="status"><%= st %></span>
            </td>

            <td>
                <% if (next != null) { %>

                <form method="post"
                      action="<%= request.getContextPath() %>/update-order-status">

                    <input type="hidden"
                           name="orderId"
                           value="<%= o.get("id") %>">

                    <input type="hidden"
                           name="status"
                           value="<%= next %>">

                    <button type="submit"><%= next %></button>
                </form>

                <% } else { %>
                    Completed
                <% } %>
            </td>
        </tr>

        <%
                }
            }
        %>
    </table>
</div>

<%@ include file="includes/footer.jspf" %>
