<%
String visitor = request.getParameter("visitor") != null ? request.getParameter("visitor") : "anon";
%>
<center>
You have successfully logout from the system!!
<br>
<a href="c/">
Back to Portal
</a>
</center>
<%
String randomNo = lebah.db.UniqueID.getUID();
response.sendRedirect("c/?logoutrndId=" + randomNo); 
%>