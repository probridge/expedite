<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:template_en>
	<jsp:attribute name="header">
<link rel="stylesheet" type="text/css" media="screen"
			href="/ops/exp_res/css/theme-style.css"/>
<style type="text/css">
.jumbotron {
	background-image: url("../ops/exp_res/imgs/cloud-10.fw.png"); 
	background-position: right;
	color: #333333; 
}
</style>
</jsp:attribute>
	<jsp:body>
		<c:if test="${ not empty requestScope.globalNotice }">
		<div class="row"><div class="col-lg-12"><div class="alert alert-dismissable alert-warning">
		<button type="button" class="close" data-dismiss="alert">×</button>
		<p><c:out value="${ requestScope.globalNotice }" /></p>
		</div></div></div>
		</c:if>
		<!-- Jumbotron -->
		<div class="jumbotron" style="height: 320px">
			<h1>Hello!</h1>
			<p class="lead">Welcome to use Antai data platform. You can design, release and manage the interactive survey form.</p>
			<p><a class="btn btn-success" href="/jaccount">Start to use &raquo;</a></p>
			<h5><i class="icon-lock"></i>&nbsp;<em>Expedite uses jAccount SSO service</em></h5>
		</div>
		<div class="block features">
      <h2 class="title-divider">
				<span>Platform Feature</span><small>Leading engine for enterprise questionnaire form</small>
			</h2>
      <div class="row">
        <div class="feature col-sm-6 col-md-3"> <a href="#"
						style="cursor: default;"><img src="/ops/exp_res/imgs/feature-4.png"
						alt="基于Web的交互式表单设计" class="img-responsive"/></a>
          <h3 class="title">Interactive form design</h3>
          <p>User can construct powerful interactive and multi-language supported dynamic visualized forms</p>
        </div>
        <div class="feature col-sm-6 col-md-3"> <a href="#"
						style="cursor: default;"><img src="/ops/exp_res/imgs/feature-2.png"
						alt="符合业界标准" class="img-responsive"/></a>
          <h3 class="title">Conform to the Stardard</h3>
          <p>Conform to the W3C Xform standard and base on the open-sourced XML and advanced PureXML database</p> 
        </div>
        <div class="feature col-sm-6 col-md-3"> <a href="#"
						style="cursor: default;"><img src="/ops/exp_res/imgs/feature-3.png"
						alt="数据安全" class="img-responsive"/></a>
          <h3 class="title">Access control and data security</h3>
          <p>Access control based on user, to insure the form security and from data divulge.</p>
        </div>
        <div class="feature col-sm-6 col-md-3"> <a href="#"
						style="cursor: default;"><img src="/ops/exp_res/imgs/feature-1.png"
						alt="易于使用" class="img-responsive"/></a>
          <h3 class="title">Easy to use</h3>
          <p>Base on brief and modern style, and support terminals such as mobiles and tablets.</p>
        </div>
      </div>
    </div>
    </jsp:body>
</t:template_en>