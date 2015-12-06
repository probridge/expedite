<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="exp" uri="http://www.probridge.com.cn/expedite-tags"%>
<%@ attribute name="header" fragment="true" required="false"%>
<%@ attribute name="jscode" fragment="true" required="false"%>
<%@ attribute name="jquery" required="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xf="http://www.w3.org/2002/xforms"
	xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
	xmlns:xi="http://www.w3.org/2001/XInclude"
	xmlns:xxi="http://orbeon.org/oxf/xml/xinclude"
	xmlns:saxon="http://saxon.sf.net/"
	xmlns:fr="http://orbeon.org/oxf/xml/form-runner"
	xmlns:frf="java:org.orbeon.oxf.fr.FormRunner"
	xmlns:fbf="java:org.orbeon.oxf.fb.FormBuilder"
	xmlns:process="java:org.orbeon.oxf.fr.SimpleProcess"
	xmlns:ev="http://www.w3.org/2001/xml-events"
	xmlns:sql="http://orbeon.org/oxf/xml/sql">
<head>
<title>Antai Data Platform</title>
<!--[if IE]><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/><![endif]-->
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<link rel="icon" href="/ops/exp_res/imgs/favicon.ico" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<!-- Bootstrap -->
<link href="/ops/exp_res/css/bootstrap.min.css" rel="stylesheet"
	media="screen" />
<link rel="stylesheet" href="/ops/exp_res/css/font-awesome.min.css" />
<link rel="stylesheet" href="/ops/exp_res/css/bootswatch.min.css" />
<link href="/ops/exp_res/css/jquery.pnotify.default.css" media="all"
	rel="stylesheet" type="text/css" />
<!-- Customize -->
<link rel="stylesheet" href="/ops/exp_res/css/footer.css" />
<link rel="stylesheet" href="/ops/exp_res/css/chinese.css" />
<jsp:invoke fragment="header" />
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="/ops/exp_res/js/html5shiv.js"></script>
      <script src="/ops/exp_res/js/respond.min.js"></script>
<![endif]-->
<script src="/ops/exp_res/js/modernizr.custom.44054.js" />
</head>
<body>
	<div class="container" id="wrap">
		<div class="navbar navbar-default">
			<div class="container">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse"
						data-target=".navbar-responsive-collapse">
						<span class="icon-bar"></span> <span class="icon-bar"></span> <span
							class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="./"
						style="padding-top: 5px; padding-bottom: 0px"><img
						src="/ops/exp_res/imgs/expedite_icon.png"
						title="${ requestScope.version }" /></a>
				</div>
				<div class="navbar-collapse collapse navbar-responsive-collapse">
					<ul class="nav navbar-nav">
						<li><a href="/pages/home-en.jsp">Form Center</a></li>
						<li><a href="#">How to use</a></li>
						<li><a href="#">Contact Us</a></li>
						<li><a href="/pages/about.jsp">About</a></li>
					</ul>
					<exp:isAuthenticated>
						<ul class="nav navbar-nav navbar-right">
							<li class="dropdown"><a href="#" class="dropdown-toggle"
								data-toggle="dropdown"><exp:getPrincipal /><b class="caret"></b></a>
								<ul class="dropdown-menu">
								<li><a href="/logout"><i class="icon-unlock spacer-right"></i>&nbsp;Logout</a></li>
								</ul></li>
						</ul>
					</exp:isAuthenticated>
					<exp:isNotAuthenticated>
						<ul class="nav navbar-nav navbar-right">
							<li class="dropdown"><a href="#" class="dropdown-toggle"
								data-toggle="dropdown"> Login<b class="caret"></b></a>
								<ul class="dropdown-menu">
									<li><a href="/jaccount"><i class="icon-lock spacer-right"></i>jAccount Sign-on</a></li>
									<li><a href="/pages/login-en.jsp?other=1"><i
											class="icon-lock spacer-right"></i>Other Users</a></li>
								</ul></li>
						</ul>
					</exp:isNotAuthenticated>
				</div>
				<!-- /.nav-collapse -->
			</div>
			<!-- /.container -->
		</div>
		<!-- /.navbar -->
		<jsp:doBody />
	</div>
	<div id="divTimeout" style="display: none; margin-bottom: 0px;"
		class="alert alert-warning">
		<h4>您很久没有运动了</h4>
		请点击鼠标继续操作，为了您的安全我们将<label id="lbTimeRemain">在一段时间后</label>退出您的会话
	</div>
	<!-- /container -->
	<div class="container">
		<div id="footer" class="row">
			<div id="footer-content">Experimental Center, Antai College of Economics & Management, Shanghai Jiao Tong University</div>
		</div>
	</div>
	<!-- /container -->
	<c:if test="${empty jquery}">
	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="/ops/exp_res/js/jquery.js"></script>
	</c:if>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="/ops/exp_res/js/bootstrap.min.js"></script>
	<script src="/ops/exp_res/js/jquery.blockUI.js"></script>
	<script src="/ops/exp_res/js/holder.js"></script>
	<script type="text/javascript"
		src="/ops/exp_res/js/jquery.pnotify.min.js"></script>
	<script type="text/javascript">
		//
		$.pnotify.defaults.history = false;
		//
		if (!Modernizr.canvas)
			complain_browser();
		//
		var stack_bar_bottom = {
			"dir1" : "up",
			"dir2" : "right",
			"spacing1" : 0,
			"spacing2" : 0
		};
		function complain_browser() {
			var opts = {
				title : "<b>您的浏览器太久没有更新了</b>",
				text : "要正常使用vBox, 我们强烈建议您将浏览器升级到更加快速、安全的新版本，如Internet Explorer 9或者Chrome 29以上版本！",
				type : "error",
				hide : false,
				addclass : "stack-bar-bottom alert-danger",
				cornerclass : "",
				width : "100%",
				stack : stack_bar_bottom
			};
			$.pnotify(opts);
		}
	</script>
	<jsp:invoke fragment="jscode" />
</body>
</html>
