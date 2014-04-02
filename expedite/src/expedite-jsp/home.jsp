<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="exp" uri="http://www.probridge.com.cn/expedite-tags"%>
<t:template jquery="no">
	<jsp:attribute name="header">
		<link rel="stylesheet" type="text/css" media="screen"
			href="/ops/exp_res/css/theme-style.css" />	
		<xf:model id="home-form-model" xxf:xpath-analysis="true">
			<xf:instance id="my-info">
				<documents />
			</xf:instance>
			<xf:instance id="search-instance">
                <search xmlns="">
                    <query />
                    <page-size>10</page-size>
                    <page-number>1</page-number>
                    <lang />
                </search>
            </xf:instance>	
            <xf:submission id="read-my-info"
				ref="instance('search-instance')" validate="false"
				method="post"
				resource="/fr/service/persistence/search/survey/userinfo"
				replace="instance" instance="my-info">
            </xf:submission>
<exp:roleIterator mode="sample">
			<xf:instance id="instance-${pageScope.tagRoleName}">
				<documents />
			</xf:instance>
			<xf:submission id="submission-${pageScope.tagRoleName}"
				ref="instance('search-instance')" validate="false"
				method="post"
				resource="/fr/service/persistence/search/${pageScope.tagAppName}/${pageScope.tagFormName}"
				replace="instance" instance="instance-${pageScope.tagRoleName}">
			</xf:submission>
			<xf:send ev:event="xforms-ready" submission="submission-${pageScope.tagRoleName}" />
</exp:roleIterator>
			<xf:action ev:event="xforms-ready">
				<xf:send submission="read-my-info"/>
			</xf:action>
		</xf:model>	
    </jsp:attribute>
	<jsp:attribute name="jscode">
	<script language="JavaScript" src="/ops/exp_res/js/home.js"/>
	</jsp:attribute>
	<jsp:body>
	<div class="row">
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
			<h2 style="margin-top: 0px">欢迎使用Expedite!</h2>
			<h5>
			今天您想从哪儿开始？
			</h5>
		</div>
	</div>
	<div class="row">
<c:if test="${ group eq 'user' or group eq 'editor' or group eq 'admin'}">
	<c:if test="${ group eq 'user' }"><c:set var="columnWidth" value="12" /></c:if>
	<c:if test="${ group eq 'editor' }"><c:set var="columnWidth" value="8" /></c:if>
	<c:if test="${ group eq 'admin' }"><c:set var="columnWidth" value="4" /></c:if>
	<div class="col-lg-${columnWidth} col-md-${columnWidth} col-sm-12 col-xs-12">
		<div class="panel panel-default panel-success">
		  <div class="panel-heading">
		    <h3 class="panel-title">用户</h3>
		  </div>
		  <div class="panel-body">
			<h3>我的个人信息</h3>
			<hr />
			<xf:repeat ref="instance('my-info')/document[contains(@operations,'update')]">
				<xf:var name="link" value="concat('/fr/survey/userinfo/view/', @name)" />
				<a href="{$link}" onclick="return openWin(this.href,'formrunner');">维护我的信息</a>
			</xf:repeat>
			<a href="/fr/survey/userinfo/new" onclick="return openWin(this.href,'formrunner');">
			<xf:output value="if (count(instance('my-info')/document) = 0) then '新建个人信息' else ''"/>
			</a>
			<h3>我收到的问卷邀请</h3>
			<hr />
			<exp:roleIterator mode="sample">
				${pageScope.tagDescription}
				<xf:repeat ref="instance('instance-${pageScope.tagRoleName}')/document">
					<xf:var name="link" value="concat('/fr/${pageScope.tagAppName}/${pageScope.tagFormName}/view/', @name)" />
					<a href="{$link}" class="pull-right" onclick="return openWin(this.href,'formrunner');">完成</a>
				</xf:repeat>
				<a href="/fr/${pageScope.tagAppName}/${pageScope.tagFormName}/new" class="pull-right" onclick="return openWin(this.href,'formrunner');">
				<xf:output value="if (count(instance('instance-${pageScope.tagRoleName}')/document) = 0) then '开始' else ''"/>
				</a>
				<br/>
			</exp:roleIterator>
			<hr />
			<h3>公共问卷调研</h3>
			暂无
			<hr />
			<h3>实验中心日常表单</h3>
			暂无
			<hr />
		  </div>
		</div>	
	</div>
</c:if>
<c:if test="${ group eq 'admin' or group eq 'editor'}">
	<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">
		<div class="panel panel-default panel-info">
		  <div class="panel-heading">
		    <h3 class="panel-title">表单管理员</h3>
		  </div>
		  <div class="panel-body">
			<h3>准备表单和权限</h3><br/>
			<a href="/roles">开始配置</a><br/>
			<h3>设计、管理和发布问卷</h3><br/>
			<a href="/fr/orbeon/builder/summary" onclick="return openWin(this.href,'formbuilder');">表单编辑器</a><br/>
			<h3>目标用户高级筛选</h3><br/>
			<a href="/xquery">开始使用xQuery</a><br/>
			<h3>问卷数据下载</h3><br/>
<exp:formIterator>
			<a href="/export?appName=${pageScope.tagAppName}&amp;formName=${pageScope.tagFormName}">${pageScope.tagFormTitle}</a><br/>
</exp:formIterator>
		  </div>
		</div>	
	</div>
</c:if>
<c:if test="${ group eq 'admin'}">
	<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">
		<div class="panel panel-default panel-warning">
		  <div class="panel-heading">
		    <h3 class="panel-title">系统管理员</h3>
		  </div>
		  <div class="panel-body">
			<h3>用户管理</h3><br/>
			<a href="/users">开始</a><br/>
			<h3>建库和权限管理</h3><br/>
			<a href="/roles">开始</a><br/>
			<h3>系统设置</h3><br/>
			<a href="#">开始</a><br/>
		  </div>
		</div>	
	</div>
</c:if>
	</div>
<c:if test="${ group eq 'admin'}">
 	<fr:xforms-inspector/>
</c:if>
    </jsp:body>
</t:template>