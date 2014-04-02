<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:template>
	<jsp:attribute name="header"/>
	<jsp:body>
	<c:if test="${ not empty requestScope.error }">
	<div class="row"><div class="col-lg-12"><div class="alert alert-dismissable alert-warning">
	<button type="button" class="close" data-dismiss="alert">×</button>
	<p>发生错误了，请<a href="/pages/home.jsp" class="alert-link">返回</a>重试！</p>
	<p><c:out value="${ requestScope.error }" /></p>
	</div></div></div>
	</c:if>
    </jsp:body>
</t:template>