
<%@ page import="java.util.*" %>

<%@ include file="includes/header.jspf" %>

<h1>Menu Management</h1>

<!-- Add Menu Item -->
<div class="card">

    <h2>Add Menu Item</h2>

    <form class="form-grid"
          method="post"
          action="${pageContext.request.contextPath}/menu-admin">

        <input type="hidden" name="action" value="add">

        <input
            name="name"
            placeholder="Item name"
            required
        >

        <input
            name="category"
            placeholder="Category"
            required
        >

        <input
            name="price"
            type="number"
            min="0.01"
            step="0.01"
            placeholder="Price"
            required
        >

        <input
            name="description"
            placeholder="Description"
        >

        <!-- Internet Image URL -->
        <label for="image_url">Food Image URL</label>

        <input
            type="url"
            id="image_url"
            name="image_url"
            placeholder="Paste food image URL here"
            required
        >

        <!-- Image Preview -->
        <img
            id="imagePreview"
            src=""
            alt="Food Image Preview"
            style="display:none; width:180px; height:130px; object-fit:cover; border-radius:10px;"
        >

        <button type="submit">
            Add Menu Item
        </button>

    </form>

</div>


<!-- Existing Menu Items -->
<div class="card">

    <h2>Existing Items</h2>

    <table>

        <tr>
            <th>Image</th>
            <th>Name</th>
            <th>Category</th>
            <th>Price</th>
            <th>Availability</th>
            <th>Action</th>
        </tr>

        <%
            List<Map> items =
                (List<Map>) request.getAttribute("items");

            if (items != null) {
                for (Map m : items) {
        %>

        <tr>

            <!-- Food Image -->
            <td>

                <% if (m.get("image_url") != null
                       && !String.valueOf(m.get("image_url")).isEmpty()) { %>

                    <img
                        src="<%= m.get("image_url") %>"
                        alt="<%= m.get("name") %>"
                        style="width:80px; height:60px; object-fit:cover; border-radius:8px;"
                    >

                <% } else { %>

                    No Image

                <% } %>

            </td>

            <td><%= m.get("name") %></td>

            <td><%= m.get("category") %></td>

            <td>?<%= m.get("price") %></td>

            <td>
                <%= Boolean.TRUE.equals(m.get("available"))
                    || "1".equals(String.valueOf(m.get("available")))
                    ? "Available"
                    : "Unavailable" %>
            </td>

            <td>

                <form
                    method="post"
                    action="${pageContext.request.contextPath}/menu-admin">

                    <input
                        type="hidden"
                        name="action"
                        value="toggle"
                    >

                    <input
                        type="hidden"
                        name="id"
                        value="<%= m.get("id") %>"
                    >

                    <button type="submit" class="secondary">
                        Toggle Availability
                    </button>

                </form>

            </td>

        </tr>

        <%
                }
            }
        %>

    </table>

</div>


<!-- Image Preview Script -->
<script>

    document.getElementById("image_url").addEventListener("input", function() {

        const url = this.value.trim();

        const preview = document.getElementById("imagePreview");

        if (url) {

            preview.src = url;
            preview.style.display = "block";

        } else {

            preview.style.display = "none";

        }

    });

</script>

<%@ include file="includes/footer.jspf" %>


