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

<jsp:useBean id="main5" scope="session" class="roart.beans.session.control.Main" />

<jsp:setProperty name="main5" property="*" />
<jsp:directive.page import="java.util.List"/>
<jsp:directive.page import="java.util.ArrayList"/>
<jsp:directive.page import="java.util.TreeMap"/>
<!--%
	main.processRequest(request);
%-->

<%
	//HttpSession session = request.getSession();
	String lucene = request.getParameter("lucene");
	String filesystem = request.getParameter("filesystem");
	String luceneadd = request.getParameter("luceneadd");
	String filesystemadd = request.getParameter("filesystemadd");
	String cleanup = request.getParameter("cleanup");
	System.out.println("test " + lucene + " " + filesystem);
	List<String> strarr = null;
	if (filesystem != null) {
 	   strarr = main5.traverse();
	}
	if (lucene != null) {
 	   strarr = main5.index();
	}
	if (filesystemadd != null) {
 	   strarr = main5.traverse(filesystemadd);
	}
	if (luceneadd != null) {
 	   strarr = main5.index(luceneadd);
	}
	if (cleanup != null) {
 	   strarr = main5.cleanup();
	}
  	   for (int i=0; i<strarr.size(); i++) {
	     String str = strarr.get(i);
%>
<%= str %>
<br/>
<%
          }
%>
<h1>finished, or something</h1>
<br/>

</body>
</html>
