<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:template>
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
			<h1>你好！</h1>
			<p class="lead">欢迎使用Expedite表单平台，设计、发布和管理交互式表单问卷。</p>
			<p><a class="btn btn-success" href="/jaccount">开始使用 &raquo;</a></p>
			<h5><i class="icon-lock"></i>&nbsp;<em>Expedite使用上海交大jAccount统一认证体系</em></h5>
		</div>
		<div class="block features">
      <h2 class="title-divider">
				<span>平台特点</span><small>领先的企业问卷表单引擎</small>
			</h2>
      <div class="row">
        <div class="feature col-sm-6 col-md-3"> <a href="#"
						style="cursor: default;"><img src="/ops/exp_res/imgs/feature-4.png"
						alt="基于Web的交互式表单设计" class="img-responsive"/></a>
          <h3 class="title">交互式表单设计</h3>
          <p>使用浏览器就可以以可视化方式构建前所未有的强大的交互式、支持多语种的动态表单。</p>
        </div>
        <div class="feature col-sm-6 col-md-3"> <a href="#"
						style="cursor: default;"><img src="/ops/exp_res/imgs/feature-2.png"
						alt="符合业界标准" class="img-responsive"/></a>
          <h3 class="title">符合业界标准</h3>
          <p>符合业界W3C XForm标准的表单引擎，基于开放的XML和先进PureXML数据库格式存储结构化信息数据。</p> 
        </div>
        <div class="feature col-sm-6 col-md-3"> <a href="#"
						style="cursor: default;"><img src="/ops/exp_res/imgs/feature-3.png"
						alt="数据安全" class="img-responsive"/></a>
          <h3 class="title">权限控制和数据安全</h3>
          <p>基于角色的权限控制，确保表单作为重要资产不会泄漏，样本据不会被意外流失。</p>
        </div>
        <div class="feature col-sm-6 col-md-3"> <a href="#"
						style="cursor: default;"><img src="/ops/exp_res/imgs/feature-1.png"
						alt="易于使用" class="img-responsive"/></a>
          <h3 class="title">易于使用</h3>
          <p>使用简洁现代的界面设计风格，表单支持移动终端如手机、平板等设备的访问。</p>
        </div>
      </div>
    </div>
    </jsp:body>
</t:template>