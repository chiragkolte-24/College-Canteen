
<%@ include file="includes/header.jspf" %>

<%
    String orderNumber =
            (String) session.getAttribute("lastOrder");

    String paymentMethod =
            (String) session.getAttribute("paymentMethod");

    String transactionId =
            (String) session.getAttribute("paymentTransactionId");

    Object amount =
            session.getAttribute("lastOrderTotal");

    if (orderNumber == null || paymentMethod == null
            || transactionId == null || amount == null) {

        response.sendRedirect(
                request.getContextPath() + "/menu"
        );
        return;
    }
%>

<div class="card success-card">

    <div class="success-icon">&#10004;</div>

    <h1>Payment Successful!</h1>

    <p>
        Your payment simulation was completed successfully.
        Your canteen order has been placed.
    </p>

    <div class="payment-details">

        <h2>Payment Details</h2>

        <p>
            <strong>Order Number:</strong>
            <%= orderNumber %>
        </p>

        <p>
            <strong>Transaction ID:</strong>
            <%= transactionId %>
        </p>

        <p>
            <strong>Payment Method:</strong>
            <%= paymentMethod %>
        </p>

        <p>
            <strong>Amount:</strong>
            &#8377;<%= amount %>
        </p>

        <p>
            <strong>Payment Status:</strong>
            <span class="paid">SUCCESS</span>
        </p>

        <p>
            <strong>Order Status:</strong>
            Placed
        </p>

    </div>

    <p>
        Please collect your order from the canteen counter.
    </p>

    <div class="success-actions">

        <a href="${pageContext.request.contextPath}/menu"
           class="success-btn">
            Order More Food
        </a>

    </div>

</div>

<style>
.success-card {
    max-width: 650px;
    margin: 40px auto;
    padding: 30px;
    text-align: center;
}

.success-icon {
    width: 80px;
    height: 80px;
    line-height: 80px;
    border-radius: 50%;
    background: #d4edda;
    color: #198754;
    font-size: 48px;
    font-weight: bold;
    margin: 0 auto 20px;
}

.payment-details {
    background: #f5f5f5;
    padding: 20px;
    border-radius: 10px;
    margin: 25px 0;
    text-align: left;
}

.payment-details p {
    margin: 12px 0;
}

.paid {
    color: #198754;
    font-weight: bold;
}

.success-btn {
    display: inline-block;
    padding: 13px 25px;
    background: #198754;
    color: white;
    text-decoration: none;
    border-radius: 8px;
}

.success-btn:hover {
    background: #146c43;
}
</style>

<%@ include file="includes/footer.jspf" %>

