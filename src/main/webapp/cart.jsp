<%@ page import="java.util.*,com.canteen.model.CartItem" pageEncoding="UTF-8" %>
<%@ include file="includes/header.jspf" %>

<h1>Your Cart</h1>

<%
    String cartMessage =
        (String) session.getAttribute("cartMessage");

    if (cartMessage != null) {
%>
    <div class="success">
        <%= cartMessage %>
    </div>
<%
        session.removeAttribute("cartMessage");
    }

    Collection<CartItem> cart =
        (Collection<CartItem>) request.getAttribute("cart");

    Number totalValue =
        (Number) request.getAttribute("total");

    double total = totalValue == null
        ? 0.0
        : totalValue.doubleValue();

    if (cart == null || cart.isEmpty()) {
%>

    <div class="card">
        <h3>Your cart is empty!</h3>
        <p>Add some delicious food from our menu.</p>

        <a href="${pageContext.request.contextPath}/menu">
            Browse Menu
        </a>
    </div>

<%
    } else {
%>

    <div class="card">

        <h2>Cart Items</h2>

        <div class="table-container">

            <table>
                <thead>
                    <tr>
                        <th>Item</th>
                        <th>Price</th>
                        <th>Quantity</th>
                        <th>Subtotal</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>

                <%
                    for (CartItem i : cart) {
                %>

                    <tr id="row-<%= i.id %>">

                        <!-- Item Name -->
                        <td>
                            <%= i.name %>
                        </td>

                        <!-- Price -->
                        <td data-price="<%= i.price %>">
                            &#8377;<%= String.format(Locale.US, "%.2f", i.price) %>
                        </td>

                        <!-- Quantity -->
                        <td>

                            <div class="quantity-controls">

                                <button type="button"
                                        class="quantity-btn minus"
                                        onclick="updateQuantity(<%= i.id %>, -1)"
                                        aria-label="Decrease quantity">
                                    &#8722;
                                </button>

                                <span id="qty-<%= i.id %>">
                                    <%= i.quantity %>
                                </span>

                                <button type="button"
                                        class="quantity-btn plus"
                                        onclick="updateQuantity(<%= i.id %>, 1)"
                                        aria-label="Increase quantity">
                                    +
                                </button>

                            </div>

                        </td>

                        <!-- Subtotal -->
                        <td id="subtotal-<%= i.id %>">
                            &#8377;<%= String.format(Locale.US, "%.2f", i.getSubtotal()) %>
                        </td>

                        <!-- Remove -->
                        <td>

                            <form method="post"
                                  action="${pageContext.request.contextPath}/cart">

                                <input type="hidden"
                                       name="action"
                                       value="remove">

                                <input type="hidden"
                                       name="itemId"
                                       value="<%= i.id %>">

                                <button type="submit"
                                        class="remove-btn"
                                        onclick="return confirm('Remove this item from your cart?');">
                                    Remove
                                </button>

                            </form>

                        </td>

                    </tr>

                <%
                    }
                %>

                </tbody>
            </table>

        </div>

        <!-- Cart Total -->
        <h2 class="right">
            Total:
            &#8377;<span id="cartTotal"><%= String.format(Locale.US, "%.2f", total) %></span>
        </h2>
<div class="cancellation-notice">
    <strong>⚠ No Cancellation Policy:</strong>
    Once an order is placed, it cannot be cancelled.
    Please review your items and quantity carefully before proceeding to payment.
</div>
        <!-- Cart Actions -->
        <div class="cart-actions">

            <a href="${pageContext.request.contextPath}/menu"
               class="cart-btn continue-btn">
                + Add More Items
            </a>

            <form method="post"
                  action="${pageContext.request.contextPath}/checkout">

                <button type="submit"
                        class="cart-btn order-btn">
                    Proceed to Payment
                </button>

            </form>

        </div>

        <small>
            Choose UPI or Debit/Credit Card on the next page.
        </small>

    </div>

<%
    }
%>


<style>

/* Table */
.table-container {
    width: 100%;
    overflow-x: auto;
}

