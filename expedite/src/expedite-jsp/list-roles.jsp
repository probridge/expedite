<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="exp" uri="http://www.probridge.com.cn/expedite-tags"%>
<t:template>
	<jsp:attribute name="header">
		<link rel="stylesheet" type="text/css" media="screen"
			href="/ops/exp_res/css/theme-style.css" />	
    </jsp:attribute>
	<jsp:attribute name="jscode">
	<script language="JavaScript">
	function updateVal() {
		if ($('#roleTypeEditor').prop('checked')){
			if ($('#appName').val()) {
				$('#formName').attr('disabled','disabled').val('');			
				$('#roleName').val($('#appName').val() + $('input[type="radio"][name="roleType"]:checked').val());			
			} else {
				$('#roleName').val('');
			}
		}
		if ($('#roleTypeParticipant').prop('checked')) {
			$('#formName').removeAttr('disabled');			
			if ( $('#appName').val()) 
				if ($('#formName').val()) {
					$('#roleName').val($('#appName').val() + '-' + $('#formName').val() + $('input[type="radio"][name="roleType"]:checked').val());
					return;
				}
			$('#roleName').val('');
		}
	}
	</script>
<c:if test="${ isEditor }">
	<script language="JavaScript">
	$('document').ready(function() {
		$('#roleTypeParticipant').attr("checked","checked");
		$('#roleTypeEditor').attr("disabled","disabled");
	});
	</script>
</c:if>
<c:if test='${! empty role}'>
	<script language="JavaScript">
	$('document').ready(function() {
		updateVal();
	});
	</script>
</c:if>
	</jsp:attribute>
	<jsp:body>
	<h2>角色清单</h2>
	<table class="table table-striped table-hover">
        <thead>
          <tr>
            <th>数据库</th>
            <th>表单</th>
            <th>角色名</th>
            <th>描述</th>
            <th>#</th>
            <th>#</th>
            <th>#</th>
          </tr>
        </thead>
        <tbody>
<c:forEach items="${roles}" var="role">
          <tr class="<c:out value="${(empty role.formName)?'info':''}"/>">
            <td><c:out value="${(empty role.formName)? role.appName : '-' }" /></td>
            <td><c:out value="${(empty role.formName)?'*':role.formName}" /></td>
            <td><c:out value="${role.roleName}" /></td>
            <td><c:out value="${role.description}" /></td>
            <td><a href="<c:out value="${'roles?action=edit&roleName='}${role.roleName}"/>">修改</a></td>
            <td><a href="<c:out value="${'roles?action=delete&roleName='}${role.roleName}"/>">删除</a></td>
            <td><a href="<c:out value="${'assign?listRole='}${role.roleName}"/>">成员</a></td>
          </tr>
</c:forEach>
        </tbody>
      </table>
    <hr/>
    <div class="well">
	<form method="POST" action='roles' name="frmRole" class="form-horizontal">
	<fieldset>
    <legend>角色</legend>
    <div class="form-group">
      <label for="appName" class="col-lg-2 control-label">数据库</label>
      <div class="col-lg-6">
<c:if test="${ isAdmin }">
        <input type="text" class="form-control" id="appName"  name="appName"  value="<c:out value="${role.appName}"/>" onchange="updateVal();" placeholder="数据库名"/>
</c:if>
<c:if test="${ isEditor }">
        <select class="form-control" id="appName" name="appName" onchange="updateVal();">
        <exp:roleIterator mode="editor">
          <option value="${pageScope.tagAppName}">${pageScope.tagAppName}</option>
        </exp:roleIterator>
        </select>
</c:if>
      </div>
    </div>
    <div class="form-group">
      <label class="col-lg-2 control-label">角色类型</label>
      <div class="col-lg-6">
        <div class="radio">
          <label>
            <input type="radio" id="roleTypeEditor" name="roleType" value="${ editorSuffix }" onchange="updateVal();" ${fn:endsWith(role.roleName , editorSuffix)?'checked="checked"':''}/>
            	数据库管理员角色
          </label>
        </div>
        <div class="radio">
          <label>
			<input type="radio" id="roleTypeParticipant" name="roleType" value="${ participantSuffix }" onchange="updateVal();" ${fn:endsWith(role.roleName , participantSuffix)?'checked="checked"':''}/>
            	表单样本角色
          </label>
        </div>
      </div>
    </div>
    <div class="form-group">
      <label for="formName" class="col-lg-2 control-label">表单</label>
      <div class="col-lg-6">
        <input type="text" class="form-control" id="formName" name="formName" value="<c:out value="${role.formName}"/>" onchange="updateVal();" placeholder="表单名"/>
      </div>
    </div>
    <div class="form-group">
      <label for="appName" class="col-lg-2 control-label">描述</label>
      <div class="col-lg-6">
        <input type="text" class="form-control" id="description" name="description" value="<c:out value="${role.description}"/>" onchange="updateVal();" placeholder="角色的描述信息"/>
      </div>
    </div>
    <div class="form-group">
      <label for="appName" class="col-lg-2 control-label">角色名称</label>
      <div class="col-lg-6">
    	<input type="text" class="form-control" id="roleName" name="roleName" value="<c:out value="${role.roleName}"/>" readonly="readonly"/>
      </div>
    </div>
    <div class="form-group">
      <div class="col-lg-6 col-lg-offset-2">
		<a class="btn btn-default" href="/pages/home.jsp">返回</a>
		<input type="submit" value="保存" onclick="updateVal();" class="btn btn-primary" />
	  </div>
	</div>
	</fieldset>
	</form>
	</div>
    </jsp:body>
</t:template>