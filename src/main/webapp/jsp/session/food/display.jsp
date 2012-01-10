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
<jsp:directive.page import="java.util.ArrayList"/>
<jsp:directive.page import="java.util.List"/>
<!--jsp:directive.page import="roart.beans.sessions.unit"/-->
<!--%
	main.processRequest(request);
%-->

<%
	//HttpSession session = request.getSession();
	String type = request.getParameter("type");
	String title = request.getParameter("title");
	String search = request.getParameter("search");
	String vocalbulary = request.getParameter("vocalbulary");
	List<String[]> myunits = null;
	if (title != null) {
		myunits = main2.searchtitle(type, title);
	}
	if (search != null) {
		myunits = main2.searchstuff(type, search);
	}
	if (vocalbulary != null) {
		myunits = main2.getvocalbulary();
	}
	for (int i=0; i<myunits.size(); i++) {
	  for (int j=0; j<myunits.get(i).length; j++) {
%>
<%= myunits.get(i)[j] %>
<%
	  }
%>
<br/>
<br/>
<%
	}
%>
<br/>
<%= myunits.size() %>

</body>
</html>
