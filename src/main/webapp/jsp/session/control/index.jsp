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
<input type="submit" name="filesystemlucene" value="filesystemlucene"/>
</form>

<br/>

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

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="submit" name="cleanup2" value="cleanup2"/>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="submit" name="cleanupfs" value="cleanupfs"/>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="submit" name="memoryusage" value="memoryusage"/>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="submit" name="notindexed" value="notindexed"/>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="filesystemluceneadd"/>
<input type="submit" name="filesystemluceneadd2" value="filesystemluceneadd"/>
<textarea rows="1" cols="40" name="filesystemluceneadd"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="filesystemadd"/>
<input type="submit" name="filesystemadd2" value="filesystemadd"/>
<textarea rows="1" cols="40" name="filesystemadd"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="luceneadd"/>
<input type="submit" name="luceneadd2" value="luceneadd"/>
<textarea rows="1" cols="40" name="luceneadd"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="lucenereindex"/>
<input type="submit" name="luceneadd2" value="lucenereindex"/>
<textarea rows="1" cols="40" name="lucenereindex"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="cleanupfs"/>
<input type="submit" name="cleanupfs2" value="cleanupfs"/>
<textarea rows="1" cols="40" name="cleanupfs"></textarea>
</form>

<br/>

<form action="display.jsp" style="display: inline; margin: 0;">
<input type="hidden" name="type" value="suffixindex"/>
<input type="submit" name="suffixindex2" value="suffixindex"/>
<textarea rows="1" cols="40" name="suffixindex"></textarea>
</form>

</body>
</html>
