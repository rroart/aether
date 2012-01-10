<%--
 Copyright 2004-2005 Sun Microsystems, Inc.  All rights reserved.
 Use is subject to license terms.
--%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<fmt:setBundle basename="LocalStrings"/>

<html>
<body>
<!--
  Copyright (c) 1999 The Apache Software Foundation.  All rights 
  reserved.
-->

<jsp:useBean id="main4" scope="session" class="roart.beans.session.training.Main" />

<jsp:setProperty name="main4" property="*" />
<!--%
	main.processRequest(request);
%-->


<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="comic"/>
<input type="submit">
<select size="5" name="title">
<% 
	String[] items = new String[0];//main4.getTitles("tren");
	for (int i=0; i<items.length; i++) {
%>
<option value=
<%=
i
%>
>
<%=
items[i]
%>
</option>
<%
	}
%>
</select>
</form> 

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="tren"/>
<input type="submit">
<select size="5" name="year">
<% 
	String[] items2 = main4.getYears("tren");
	for (int i=0; i<items2.length; i++) {
%>
<option>
<%=
items2[i]
%>
</option>
<%
	}
%>
</select>
</form> 

<br/>

</body>
</html>
