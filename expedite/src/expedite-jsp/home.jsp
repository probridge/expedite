<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:template jquery="no">
	<jsp:attribute name="header">
		<link rel="stylesheet" type="text/css" media="screen"
			href="/ops/exp_res/css/theme-style.css" />	
		<xf:model id="home-form-model" xxf:xpath-analysis="true">
			<xf:instance id="fr-service-request-instance"
				xxf:exclude-result-prefixes="#all">
				<request />
			</xf:instance>
			<xf:instance id="fr-service-response-instance"
				xxf:exclude-result-prefixes="#all">
				<response />
			</xf:instance>
			
			<xf:instance id="abcdef-instance" class="fr-database-service"
				xxf:exclude-result-prefixes="#all">
				<body>&lt;sql:config
					xmlns:sql="http://orbeon.org/oxf/xml/sql"&gt;
					&lt;sql:datasource&gt;db2&lt;/sql:datasource&gt;
					&lt;sql:query&gt;SELECT * FROM EXPEDITE_ROLES
					WHERE "user_name" = &lt;sql:param type="xs:string" select="'pennyge@gmail.com'"/&gt;
					&lt;/sql:query&gt;					
					&lt;/sql:config&gt;
				</body>
			</xf:instance>
			<xf:submission id="abcdef-submission" class="fr-database-service"
				ref="instance('fr-service-request-instance')"
				resource="/fr/service/custom/orbeon/database" method="post"
				serialization="application/xml" replace="instance"
				instance="fr-service-response-instance" />

			<xf:instance id="my-info">
				<documents />
			</xf:instance>
			<xf:instance id="my-invitation">
				<documents />
			</xf:instance>
			<xf:instance id="info-search-instance">
                <search xmlns="">
                    <query />
                    <page-size>5</page-size>
                    <page-number>1</page-number>
                    <lang />
                </search>
            </xf:instance>			
			<xf:instance id="invitation-search-instance">
                <search xmlns="">
                    <query />
					<query name="email" path="section-invitation[1]/email[1]"
					type="xf:email" control="input" search-field="true"
					summary-field="true" match="exact">pennyge@gmail.com</query>
					<query name="database-name"
					path="section-invitation[1]/database-name[1]" type="xs:string"
					control="input" summary-field="true" />
                    <query name="form-name"
					path="section-invitation[1]/form-name[1]" type="xs:string"
					summary-field="true" />
                    <query name="expire-date"
					path="section-invitation[1]/expire-date[1]" type="xf:date"
					control="input" summary-field="true" />                    
                    <page-size>10</page-size>
                    <page-number>1</page-number>
                    <lang />
                </search>
            </xf:instance>
            <xf:submission id="read-my-info"
				ref="instance('info-search-instance')" validate="false"
				method="post"
				resource="/fr/service/persistence/search/survey/userinfo"
				replace="instance" instance="my-info">
            </xf:submission>
			<xf:submission id="read-my-invitations"
				ref="instance('invitation-search-instance')" validate="false"
				method="post"
				resource="/fr/service/persistence/search/survey/invitation"
				replace="instance" instance="my-invitation">
			</xf:submission>
			<xf:action ev:event="xforms-ready">
				<xf:send submission="read-my-info"/>
				<xf:send submission="read-my-invitations" />
				<xf:send submission="abcdef-submission" />
			</xf:action>
			
			<xf:action ev:event="xforms-submit" ev:observer="abcdef-submission">
					<xf:insert ref="instance('fr-service-request-instance')"
						origin="saxon:parse(instance('abcdef-instance'))" />		 
			</xf:action>
		</xf:model>	
    </jsp:attribute>
	<jsp:attribute name="jscode"></jsp:attribute>
	<jsp:body>
	<h3>管理我的信息(survey/userinfo)</h3><br/>
	<xf:repeat ref="instance('my-info')/document">
		<xf:var name="link" value="concat('/fr/survey/userinfo/view/', @name)" />
		<a href="{$link}" target="_blank">维护我的信息</a>
	</xf:repeat>
	<hr />
	<h3>参与邀请调查(from survey/invitation)</h3><br/>
	<xf:repeat ref="instance('my-invitation')/document">
		<xf:var name="link" value="concat('/fr/', details/detail[2] ,'/', details/detail[3], '/new')" />
		<a href="{$link}" target="_blank">开始</a><br/>
	</xf:repeat>
	<hr />
	<h3>设计、管理和发布调查(link to form builder summary page)</h3><br/>
	<a href="/fr/orbeon/builder/summary" target="_blank">开始</a><br/>
	<hr />
	<h3>样本筛选(link to survey/userinfo/summary)</h3><br/>
	<a href="/fr/survey/userinfo/summary" target="_blank">开始</a><br/>
	<hr />
	<h3>分配角色(assign roles to selected sample user)</h3><br/>
	<a href="/fr/survey/userinfo/summary" target="_blank">开始</a><br/>
	<hr />
	<h3>用户管理(user crud)</h3><br/>
	<a href="#" target="_blank">开始</a><br/>
	<hr />
	<h3>角色管理(role table crud and permission file update)</h3><br/>
	<a href="#" target="_blank">开始</a><br/>
	<hr />
	<h3>编辑角色配置(editor role assignment)</h3><br/>
	<a href="#" target="_blank">开始</a><br/>
	<hr />
	<fr:xforms-inspector/>
    </jsp:body>
</t:template>