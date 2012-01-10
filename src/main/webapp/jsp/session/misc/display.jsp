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

<jsp:useBean id="main" scope="session" class="roart.beans.session.misc.Main" />

<jsp:setProperty name="main" property="*" />
<jsp:directive.page import="java.util.List"/>
<jsp:directive.page import="java.util.ArrayList"/>
<jsp:directive.page import="roart.beans.session.misc.Unit"/>
<!--%
	main.processRequest(request);
%-->

<%
	//HttpSession session = request.getSession();
	String type = request.getParameter("type");
	String searchme = request.getParameter("searchme");
	System.out.println("bla " + searchme);
	if (searchme == null) {
	String creator = request.getParameter("creator");
	String year = request.getParameter("year");
	List<unit> myunits = null;
	if (year != null) {
		myunits = main.searchyear(type, year);
	}
	if (creator != null) {
		myunits = main.searchcreator(type, creator);
	}
	Integer count = new Integer (0);
	Float price = new Float (0);
	for (int i=0; i<myunits.size(); i++) {
		count += new Integer(myunits.get(i).getCount());
		if (!myunits.get(i).getPrice().substring(0,1).equals("D") && !myunits.get(i).getPrice().substring(0,1).equals("L") && !myunits.get(i).getPrice().substring(0,1).equals("g") ) {
			price += new Float(myunits.get(i).getPrice());
		}
%>
<%= myunits.get(i).getDate() %>
<%= myunits.get(i).getCount() %>
<%= myunits.get(i).getType() %>
<%= myunits.get(i).getPrice() %>
<% 
   String str = myunits.get(i).getIsbn();
   if (str != null && !str.equals("0")) {
%>
<a href="http://www.lookupbyisbn.com/Search/Book/<%= myunits.get(i).getIsbn() %>/1">US <%= myunits.get(i).getIsbn() %></a>
<a href="http://www.ark.no/SamboWeb/sok.do?isbn=<%= myunits.get(i).getIsbn() %>">NO <%= myunits.get(i).getIsbn() %></a>
<a href="http://libris.kb.se/hitlist?d=libris&q=numm%3a<%= myunits.get(i).getIsbn() %>">SE <%= myunits.get(i).getIsbn() %></a>
<a href="https://www.google.com/search?q=isbn%2b%2b<%= myunits.get(i).getIsbn() %>">G <%= myunits.get(i).getIsbn() %></a>
<%
	  }
%>
<%= myunits.get(i).getCreator() %>
<%= myunits.get(i).getTitle() %>
<br/>
<%
	}
%>
<br>
<%= myunits.size() %>
<%= count %>
<%= price %>
<%
	} else {
	  List<String> strarr = main.searchme(type, searchme);
	  for (int i=0; i<strarr.size(); i++) {
	    String str = strarr.get(i);
%>
<%= str %>
<br/>
<%
          }
	  List<String> strarr2 = main.searchme(searchme);
	  for (int i=0; i<strarr2.size(); i++) {
	    String str = strarr2.get(i);
%>
<%= str %>
<br/>
<%
          }
	}
%>

</body>
</html>
