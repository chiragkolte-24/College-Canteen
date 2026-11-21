<%@ include file="includes/header.jspf" %><section class="auth card"><h1>Welcome back</h1><p>Login to order your favourite food.</p>
<% if(request.getAttribute("error")!=null){ %><div class="error">${error}</div><% } %>
<% if("1".equals(request.getParameter("registered"))){ %><div class="success">Registration successful. Please login.</div><% } %>
<form method="post" action="${pageContext.request.contextPath}/login"><label>Email</label><input type="email" name="email" required><label>Password</label><input type="password" name="password" required><button>Login</button></form><p>New student? <a href="${pageContext.request.contextPath}/register">Create account</a></p></section><%@ include file="includes/footer.jspf" %>
