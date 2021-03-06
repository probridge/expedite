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
			$('#formName').attr('disabled','disabled').val('');
			$('#customRoleName').attr('disabled','disabled').val('');
			$('#dataLimit').attr('disabled','disabled').val('');
		}
		if ($('#roleTypeParticipant').prop('checked')) {
			$('#formName').removeAttr('disabled');
			$('#dataLimit').removeAttr('disabled');
			$('#customRoleName').attr('disabled','disabled').val('');
		}
		if ($('#roleTypeCustom').prop('checked')) {
			$('#formName').removeAttr('disabled');
			$('#dataLimit').removeAttr('disabled');
			$('#customRoleName').removeAttr('disabled');
		}
		//
		error = false;
		//
		if (!$('#appName').val().match(/^[a-z0-9]+$/i)) {
			$('#appName').parent().parent().addClass("has-error");
			error = true;
		} else {
			$('#appName').parent().parent().removeClass("has-error");
		}
		//
		if (!$('#formName').attr('disabled')) {
			if (!$('#formName').val().match(/^[a-z0-9]+$/i)) {
				$('#formName').parent().parent().addClass("has-error");
				error = true;
			} else {
				$('#formName').parent().parent().removeClass("has-error");
			}
		} else {
			$('#formName').parent().parent().removeClass("has-error");
		}
		//
		if (!$('#customRoleName').attr('disabled')) {
			if (!$('#customRoleName').val().match(/^[a-z0-9]+$/i)) {
				$('#customRoleName').parent().parent().addClass("has-error");
				error = true;
			} else {
				$('#customRoleName').parent().parent().removeClass("has-error");
			}
		} else {
			$('#customRoleName').parent().parent().removeClass("has-error");
		}
		//
		if (!error) {
			$('input[type="submit"]').removeAttr('disabled');
			//
			if ($('#roleTypeEditor').prop('checked')){
				if ($('#appName').val()) {
					$('#roleName').val($('#appName').val() + $('input[type="radio"][name="roleType"]:checked').val());			
				} else {
					$('#roleName').val('');
				}
			}
			if ($('#roleTypeParticipant').prop('checked')) {
				if ( $('#appName').val()) 
					if ($('#formName').val()) {
						$('#roleName').val($('#appName').val() + '-' + $('#formName').val() + $('input[type="radio"][name="roleType"]:checked').val());
						return;
					}
				$('#roleName').val('');
			}
			if ($('#roleTypeCustom').prop('checked')) {
				if ( $('#appName').val()) 
					if ($('#formName').val())
					 	if ($('#roleTypeCustom').val()) {
							$('#roleName').val($('#appName').val() + '-' + $('#formName').val() + '-' + $('#customRoleName').val());
							return;
						}
				$('#roleName').val('');
			}
		} else {
			$('input[type="submit"]').attr('disabled','disabled');
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
            <th>过期</th>
            <th>描述</th>
            <th>最大文档数</th>
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
        <c:if test="${ empty role.roleExpiration }">
            <td>-</td>        
		</c:if>
        <c:if test="${ not empty role.roleExpiration }">
            <td><fmt:formatDate value="${role.roleExpiration}" pattern="yyyy-MM-dd"/></td>
		</c:if>
            <td><c:out value="${role.description}" /></td>
            <td><c:out value="${(empty role.formName)?'-':((empty role.dataLimit)?'∞':role.dataLimit)}" /></td>
        <c:if test="${ role.appName eq sandboxApp }">
            <td>-</td>
            <td>-</td>
            <td><a href="<c:out value="${'assign?listRole='}${role.roleName}"/>">成员</a></td>
        </c:if>
        <c:if test="${ role.appName ne sandboxApp }">
            <td><a href="<c:out value="${'roles?action=edit&roleName='}${role.roleName}"/>">修改</a></td>
            <td><a href="<c:out value="${'roles?action=delete&roleName='}${role.roleName}"/>" onclick="return confirm('确认删除吗？');">删除</a></td>
            <td><a href="<c:out value="${'assign?listRole='}${role.roleName}"/>">成员</a></td>
        </c:if>
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
        <div class="radio">
          <label>
			<input type="radio" id="roleTypeCustom" name="roleType" onchange="updateVal();" value="others" ${(!fn:endsWith(role.roleName , editorSuffix) and !fn:endsWith(role.roleName , participantSuffix)) ?'checked="checked"':''}/>
            	自定义角色
          </label>
        </div>
      </div>
    </div>
    <div class="form-group">
      <label for="appName" class="col-lg-2 control-label">数据库名</label>
      <div class="col-lg-6">
<c:if test="${ isAdmin }">
        <input type="text" class="form-control" id="appName"  name="appName"  value="<c:out value="${role.appName}"/>" onchange="updateVal();" placeholder="数据库名" required="required"/>
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
      <label for="formName" class="col-lg-2 control-label">表单名</label>
      <div class="col-lg-6">
        <input type="text" class="form-control" id="formName" name="formName" value="<c:out value="${role.formName}"/>" onchange="updateVal();" placeholder="表单名"/>
        <span class="help-block">首页显示请输入homepage</span>
      </div>
    </div>
    <div class="form-group">
      <label for="customRoleName" class="col-lg-2 control-label">角色名</label>
      <div class="col-lg-6">
        <input type="text" class="form-control" id="customRoleName" name="customRoleName" value="<c:out value="${fn:split(role.roleName,'-')[2]}"/>" onchange="updateVal();" placeholder="角色名"/>
        <span class="help-block">首页显示角色为visible</span>
      </div>
    </div>
    <div class="form-group">
      <label for="expiration" class="col-lg-2 control-label">角色有效期</label>
      <div class="col-lg-6">
        <input type="text" class="form-control" id="expiration" name="expiration" value="<fmt:formatDate value="${role.roleExpiration}" pattern="yyyy-MM-dd"/>" onchange="updateVal();" placeholder="角色的过期日期，格式为YYYY-MM-DD，空为不设过期"/>
      </div>
    </div>
    <div class="form-group">
      <label for="description" class="col-lg-2 control-label">描述</label>
      <div class="col-lg-6">
        <input type="text" class="form-control" id="description" name="description" value="<c:out value="${role.description}"/>" onchange="updateVal();" placeholder="角色的描述信息"/>
      </div>
    </div>
    <div class="form-group">
      <label for="dataLimit" class="col-lg-2 control-label">允许提交次数</label>
      <div class="col-lg-6">
        <input type="text" class="form-control" id="dataLimit" name="dataLimit" value="<c:out value="${role.dataLimit}"/>" onchange="updateVal();" placeholder="每用户提交次数"/>
        <span class="help-block">请输入正整数，留空为无限，不定义本角色则默认每用户每表单允许提交一份数据</span>
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