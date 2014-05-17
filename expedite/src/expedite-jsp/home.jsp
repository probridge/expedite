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
			<xf:instance id="news">
				<documents />
			</xf:instance>
			<xf:instance id="search-instance">
                <search xmlns="">
                    <query/>
                    <query name="title" path="main-section/title" summary-field="true"/>
                    <page-size>10</page-size>
                    <page-number>1</page-number>
                    <lang />
                </search>
            </xf:instance>	
			<xf:instance id="news-search-instance">
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
            <xf:submission id="read-news"
				ref="instance('search-instance')" validate="false"
				method="post"
				resource="/fr/service/persistence/search/expedite/news"
				replace="instance" instance="news">
            </xf:submission>
<exp:roleIterator mode="participant">
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
				<xf:send submission="read-news"/>
			</xf:action>
		</xf:model>	
    </jsp:attribute>
	<jsp:attribute name="jscode">
	<script language="JavaScript" src="/ops/exp_res/js/home.js"/>
	</jsp:attribute>
	<jsp:body>
	<div class="row">
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
			<h2 style="margin-top: 0px">欢迎使用安泰实验中心数据平台!</h2>
			<h5>
			今天您想从哪儿开始？
			</h5>
		</div>
	</div>
	<div class="row">
<c:if test="${ group eq 'user' or group eq 'editor' or group eq 'admin'}">
<exp:sectionVisible requiredRole="survey-homepage-visible">
	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
		<div class="panel panel-success">
		  <div class="panel-heading">
		    <h3 class="panel-title">调研问卷 Survey</h3>
		  </div>
		  <div class="panel-body">
			<h4>我收到的问卷邀请</h4>
			<exp:roleIterator mode="participant">
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
			<hr/>
			<a href="#" onclick="show_personal_info_dialog();">
			<xf:output value="if (count(instance('my-info')/document) = 0) then '报名参加调研' else '调研报名信息'"/>
			</a>
		  </div>
		</div>	
	</div>
</exp:sectionVisible>
<exp:sectionVisible requiredRole="gongwu-homepage-visible">
	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
		<div class="panel panel-info">
		  <div class="panel-heading">
		    <h3 class="panel-title">公务平台 Workplace</h3>
		  </div>
		  <div class="panel-body">
		  	<h4>公务平台表单</h4>
			<hr/>
<exp:formIterator mode="participant" appName="gongwu">
			<a href="/fr/${pageScope.tagAppName}/${pageScope.tagFormName}/new" onclick="return openWin(this.href,'formrunner');">${pageScope.tagFormTitle}</a><br/>
</exp:formIterator>
		  </div>
		</div>	
	</div>
</exp:sectionVisible>
<exp:sectionVisible requiredRole="acem-homepage-visible">
	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
		<div class="panel panel-info">
		  <div class="panel-heading">
		    <h3 class="panel-title">安泰实验中心 ACEM Lab</h3>
		  </div>
		  <div class="panel-body">
		  	<h4>资源申请表单</h4>
			<hr/>
<exp:formIterator mode="participant" appName="acem">
			<a href="/fr/${pageScope.tagAppName}/${pageScope.tagFormName}/new" onclick="return openWin(this.href,'formrunner');">${pageScope.tagFormTitle}</a><br/>
</exp:formIterator>
		  </div>
		</div>
	</div>
</exp:sectionVisible>
<exp:sectionVisible requiredRole="*">
	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
		<div class="panel panel-warning">
		  <div class="panel-heading">
		    <h3 class="panel-title">公告和新闻 News</h3>
		  </div>
		  <div class="panel-body">
		  	<ul>
			<xf:repeat ref="instance('news')/document">
			<li>
				<xf:var name="link" value="concat('/fr/expedite/news/view/', @name)" />
				<a href="{$link}" onclick="return openWin(this.href,'formrunner');">
				<xf:output value="details/detail[1]/text()"/>
				</a>
			</li>
			</xf:repeat>
			</ul>
		  </div>
		</div>
	</div>
</exp:sectionVisible>
</c:if>
</div>
<div class="row">
<c:if test="${ group eq 'admin' or group eq 'editor'}">
	<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
		<div class="panel panel-danger">
		  <div class="panel-heading">
		    <h3 class="panel-title">表单管理</h3>
		  </div>
		  <div class="panel-body">
			<h3>
			<a href="/roles">准备权限</a>
			<a href="/fr/orbeon/builder/summary" onclick="return openWin(this.href,'formbuilder');">设计问卷</a>
			<a href="/xquery">样本筛选</a>
			<a href="#" onclick="show_export_dialog();">问卷数据</a>
			</h3>
		  </div>
		</div>	
	</div>
</c:if>
<c:if test="${ group eq 'admin'}">
	<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
		<div class="panel panel-danger">
		  <div class="panel-heading">
		    <h3 class="panel-title">系统管理员</h3>
		  </div>
		  <div class="panel-body">
			<h3>
			<a href="/users">用户管理</a>
			<a href="/roles">角色定义</a>
			<a href="/fr/expedite/news/summary" onclick="return openWin(this.href,'formrunner');">通告管理</a>
			<a href="#" onclick="alert('公务平台数据库为gongwu, 安泰实验室数据库为acem, 新闻为expedite数据库的news表单, 调研报名样本库为survey/userinfo')">系统信息</a>
			</h3>
		  </div>
		</div>	
	</div>
</c:if>
</div>
<div class="modal fade" id="exportFormDataDialog" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h3 class="modal-title">
				表单数据导出
				</h3>
			</div>
			<div class="modal-body">
			<h4>可以将选择的表单所有样本数据以Excel形式导出</h4>
			<hr style="margin: 2px 0"/>
			<p>
			<select onchange="openWin(this.value,'_self');">
			<option>请选择要导出的表单</option>
<exp:formIterator mode="editor">
			<option value="../export?appName=${pageScope.tagAppName}&amp;formName=${pageScope.tagFormName}">
			${pageScope.tagFormTitle}
			</option>
</exp:formIterator>
			</select>
			</p>
			<a href="/fr/survey/userinfo/new" onclick="return openWin(this.href,'formrunner');">
			<xf:output value="if (count(instance('my-info')/document) = 0) then '点击报名参加调研' else ''"/>
			</a>
			</div>
			<div class="modal-footer">
			<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<div class="modal fade" id="personalInfoDialog" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h3 class="modal-title">
				您的调研报名信息
				</h3>
			</div>
			<div class="modal-body">
			<h4>提供准确的个人信息将有助于老师根据调研样本的准确筛选，提高调研结果的准确性。</h4>
			<hr style="margin: 2px 0"/>
			<p>
			<xf:repeat ref="instance('my-info')/document[contains(@operations,'update')]">
				<xf:var name="link" value="concat('/fr/survey/userinfo/view/', @name)" />
				<a href="{$link}" onclick="return openWin(this.href,'formrunner');">维护我的信息</a>
			</xf:repeat>
			</p>
			<a href="/fr/survey/userinfo/new" onclick="return openWin(this.href,'formrunner');">
			<xf:output value="if (count(instance('my-info')/document) = 0) then '点击报名参加调研' else ''"/>
			</a>
			</div>
			<div class="modal-footer">
			<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<c:if test="${ group eq 'admin'}">
 	<fr:xforms-inspector/>
</c:if>
    </jsp:body>
</t:template>