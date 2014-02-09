<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="exp" uri="http://www.probridge.com.cn/expedite-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<xhtml:html xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events">
<xhtml:head>
<xhtml:meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<xhtml:title>Welcome</xhtml:title>
</xhtml:head>
<xhtml:body>
<xhtml:h1>欢迎您</xhtml:h1>
<xhtml:p><xhtml:a href="../forms/">Access Form</xhtml:a></xhtml:p>
<exp:isAuthenticated>
<xhtml:p><xhtml:a href="logout.jsp">Logout</xhtml:a></xhtml:p>
</exp:isAuthenticated>
<exp:isNotAuthenticated>
<xhtml:p><xhtml:a href="login.jsp">Login</xhtml:a></xhtml:p>
<xhtml:p><xhtml:a href="../jaccount/">jAccount Login</xhtml:a></xhtml:p>
</exp:isNotAuthenticated>
<exp:genImage relPath="img/Chrysanthemum.jpg"/>
</xhtml:body>
</xhtml:html>