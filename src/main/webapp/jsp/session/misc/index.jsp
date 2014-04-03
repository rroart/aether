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
<!--%
	main.processRequest(request);
%-->


<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="cd"/>
<input type="submit">
<select size="5" name="creator">
<% 
        roart.search.SearchLucene.indexme("cd");
	String[] items = main.getCreators("cd");
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

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="cd"/>
<input type="submit">
<select size="5" name="year">
<% 
	String[] items2 = main.getYears("cd");
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
<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="cd"/>
<input type="submit">
<textarea rows="4" cols="40" name="searchme"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="dvd"/>
<input type="submit">
<select size="5" name="creator">
<% 
        roart.search.SearchLucene.indexme("dvd");
	String[] items3 = main.getCreators("dvd");
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

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="dvd"/>
<input type="submit">
<select size="5" name="year">
<% 
	String[] items4 = main.getYears("dvd");
	for (int i=0; i<items4.length; i++) {
%>
<option>
<%=
items4[i]
%>
</option>
<%
	}
%>
</select>
</form> 
<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="dvd"/>
<input type="submit">
<textarea rows="4" cols="40" name="searchme"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="book"/>
<input type="submit">
<select size="5" name="creator">
<% 
        roart.search.SearchLucene.indexme("book");
	String[] items5 = main.getCreators("book");
	for (int i=0; i<items5.length; i++) {
%>
<option>
<%=
items5[i]
%>
</option>
<%
	}
%>
</select>
</form> 

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="book"/>
<input type="submit">
<select size="5" name="year">
<% 
	String[] items6 = main.getYears("book");
	for (int i=0; i<items6.length; i++) {
%>
<option>
<%=
items6[i]
%>
</option>
<%
	}
%>
</select>
</form> 
<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="book"/>
<input type="submit">
<textarea rows="4" cols="40" name="searchme"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="booku"/>
<input type="submit">
<select size="5" name="creator">
<% 
        roart.search.SearchLucene.indexme("booku");
	String[] items7 = main.getCreators("booku");
	for (int i=0; i<items7.length; i++) {
%>
<option>
<%=
items7[i]
%>
</option>
<%
	}
%>
</select>
</form> 

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="booku"/>
<input type="submit">
<select size="5" name="year">
<% 
	String[] items8 = main.getYears("booku");
	for (int i=0; i<items8.length; i++) {
%>
<option>
<%=
items8[i]
%>
</option>
<%
	}
%>
</select>
</form> 
<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="booku"/>
<input type="submit">
<textarea rows="4" cols="40" name="searchme"></textarea>
</form>

<br/>

<%	
	if (!main.isSecret()) {
%>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="book0"/>
<input type="submit">
<select size="5" name="creator">
<% 
        roart.search.SearchLucene.indexme("book0");
	String[] items9 = main.getCreators("book0");
	for (int i=0; i<items9.length; i++) {
%>
<option>
<%=
items9[i]
%>
</option>
<%
	}
%>
</select>
</form> 

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="book0"/>
<input type="submit">
<select size="5" name="year">
<% 
	String[] items10 = main.getYears("book0");
	for (int i=0; i<items10.length; i++) {
%>
<option>
<%=
items10[i]
%>
</option>
<%
	}
%>
</select>
</form> 
<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="book0"/>
<input type="submit">
<textarea rows="4" cols="40" name="searchme"></textarea>
</form>

<br/>

<% 
        roart.search.SearchLucene.indexme("book0gen");
%>
<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="0"/>
<input type="submit" name="all" value="search simple">
<textarea rows="4" cols="40" name="searchme0"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="1"/>
<input type="submit" name="all" value="search simple">
<textarea rows="4" cols="40" name="searchme"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="2"/>
<input type="submit" name="all" value="search analyzing">
<textarea rows="4" cols="40" name="searchme"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="3"/>
<input type="submit" name="all" value="search complex">
<textarea rows="4" cols="40" name="searchme"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="4"/>
<input type="submit" name="all" value="search extendable">
<textarea rows="4" cols="40" name="searchme"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="lucene2"/>
<input type="submit" name="all" value="search multi">
<textarea rows="4" cols="40" name="searchsimilar"></textarea>
</form>

<%
	}
%>

</body>
</html>
