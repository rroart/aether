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
<jsp:directive.page import="java.util.ArrayList"/>
<jsp:directive.page import="java.util.List"/>
<jsp:directive.page import="java.util.TreeMap"/>
<jsp:directive.page import="roart.beans.session.training.Unit"/>
<!--%
	main.processRequest(request);
%-->

<%
	//HttpSession session = request.getSession();
	String type = request.getParameter("type");
	String year = request.getParameter("year");
	if (year == null) {
	String title = request.getParameter("title");
	String all = request.getParameter("all");
	String letter = request.getParameter("letter");
	String search = request.getParameter("search");
	List<unit> myunits = null;
        int count = 0;
	int sum = 0;
/*
	if (title != null) {
		myunits = main4.searchtitle(type, (new Integer(title)).intValue());
	}
	if (all != null) {
		myunits = main4.searchtitle(type);
	}
	if (letter != null) {
		myunits = main4.searchtitle(type, letter);
	}
*/
	for (int i=0; i<myunits.size(); i++) {
                    unit myunit = myunits.get(i);
/*
		    String strcount = myunit.getCount();
		    int cnt = ((new Integer(strcount)).intValue());
		    int prc = ((new Integer(myunit.getPrice())).intValue());
		    count += cnt;
	   	    sum += cnt * prc;
*/
%>
<%= myunits.get(i).getData() %>
<br />
<%
	  List<String> lines = null; //myunits.get(i).getContent();
	  for (int j=0; j<lines.size(); j++) {
%>
<%= lines.get(j) %>
<br/>
<%
	  }
%>
<br/>
<br/>
<%
	}
%>

<br/>

<%
	} else {
        int count = 0;
	int sum = 0;
	        TreeMap<String, Integer> mysums = new TreeMap<String, Integer>();
		List<unit> myunits = main4.searchyear(mysums, type, year);

	for (int i=0; i<myunits.size(); i++) {
                    unit myunit = myunits.get(i);
		    //String strcount = myunit.getCount();
%>
<%= myunits.get(i).getDate() %>
<%= myunits.get(i).getData() %>
<br/>
<%
	}
%>
<br/>
<br/>
<%
	for (String key : mysums.keySet()) {
	  Integer i = mysums.get(key);
	  count += i.intValue();
%>
<%= key %>
<%= i.intValue() %>
<br/>
<%
	  }
%>
<br/>
<%= myunits.size() %>
<%= count %>
<%= sum %>

<%
        }
%>
<br/>

</body>
</html>
