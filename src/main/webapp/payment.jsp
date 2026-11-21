
<%@ page import="java.util.*,com.canteen.model.CartItem" %>
<%@ include file="includes/header.jspf" %>

<%
    // Get cart from session as a Map
    Map<Integer, CartItem> cart =
        (Map<Integer, CartItem>) session.getAttribute("cart");

    // Check whether cart is empty
    if (cart == null || cart.isEmpty()) {
        response.sendRedirect(
            request.getContextPath() + "/menu"
        );
        return;
    }

    // Calculate total amount
    double total = 0;

    for (CartItem item : cart.values()) {
        total += item.getSubtotal();
    }
%>

<h1>Secure Payment</h1>
<p class="muted">Choose your preferred payment method.</p>

<% if (request.getAttribute("error") != null) { %>
    <div class="error">
        <%= request.getAttribute("error") %>
    </div>
<% } %>

<div class="payment-container">

    <div class="card payment-card">

        <h2>Order Summary</h2>

        <h1 class="payment-total">
            &#8377;<%= String.format("%.2f", total) %>
        </h1>

        <p>Amount to be paid</p>

        <hr>

        <h2>Select Payment Method</h2>

        <form method="post"
              action="${pageContext.request.contextPath}/payment"
              id="paymentForm">

            <label for="paymentMethod">Payment Method</label>

            <select name="paymentMethod"
                    id="paymentMethod"
                    required
                    onchange="changePaymentMethod()">

                <option value="UPI">UPI Payment</option>
                <option value="CARD">Debit / Credit Card</option>

            </select>

            <!-- UPI Payment -->
            <div id="upiSection">

                <h3>UPI Payment</h3>

                <label for="upiId">Enter UPI ID</label>

                <input type="text"
                       id="upiId"
                       name="upiId"
                       placeholder="example@upi">

                <p class="muted">
                    Demo payment only. No actual UPI transaction
                    will take place.
                </p>

            </div>

            <!-- Card Payment -->
            <div id="cardSection" style="display:none;">

                <h3>Debit / Credit Card</h3>

                <label for="cardName">Cardholder Name</label>

                <input type="text"
                       id="cardName"
                       name="cardName"
                       placeholder="Enter cardholder name">

                <label for="cardNumber">Card Number</label>

                <input type="text"
                       id="cardNumber"
                       name="cardNumber"
                       placeholder="1234 5678 9012 3456"
                       maxlength="23"
                       inputmode="numeric"
                       autocomplete="off">

                <label for="expiry">Expiry Date</label>

                <input type="text"
                       id="expiry"
                       name="expiry"
                       placeholder="MM/YY"
                       maxlength="5"
                       autocomplete="off">

                <label for="cvv">CVV</label>

                <input type="password"
                       id="cvv"
                       name="cvv"
                       placeholder="123"
                       maxlength="4"
                       inputmode="numeric"
                       autocomplete="off">

                <p class="muted">
                    Use fictional test details only.
                    Never enter a real card number or CVV.
                </p>

            </div>

            <button type="submit" class="pay-button">
                Confirm Payment
            </button>

        </form>

    </div>

</div>

<style>
.payment-container {
    max-width: 600px;
    margin: 25px auto;
}

.payment-card {
    padding: 25px;
}

.payment-total {
    color: #198754;
    font-size: 32px;
    margin-bottom: 5px;
}

.payment-card label {
    display: block;
    margin-top: 15px;
    margin-bottom: 6px;
    font-weight: 600;
}

.payment-card input,
.payment-card select {
    width: 100%;
    padding: 12px;
    border: 1px solid #ccc;
    border-radius: 7px;
    box-sizing: border-box;
}

.payment-card h3 {
    margin-top: 20px;
}

.pay-button {
    width: 100%;
    margin-top: 25px;
    padding: 14px;
    background: #198754;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    cursor: pointer;
}

.pay-button:hover {
    background: #146c43;
}
</style>

<script>
function changePaymentMethod() {

    const method =
        document.getElementById("paymentMethod").value;

    const upiSection =
        document.getElementById("upiSection");

    const cardSection =
        document.getElementById("cardSection");

    const upiId =
        document.getElementById("upiId");

    const cardName =
        document.getElementById("cardName");

    const cardNumber =
        document.getElementById("cardNumber");

    const expiry =
        document.getElementById("expiry");

    const cvv =
        document.getElementById("cvv");

    if (method === "UPI") {

        upiSection.style.display = "block";
        cardSection.style.display = "none";

        upiId.required = true;

        cardName.required = false;
        cardNumber.required = false;
        expiry.required = false;
        cvv.required = false;

    } else {

        upiSection.style.display = "none";
        cardSection.style.display = "block";

        upiId.required = false;

        cardName.required = true;
        cardNumber.required = true;
        expiry.required = true;
        cvv.required = true;
    }
}

changePaymentMethod();
</script>

<%@ include file="includes/footer.jspf" %>

