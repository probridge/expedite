<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<t:template>
	<jsp:attribute name="header">
		<link rel="stylesheet" type="text/css" media="screen"
			href="/ops/exp_res/css/theme-style.css" />	
    </jsp:attribute>
	<jsp:attribute name="jscode">
	</jsp:attribute>
	<jsp:body>
    <div class="well">
	<form method="POST" action='users' name="frmUser" class="form-horizontal">
	<fieldset>
    <legend>用户管理</legend>
    <div class="form-group">
      <label for="userName" class="col-lg-2 control-label">用户名</label>
      <div class="col-lg-6">
        <input type="text" class="form-control" ${empty user.userName?'':'readonly="readonly"'} id="userName" name="userName" value="<c:out value="${user.userName}"/>" placeholder="用户名"/>
      </div>
    </div>
    <div class="form-group">
      <label for="userPassword" class="col-lg-2 control-label">密码</label>
      <div class="col-lg-6">
        <input type="password" class="form-control" ${user.userType==0?'':'readonly="readonly"'} id="userPassword" name="userPassword" value="<c:out value="${user.userPassword}"/>"/>
      </div>
    </div>
    <div class="form-group">
      <label for="userDescription" class="col-lg-2 control-label">描述</label>
      <div class="col-lg-6">
        <input type="text" class="form-control" id="userDescription" name="userDescription" value="<c:out value="${user.userDescription}"/>" placeholder="用户描述"/>
      </div>
    </div>
    <div class="form-group">
      <label for="userGroup" class="col-lg-2 control-label">用户组</label>
      <div class="col-lg-6">
        <select class="form-control" id="userGroup" name="userGroup">
			<option value="${GroupUser}" ${user.userGroup eq GroupUser?'selected="selected"':''}>普通用户</option>
			<option value="${GroupEditor}" ${user.userGroup eq GroupEditor?'selected="selected"':''}>问卷编辑</option>
			<option value="${GroupAdmin}" ${user.userGroup eq GroupAdmin?'selected="selected"':''}>系统管理员</option>
        </select>
      </div>
    </div>
    <div class="form-group">
      <label class="col-lg-2 control-label">类型</label>
      <div class="col-lg-6">
        <div class="radio">
          <label>
			<input type="radio" name="userType" value="0" ${user.userType==0?'checked="checked"':'disabled="disabled"'}/>
			本地用户
          </label>
        </div>
        <div class="radio">
          <label>
          	<input type="radio" name="userType" value="1" ${user.userType==1?'checked="checked"':'disabled="disabled"'}/>
          	统一认证
          </label>
        </div>
      </div>
    </div>
    <div class="form-group">
      <label class="col-lg-2 control-label">状态</label>
      <div class="col-lg-6">
        <div class="radio">
          <label>
          	<input type="radio" name="userEnabled" value="1" ${user.userEnabled==1?'checked="checked"':''}/>
          	活动
          </label>
        </div>
        <div class="radio">
          <label>
			<input type="radio" name="userEnabled" value="0" ${user.userEnabled==0?'checked="checked"':''}/>
			禁用
		  </label>
        </div>
      </div>
    </div>
    <div class="form-group">
      <label for="userExpiration" class="col-lg-2 control-label">过期日</label>
      <div class="col-lg-6">
        <input type="text" class="form-control" id="userExpiration" name="userExpiration" value="<fmt:formatDate pattern="yyyy-MM-dd" value="${user.userExpiration}" />" placeholder="用户帐号过期日 (格式: 2014-9-28)"/>
      </div>
    </div>
    <div class="form-group">
      <label for="userPwdExpiration" class="col-lg-2 control-label">密码失效</label>
      <div class="col-lg-6">
        <input type="text" class="form-control" id="userPwdExpiration" name="userPwdExpiration" value="<fmt:formatDate pattern="yyyy-MM-dd" value="${user.userPwdExpiration}" />" placeholder="帐号密码过期日 (格式: 2014-9-28)"/>
      </div>
    </div>
    <div class="form-group">
      <div class="col-lg-6 col-lg-offset-2">
		<a class="btn btn-default" href="/users">返回</a>
		<input type="submit" value="保存" class="btn btn-primary" />
	  </div>
	</div>
	</fieldset>
	</form>
	</div>
    </jsp:body>
</t:template>