<%@ page import="java.util.*,com.canteen.model.*" pageEncoding="UTF-8" %>
<%@ include file="includes/header.jspf" %>

<style>

/* MAIN MENU */
.menu-wrapper {
    max-width: 1250px;
    margin: 30px auto;
    padding: 0 20px;
}

/* BANNER */
.menu-banner {
    background: linear-gradient(120deg, #fc8019, #ff9f43);
    color: white;
    padding: 35px;
    border-radius: 18px;
    margin-bottom: 30px;
}

.menu-banner h1 {
    font-size: 32px;
    margin-bottom: 10px;
}

.menu-banner p {
    font-size: 16px;
}

/* CATEGORY SECTION */
.category-section {
    margin: 30px 0;
}

.category-section h2 {
    margin-bottom: 18px;
}

.category-list {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
}

/* CATEGORY BUTTONS */
.category-btn {
    padding: 11px 22px;
    border: 1px solid #ddd;
    border-radius: 25px;
    background: white;
    color: #282c3f !important;
    cursor: pointer;
    white-space: nowrap;
    font-size: 14px;
    font-weight: 600;
    transition: 0.2s;
}

.category-btn.active,
.category-btn:hover {
    background: #fc8019;
    color: white !important;
    border-color: #fc8019;
}

/* FOOD GRID */
.food-grid {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 22px;
}

/* FOOD CARD */
.food-card {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    border: 1px solid #eee;
    box-shadow: 0 3px 12px rgba(0,0,0,0.06);
    transition: 0.3s;
    min-width: 0;
}

.food-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 22px rgba(0,0,0,0.12);
}

/* FOOD IMAGE */
.food-image-wrapper {
    height: 190px;
    overflow: hidden;
    background: #f5f5f5;
}

.food-image {
    width: 100%;
    height: 190px;
    object-fit: cover;
    display: block;
}

.food-image-placeholder {
    height: 190px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 55px;
}

/* FOOD DETAILS */
.food-details {
    padding: 16px;
}

.food-category {
    color: #fc8019;
    font-size: 12px;
    font-weight: bold;
    text-transform: uppercase;
}

.food-name {
    font-size: 19px;
    margin: 8px 0;
    color: #282c3f;
}

.food-description {
    color: #686b78;
    font-size: 14px;
    min-height: 38px;
    margin-bottom: 15px;
}

/* PRICE AND ADD BUTTON */
.food-bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 8px;
}

.food-price {
    font-size: 18px;
    font-weight: bold;
    color: #282c3f;
}

.add-btn {
    background: white;
    color: #fc8019 !important;
    border: 1px solid #fc8019;
    border-radius: 8px;
    padding: 10px 18px;
    font-weight: bold;
    cursor: pointer;
}

.add-btn:hover {
    background: #fc8019;
    color: white !important;
}

/* NO RESULTS */
.no-results {
    display: none;
    text-align: center;
    padding: 40px;
    color: #777;
}

/* RESPONSIVE */
@media(max-width: 1000px) {
    .food-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }
}

@media(max-width: 600px) {
    .food-grid {
        grid-template-columns: 1fr;
    }

    .menu-banner {
        padding: 25px;
    }

    .menu-banner h1 {
        font-size: 25px;
    }
}

</style>

<div class="menu-wrapper">

    <!-- BANNER -->
    <div class="menu-banner">

        <h1>Hungry? We've got you! 🍔</h1>

        <p>
            Delicious food, freshly prepared.
            Order online and pick up at  canteen.
        </p>

    </div>

    <!-- CATEGORY FILTER -->
    <div class="category-section">

        <h2>Explore Categories</h2>

        <div class="category-list">

            <button type="button"
                    class="category-btn active"
                    onclick="filterCategory('All', this)">
                All
            </button>

            <%
                List<Map> items =
                    (List<Map>) request.getAttribute("items");

                Set<String> categories = new LinkedHashSet<>();

                if (items != null) {
                    for (Map item : items) {
                        if (item.get("category") != null) {
                            String cat = String.valueOf(item.get("category")).trim();

                            if (!cat.isEmpty()) {
                                categories.add(cat);
                            }
                        }
                    }
                }

                for (String category : categories) {
            %>

                <button type="button"
                        class="category-btn"
                        data-category="<%= category %>"
                        onclick="filterCategory(this.dataset.category, this)">
                    <%= category %>
                </button>

            <%
                }
            %>

        </div>

    </div>

    <!-- FOOD ITEMS -->
    <h2 style="margin-bottom:20px;">
        Recommended Food
    </h2>

    <div class="food-grid" id="foodGrid">

        <%
            if (items != null && !items.isEmpty()) {

                for (Map item : items) {

                    String imageUrl = item.get("image_url") == null
                            ? ""
                            : String.valueOf(item.get("image_url")).trim();

                    String foodName =
                            String.valueOf(item.get("name"));

                    String category =
                            item.get("category") == null
                            ? ""
                            : String.valueOf(item.get("category")).trim();

                    String description =
                            item.get("description") == null
                            ? ""
                            : String.valueOf(item.get("description"));

                    double price =
                            ((Number) item.get("price")).doubleValue();
        %>

            <article class="food-card"
                     data-category="<%= category %>">

                <!-- FOOD IMAGE -->
                <div class="food-image-wrapper">

                    <% if (!imageUrl.isEmpty()) { %>

                        <img
                            src="<%= imageUrl %>"
                            alt="<%= foodName %>"
                            class="food-image"
                            loading="lazy"
                            onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';"
                        >

                        <div class="food-image-placeholder"
                             style="display:none;">
                            🍽️
                        </div>

                    <% } else { %>

                        <div class="food-image-placeholder">
                            🍽️
                        </div>

                    <% } %>

                </div>

                <!-- FOOD DETAILS -->
                <div class="food-details">

                    <span class="food-category">
                        <%= category %>
                    </span>

                    <h3 class="food-name">
                        <%= foodName %>
                    </h3>

                    <p class="food-description">
                        <%= description %>
                    </p>

                    <!-- PRICE AND ADD BUTTON -->
                    <div class="food-bottom">

                        <span class="food-price">
                            &#8377;<%= String.format(Locale.US, "%.2f", price) %>
                        </span>

                        <form method="post"
                              action="${pageContext.request.contextPath}/cart">

                            <input type="hidden"
                                   name="itemId"
                                   value="<%= item.get("id") %>">

                            <button type="submit" class="add-btn">
                                ADD +
                            </button>

                        </form>

                    </div>

                </div>

            </article>

        <%
                }

            } else {
        %>

            <p>No food items are currently available.</p>

        <%
            }
        %>

    </div>

    <div class="no-results" id="noResults">

        <h3>😕 No food found</h3>
        <p>No items available in this category.</p>

    </div>

</div>

<script>

function filterCategory(category, button) {

    // Remove active class from all buttons
    document.querySelectorAll(".category-btn")
        .forEach(btn => btn.classList.remove("active"));

    // Set selected button active
    button.classList.add("active");

    const cards =
        document.querySelectorAll(".food-card");

    let visibleCount = 0;

    cards.forEach(card => {

        const foodCategory =
            card.dataset.category.trim().toLowerCase();

        const selectedCategory =
            category.trim().toLowerCase();

        if (selectedCategory === "all" ||
            foodCategory === selectedCategory) {

            card.style.display = "block";
            visibleCount++;

        } else {

            card.style.display = "none";

        }

    });

    document.getElementById("noResults").style.display =
        visibleCount === 0 ? "block" : "none";

}

</script>

<%@ include file="includes/footer.jspf" %>
