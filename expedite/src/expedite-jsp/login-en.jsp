<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="exp" uri="http://www.probridge.com.cn/expedite-tags"%>
<t:template_en>
	<jsp:attribute name="header">
    </jsp:attribute>
	<jsp:attribute name="jscode">
	<c:if test="${'1'==param.other}">
		<script type="text/javascript">	
		$(function () {
			$('#loginTab a[href="#other"]').tab('show'); 
		});
		</script>
	</c:if>
	<exp:hasToken>
	<script type="text/javascript">
		$('document').ready(function() {
			setTimeout(function() {
				$('#formLogin').submit();
		    }, 1000);
		});
	</script>
	</exp:hasToken>
	<script type="text/javascript">
		function startLogin() {
			$('#loginIcon').removeClass('icon-lock').addClass('icon-refresh icon-spin');
			$('#loginIcon').parent().addClass('disabled');
		}
	</script>
    </jsp:attribute>
	<jsp:body>
		<div class="row">
			<div class="col-lg-4 col-lg-offset-1 col-md-4 col-md-offset-1 col-sm-5 hidden-xs"> 
	            <img src="/ops/exp_res/imgs/login.png"/>
			</div>
			<div class="col-lg-4 col-lg-offset-2 col-md-5 col-md-offset-1 col-sm-5 col-sm-offset-1 col-xs-12"> 
<c:if test="${ not empty sessionScope.loginError }">
			<div class="alert alert-dismissable alert-warning">
			<button type="button" class="close" data-dismiss="alert">×</button>
			<p>${ sessionScope.loginError }, please <a href="#" class="alert-link">contact</a> us.</p>
			</div>
<c:set var="loginError" value="" scope="session"/>
</c:if>
<c:if test="${ not empty sessionScope.loginInfo }">
			<div class="alert alert-dismissable alert-info">
			<button type="button" class="close" data-dismiss="alert">×</button>
			<p>${ sessionScope.loginInfo }</p>
			</div>
<c:set var="loginInfo" value="" scope="session"/>
</c:if>
				<div class="row well" style="margin-top: 50px">
					<form class="bs-example form-horizontal" method="post" action="/login" id="formLogin">
						<fieldset>
							<exp:hasToken>
							<ul class="nav nav-tabs" id="loginTab">
								<li class="active"><a href="#jaccount" data-toggle="tab">jAccount User</a></li>
							</ul>
							<div class="tab-content">
								<div class="tab-pane active" id="jaccount" style="padding-top: 30px; padding-bottom: 20px; background-color: white; border: 1px #ddd; border-style: none solid solid">
									<div class="form-group">
										<div class="col-lg-8 col-lg-offset-3 col-md-8 col-md-offset-3 col-sm-8 col-sm-offset-3">
											<a class="btn btn-success btn-sm" href="#" style="width: 180px;"><i class="icon-refresh icon-spin icon-large spacer-right"></i>Logging in……</a> 
										</div>
									</div>							
								</div>
							</div>
							<input type="hidden" name="loginToken" value="true"/>
							</exp:hasToken>
							<exp:noToken>
							<ul class="nav nav-tabs" id="loginTab">
								<li class="active"><a href="#jaccount" data-toggle="tab">jAccount User</a></li>
								<li><a href="#other" data-toggle="tab">Other Users</a></li>
							</ul>
							<div class="tab-content">
								<div class="tab-pane active" id="jaccount" style="padding-top: 30px; padding-bottom: 20px; background-color: white; border: 1px #ddd; border-style: none solid solid">
									<div class="form-group">
										<div class="col-lg-8 col-lg-offset-3 col-md-8 col-md-offset-3 col-sm-8 col-sm-offset-3">
											<a class="btn btn-success btn-sm" href="/jaccount" style="width: 180px;"><i class="icon-lock icon-large spacer-right"></i>jAccount Login</a> 
										</div>
									</div>
								</div>
								<div class="tab-pane" id="other" style="padding-top: 30px; padding-bottom: 20px; background-color: white; border: 1px #ddd; border-style: none solid solid">
									<div class="form-group">
										<label for="inputEmail" class="col-lg-3 col-md-3 col-sm-3 control-label">Email</label>
										<div class="col-lg-8 col-md-8 col-sm-8">
										<div class="input-group" style="width: 100%">
											<input type="email" class="form-control" id="inputEmail" name="inputEmail"
												placeholder="Email" value="${requestScope.email}" maxlength="30"/>
								         </div>
										</div>
									</div>
									<div class="form-group">
										<label for="inputPassword" class="col-lg-3 col-md-3 col-sm-3 control-label">Password</label>
										<div class="col-lg-8 col-md-8 col-sm-8">
										<div class="input-group" style="width: 100%">
											<input type="password" class="form-control" id="inputPassword" name="inputPassword" maxlength="30"/>
								         </div>
										</div>
									</div>
									<div class="form-group"> 
										<div class="col-lg-4 col-lg-offset-3 col-md-4 col-md-offset-3 col-sm-4 col-sm-offset-3">
											<button type="submit" class="btn btn-primary btn-sm" style="width: 80px;" onClick="startLogin();"><i class="icon-lock icon-large spacer-right" id="loginIcon"></i>Login</button>
										</div>
									</div>
									<a href="register.jsp" class="pull-right">No account? Please register here.</a>
								</div>
							</div>							
							<input type="hidden" name="loginToken" value="false"/>							
							</exp:noToken>
						</fieldset>
						<input type="hidden" name="rememberMe" value="true"/>
					</form>
				</div>
			</div>
		</div>
    </jsp:body>
</t:template_en>
