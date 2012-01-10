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

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="submit" name="filesystem" value="filesystem"/>
</form>

<br/>
<form action="display.jsp" style="display: inline; margin: 0;">
<input type="submit" name="lucene" value="lucene"/>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="submit" name="cleanup" value="cleanup"/>
</form>

<br/>

</body>
</html>
