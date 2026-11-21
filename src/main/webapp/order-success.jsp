
<%@ include file="includes/header.jspf" %>

<div class="card order-success">

    <div class="success-icon">
        ?
    </div>

    <h1>Order Placed Successfully!</h1>

    <p>
        Thank you for ordering from our College Canteen.
        Your order has been received successfully.
    </p>

    <%
        String orderNumber =
            (String) session.getAttribute("lastOrder");
    %>

    <% if (orderNumber != null) { %>

        <div class="order-details">
            <h3>Order Details</h3>

            <p>
                <strong>Order Number:</strong>
                <%= orderNumber %>
            </p>

            <p>
                <strong>Order Status:</strong>
                Placed
            </p>

            <p>
                <strong>Payment:</strong>
                Pay at Pickup
            </p>
        </div>

    <% } %>

    <p>
        Please collect your order from the canteen counter.
    </p>

    <div class="order-actions">

        <a href="${pageContext.request.contextPath}/menu"
           class="btn">
            Order More Food
        </a>

        <a href="${pageContext.request.contextPath}/my-orders"
           class="btn secondary">
            View My Orders
        </a>

    </div>

</div>

<style>
.order-success {
    max-width: 650px;
    margin: 40px auto;
    padding: 30px;
    text-align: center;
}

.success-icon {
    width: 75px;
    height: 75px;
    line-height: 75px;
    border-radius: 50%;
    background: #d4edda;
    color: #155724;
    font-size: 45px;
    font-weight: bold;
    margin: 0 auto 20px;
}

.order-details {
    background: #f5f5f5;
    padding: 20px;
    margin: 25px 0;
    border-radius: 10px;
}

.order-actions {
    display: flex;
    justify-content: center;
    gap: 12px;
    flex-wrap: wrap;
    margin-top: 25px;
}

.order-actions a {
    text-decoration: none;
    padding: 12px 20px;
    border-radius: 8px;
}

.order-actions .btn {
    background: #198754;
    color: white;
}

.order-actions .secondary {
    background: #555;
    color: white;
}
</style>

<%@ include file="includes/footer.jspf" %>
