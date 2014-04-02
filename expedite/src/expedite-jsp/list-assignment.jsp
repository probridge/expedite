<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<t:template>
	<jsp:attribute name="header">
		<link rel="stylesheet" type="text/css" media="screen"
			href="/ops/exp_res/css/theme-style.css" />	
    </jsp:attribute>
	<jsp:body>
	<h2><c:out value="${ listBy eq 'user' ? '用户拥有的角色' : '角色成员清单'}"/></h2>
	<h4><c:out value="${ listBy eq 'user' ? param.listUser : param.listRole }"/></h4>
	<table class="table table-striped table-hover">
        <thead>
          <tr>
            <th><c:out value="${ listBy eq 'user' ? '角色' : '用户'}"/></th>
            <th>
            <a href="assign?action=removeall&amp;roleName=${param.listRole}&amp;listUser=${param.listUser}&amp;listRole=${param.listRole}">
            <c:out value="${ listBy eq 'user' ? '' : '移除所有'}"/>
            </a>
            </th>
          </tr>
        </thead>
        <tbody>
<c:forEach items="${assignments}" var="assignment">
          <tr>
          <c:if test="${ listBy eq 'user' }">
          <td><a href="assign?listRole=${assignment.userRoles}"><c:out value="${assignment.userRoles}" /></a></td>
          </c:if>
          <c:if test="${ listBy eq 'role' }">
          <td><a href="assign?listUser=${assignment.userName}"><c:out value="${assignment.userName}" /></a></td>        
          </c:if>
          <td><a href="assign?action=remove&amp;roleName=${assignment.userRoles}&amp;userName=${assignment.userName}&amp;listUser=${param.listUser}&amp;listRole=${param.listRole}">移除</a></td>
          </tr>
</c:forEach>
        </tbody>
      </table>
    <div class="well">
    <form method="POST" action="assign?listUser=${param.listUser}&amp;listRole=${param.listRole}" class="form-horizontal">
	<fieldset>
    <legend>角色管理</legend>
   	<c:if test="${ listBy eq 'user'}">
    <div class="form-group">
      <label for="userName" class="col-lg-2 control-label">用户</label>
      <div class="col-lg-6">
		<input type="text" name="userName" value="${param.listUser}" readonly="readonly" class="form-control"/>
      </div>
    </div>
    <div class="form-group">
      <label for="roleName" class="col-lg-2 control-label">角色</label>
      <div class="col-lg-6">
		<select name="roleName" class="form-control" multiple="multiple">
		<c:forEach items="${roles}" var="role">
		<option value="${role.roleName}">${role.roleName} - ${role.description} </option>
		</c:forEach>
		</select>
      </div>
    </div>
	</c:if>	
   	<c:if test="${ listBy ne 'user'}">
    <div class="form-group">
      <label for="roleName" class="col-lg-2 control-label">角色</label>
      <div class="col-lg-6">
		<input type="text" name="roleName" class="form-control" readonly="readonly" value="${ param.listRole }" />
      </div>
    </div>
    <div class="form-group">
      <label for="userName" class="col-lg-2 control-label">用户</label>
      <div class="col-lg-6">
		<select name="userName" class="form-control" multiple="multiple">
			<c:forEach items="${users}" var="user">
			<option value="${user.userName}">${user.userName}</option>
			</c:forEach>
		</select>
      </div>
    </div>
	</c:if>
    <div class="form-group">
      <div class="col-lg-6 col-lg-offset-2">
		<a class="btn btn-default" onclick="window.history.back()">返回</a>
		<input type="submit" value="增加" class="btn btn-primary" />
	  </div>
	</div>
	</fieldset>
	</form>
	</div>
    </jsp:body>
</t:template>