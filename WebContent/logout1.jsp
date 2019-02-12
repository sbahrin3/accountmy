<%
String visitor = session.getAttribute("_portal_visitor") != null ? 
			     (String) session.getAttribute("_portal_visitor") : "anon";
session.invalidate();
%>
<script>
document.location = "logout3.jsp?visitor=<%=visitor%>";
</script>