<%--
 Copyright 2004-2005 Sun Microsystems, Inc.  All rights reserved.
 Use is subject to license terms.
--%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<fmt:setBundle basename="LocalStrings"/>

<html>
<!--
  Copyright (c) 1999 The Apache Software Foundation.  All rights 
  reserved.
-->

<body bgcolor="white">

<font color="red">

<%@ page buffer="5kb" autoFlush="false" %>

<p><fmt:message key="include.jsp.inplace"/>

<%@ include file="foo.jsp" %>

<p> <jsp:include page="/include/foo.html" flush="true"/> <fmt:message key="include.jsp.including"/>

<jsp:include page="foo.jsp" flush="true"/>

:-) 

</html>
