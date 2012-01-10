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

<jsp:useBean id="main3" scope="session" class="roart.beans.session.comic.Main" />

<jsp:setProperty name="main3" property="*" />
<!--%
	main.processRequest(request);
%-->


<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="comic"/>
<input type="submit">
<select size="5" name="title">
<% 
	String[] items = main3.getTitles("comic");
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

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="comic"/>
<input type="submit">
<select size="5" name="letter">
<% 
	String[] items2 = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
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

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="comic"/>
<input type="hidden" name="all" value="all"/>
<input type="submit">
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="comic"/>
<input type="submit">
<textarea rows="4" cols="40" name="search"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="com"/>
<input type="submit">
<select size="5" name="year">
<% 
	String[] items3 = main3.getYears("com");
	for (int i=0; i<items3.length; i++) {
%>
<option>
<%=
items3[i]
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
