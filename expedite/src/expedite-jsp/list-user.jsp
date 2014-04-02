<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<t:template>
	<jsp:attribute name="header">
		<link rel="stylesheet" type="text/css" media="screen"
			href="/ops/exp_res/css/theme-style.css" />	
    </jsp:attribute>
	<jsp:attribute name="jscode"></jsp:attribute>
	<jsp:body>
	<h2>用户管理</h2>
	<table class="table table-striped table-hover ">
        <thead>
          <tr>
            <th>用户名</th>
            <th>描述</th>
            <th>用户组</th>
            <th>类型</th>
            <th>状态</th>
            <th>过期日</th>
            <th>密码失效</th>
            <th>#</th>
            <th>#</th>
            <th>#</th>
          </tr>
        </thead>
        <tbody>
<c:forEach items="${users}" var="user">
          <tr>
            <td><c:out value="${user.userName}" /></td>
            <td><c:out value="${user.userDescription}" /></td>
            <td><c:out value="${user.userGroup}" /></td>
            <td><c:out value="${user.userType==1?'统一认证':'本地用户'}" /></td>
            <td><c:out value="${user.userEnabled==1?'活动':'禁用'}" /></td>
            <td><fmt:formatDate pattern="yyyy-MM-dd" value="${user.userExpiration}" /></td>
            <td><fmt:formatDate pattern="yyyy-MM-dd" value="${user.userPwdExpiration}" /></td>
            <td><a href="<c:out value="${'users?action=delete&userName='}${user.userName}"/>">删除</a></td>
            <td><a href="<c:out value="${'users?action=edit&userName='}${user.userName}"/>">修改</a></td>
            <td><a href="<c:out value="${'assign?listUser='}${user.userName}"/>">角色</a></td>
          </tr>
</c:forEach>
        </tbody>
      </table>
      <a class="btn btn-default" href="/pages/home.jsp">返回</a>
      <a class="btn btn-primary" href="users?action=new">新增用户</a>
    </jsp:body>
</t:template>