fix rupee symbol <%@ page import="java.util.*" %>
<%@ include file="includes/header.jspf" %>

<%
    if (!"ADMIN".equals(session.getAttribute("role"))) {
        response.sendError(403);
        return;
    }

    String mode = (String) request.getAttribute("mode");
    String date = (String) request.getAttribute("date");
    String month = (String) request.getAttribute("month");

    if (mode == null) mode = "daily";
    if (date == null) date = "";
    if (month == null) month = "";

    Integer totalValue = (Integer) request.getAttribute("totalOrders");
    Integer completedValue = (Integer) request.getAttribute("completedOrders");
    Integer pendingValue = (Integer) request.getAttribute("pendingOrders");
    Double earningsValue = (Double) request.getAttribute("earnings");

    int totalOrders = totalValue != null ? totalValue : 0;
    int completedOrders = completedValue != null ? completedValue : 0;
    int pendingOrders = pendingValue != null ? pendingValue : 0;
    double earnings = earningsValue != null ? earningsValue : 0.0;

    List<Map<String, Object>> report =
        (List<Map<String, Object>>) request.getAttribute("report");

    if (report == null) {
        report = new ArrayList<>();
    }
%>

<style>
.dashboard {
    max-width: 1100px;
    margin: 30px auto;
    padding: 20px;
}

.dashboard h1 {
    margin-bottom: 8px;
}

.dashboard-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
    gap: 20px;
    margin: 25px 0;
}

.dashboard-card {
    padding: 25px;
    border-radius: 14px;
    background: white;
    box-shadow: 0 3px 15px rgba(0,0,0,0.08);
}

.dashboard-card h3 {
    margin: 0 0 12px;
    color: #666;
    font-size: 16px;
}

.dashboard-card .number {
    font-size: 30px;
    font-weight: bold;
    color: #222;
}

.dashboard-card.earnings {
    border-left: 5px solid #16a34a;
}

.dashboard-card.completed {
    border-left: 5px solid #2563eb;
}

.dashboard-card.pending {
    border-left: 5px solid #f59e0b;
}

.dashboard-card.orders {
    border-left: 5px solid #ea580c;
}

.report-filter {
    background: white;
    padding: 20px;
    border-radius: 12px;
    margin-bottom: 25px;
    box-shadow: 0 3px 12px rgba(0,0,0,0.06);
}

.report-filter form {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 12px;
}

.report-filter input,
.report-filter select,
.report-filter button {
    padding: 10px 14px;
    border: 1px solid #ddd;
    border-radius: 7px;
}

.report-filter button {
    background: #f97316;
    color: white;
    border: none;
    cursor: pointer;
}

.report-table {
    width: 100%;
    border-collapse: collapse;
    background: white;
    border-radius: 10px;
    overflow: hidden;
}

.report-table th,
.report-table td {
    padding: 14px;
    border-bottom: 1px solid #eee;
    text-align: left;
}

.report-table th {
    background: #fff1e6;
}

.status {
    padding: 5px 10px;
    border-radius: 15px;
    background: #f1f5f9;
    font-size: 13px;
}

.no-orders {
    text-align: center !important;
    padding: 25px !important;
    color: #777;
}

@media (max-width: 650px) {
    .dashboard {
        padding: 10px;
    }

    .report-table {
        font-size: 12px;
    }

    .report-table th,
    .report-table td {
        padding: 8px;
    }
}
</style>

<div class="dashboard">

    <h1>Admin Dashboard</h1>

    <p class="muted">
        Monitor canteen orders and earnings.
    </p>

    <!-- Report Filter -->
    <div class="report-filter">

        <form action="${pageContext.request.contextPath}/admin-dashboard"
              method="get">

            <select name="mode" id="reportMode"
                    onchange="changeFilter()">

                <option value="daily"
                    <%= "daily".equals(mode) ? "selected" : "" %>>
                    Daily Report
                </option>

                <option value="monthly"
                    <%= "monthly".equals(mode) ? "selected" : "" %>>
                    Monthly Report
                </option>

            </select>

            <input type="date"
                   name="date"
                   id="dateInput"
                   value="<%= date %>"
                   style="<%= "monthly".equals(mode) ? "display:none;" : "" %>">

            <input type="month"
                   name="month"
                   id="monthInput"
                   value="<%= month %>"
                   style="<%= "monthly".equals(mode) ? "" : "display:none;" %>">

            <button type="submit">View Report</button>

        </form>

    </div>

    <!-- Dashboard Cards -->
    <div class="dashboard-cards">

        <div class="dashboard-card orders">
            <h3>Total Orders</h3>
            <div class="number"><%= totalOrders %></div>
        </div>

        <div class="dashboard-card completed">
            <h3>Completed Orders</h3>
            <div class="number"><%= completedOrders %></div>
        </div>

        <div class="dashboard-card pending">
            <h3>Other Orders</h3>
            <div class="number"><%= pendingOrders %></div>
        </div>

        <div class="dashboard-card earnings">
            <h3>Total Earnings</h3>
            <div class="number">
                &#8377;<%= String.format("%.2f", earnings) %>
            </div>
        </div>

    </div>

    <!-- Report Table -->
    <h2>
        <%= "monthly".equals(mode)
            ? "Monthly Daily Breakdown"
            : "Orders for Selected Day" %>
    </h2>

    <div style="overflow-x:auto;">

        <table class="report-table">

            <% if ("monthly".equals(mode)) { %>

                <tr>
                    <th>Date</th>
                    <th>Total Orders</th>
                    <th>Completed Orders</th>
                    <th>Pending Orders</th>
                    <th>Earnings</th>
                </tr>

                <% for (Map<String, Object> row : report) { %>

                    <tr>
                        <td><%= row.get("reportDate") %></td>

                        <td><%= row.get("totalOrders") %></td>

                        <td><%= row.get("completedOrders") %></td>

                        <td><%= row.get("pendingOrders") %></td>

                        <td>
                            &#8377;<%= String.format("%.2f",
                                ((Number) row.get("earnings")).doubleValue()) %>
                        </td>
                    </tr>

                <% } %>

            <% } else { %>

                <tr>
                    <th>Order Number</th>
                    <th>Order Time</th>
                    <th>Amount</th>
                    <th>Status</th>
                </tr>

                <% for (Map<String, Object> row : report) { %>

                    <tr>
                        <td><%= row.get("orderNumber") %></td>

                        <td><%= row.get("createdAt") %></td>

                        <td>
                            &#8377;<%= String.format("%.2f",
                                ((Number) row.get("total")).doubleValue()) %>
                        </td>

                        <td>
                            <span class="status">
                                <%= row.get("status") %>
                            </span>
                        </td>
                    </tr>

                <% } %>

            <% } %>

            <% if (report.isEmpty()) { %>

                <tr>
                    <td colspan="5" class="no-orders">
                        No orders found for this period.
                    </td>
                </tr>

            <% } %>

        </table>

    </div>

</div>

<script>
function changeFilter() {

    var mode = document.getElementById("reportMode").value;

    document.getElementById("dateInput").style.display =
        mode === "daily" ? "inline-block" : "none";

    document.getElementById("monthInput").style.display =
        mode === "monthly" ? "inline-block" : "none";
}
</script>

<%@ include file="includes/footer.jspf" %>

