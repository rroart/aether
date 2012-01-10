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

<jsp:useBean id="main2" scope="session" class="roart.beans.session.food.Main" />

<jsp:setProperty name="main2" property="*" />
<!--%
	main.processRequest(request);
%-->


<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="oppskrift"/>
<input type="submit">
<select size="5" name="title">
<% 
	String[] items = main2.getTitles("oppskrift");
	for (int i=0; i<items.length; i++) {
%>
<option>
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
<input type="hidden" name="type" value="oppskrift"/>
<input type="submit">
<textarea rows="4" cols="40" name="search"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="vocalbulary"/>
<input type="submit" value="vocalbulary">
</form>

<br/>

</body>
</html>