table {
    width: 100%;
    border-collapse: collapse;
}

table th,
table td {
    padding: 13px;
    text-align: left;
    border-bottom: 1px solid #ddd;
}

/* Quantity Controls */
.quantity-controls {
    display: flex;
    align-items: center;
    gap: 12px;
}

.quantity-controls span {
    min-width: 20px;
    text-align: center;
    font-weight: bold;
}

.quantity-btn {
    width: 36px;
    height: 36px;
    border: none;
    border-radius: 7px;
    color: white;
    font-size: 20px;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
}

.minus {
    background: #dc3545;
}

.plus {
    background: #198754;
}

/* Remove Button */
.remove-btn {
    padding: 8px 12px;
    background: #dc3545;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
}

/* Cart Actions */
.cart-actions {
    display: flex;
    gap: 15px;
    flex-wrap: wrap;
    margin-top: 25px;
    align-items: center;
}

.cart-actions form {
    margin: 0;
}

.cart-btn {
    display: inline-block;
    padding: 12px 22px;
    border: none;
    border-radius: 8px;
    text-decoration: none;
    cursor: pointer;
    font-size: 15px;
    font-weight: bold;
}

.continue-btn {
    background: #0d6efd;
    color: white;
}

.order-btn {
    background: #198754;
    color: white;
}

/* Hover Effects */
.cart-btn:hover,
.remove-btn:hover,
.quantity-btn:hover {
    opacity: 0.85;
}

.quantity-btn:disabled {
    opacity: 0.4;
    cursor: not-allowed;
}

/* Mobile Responsive */
@media(max-width: 600px) {

    .cart-actions {
        flex-direction: column;
        align-items: stretch;
    }

    .cart-actions form,
    .cart-actions button,
    .cart-actions a {
        width: 100%;
        box-sizing: border-box;
        text-align: center;
    }

}

</style>


<script>

const contextPath = "${pageContext.request.contextPath}";

function updateQuantity(itemId, change) {

    const quantityElement =
        document.getElementById("qty-" + itemId);

    const row =
        document.getElementById("row-" + itemId);

    if (!quantityElement || !row) {
        return;
    }

    let quantity =
        parseInt(quantityElement.innerText.trim(), 10);

    quantity += change;

    // Minimum quantity
    if (quantity < 1) {
        return;
    }

    // Maximum quantity
    if (quantity > 99) {
        return;
    }

    const buttons = row.querySelectorAll(".quantity-btn");

    buttons.forEach(button => button.disabled = true);

    // Get price directly from data-price
    const price = parseFloat(
        row.cells[1].dataset.price
    );

    // Send updated quantity to CartServlet
    const formData = new URLSearchParams();

    formData.append("action", "update");
    formData.append("itemId", itemId);
    formData.append("quantity", quantity);

    fetch(contextPath + "/cart", {

        method: "POST",

        headers: {
            "Content-Type":
                "application/x-www-form-urlencoded"
        },

        body: formData.toString()

    })

    .then(response => {

        if (!response.ok) {
            throw new Error("Unable to update cart");
        }

        return response.text();

    })

    .then(() => {

        // Update quantity
        quantityElement.innerText = quantity;

        // Calculate subtotal
        const subtotal = price * quantity;

        document.getElementById(
            "subtotal-" + itemId
        ).innerText = "\u20B9" + subtotal.toFixed(2);

        // Recalculate total
        let total = 0;

        document.querySelectorAll("[id^='subtotal-']")
            .forEach(element => {

                const value = parseFloat(
                    element.innerText.replace(/[^\d.-]/g, "")
                );

                total += isNaN(value) ? 0 : value;

            });

        // Update total
        document.getElementById("cartTotal").innerText =
            total.toFixed(2);

    })

    .catch(error => {

        alert("Could not update quantity. Please refresh the page.");

    })

    .finally(() => {

        buttons.forEach(button => button.disabled = false);

    });

}

</script>

<%@ include file="includes/footer.jspf" %>



