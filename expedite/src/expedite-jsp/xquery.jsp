<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="exp" uri="http://www.probridge.com.cn/expedite-tags"%>
<t:template jquery="no">
	<jsp:attribute name="jscode">
	<script language="JavaScript">
	$('document').ready(function() {
    	$('#myTab a:first').tab('show');
	});
  	</script>
	</jsp:attribute>
	<jsp:attribute name="header">
		<xf:model id="xquery-form-model" xxf:xpath-analysis="true">
		<xf:instance id="search-result">
			<documents />
		</xf:instance>
		<xf:instance id="post-data-instance">
			<data xmlns="">
       			<selectedUser type="xs:string"/>
       			<selectedRole type="xs:string"/>
       		</data>
        </xf:instance>
		<xf:instance id="search-instance">
	        <search xmlns="">
	           <db2xquery>$XML//${emailControl}[contains(text(),"expedite@probridge.com.cn")]</db2xquery>
	           <query name="${emailControl}" path="${emailPath}" summary-field="true"/>
	           <page-size>1000</page-size>
	           <page-number>1</page-number>
	           <drafts>exclude</drafts>
	           <lang />
	        </search>
	    </xf:instance>
	    <xf:instance id="search-template">
	        <db2xquery xmlns="">$XML//control[boolean expression]</db2xquery>
    	</xf:instance>
	    <xf:submission id="xquery-it"
			ref="instance('search-instance')" validate="false"
			method="post"
			resource="/fr/service/persistence/search/${appName}/${formName}"
			replace="instance" instance="search-result">
	    </xf:submission>
	    <xf:submission id="post-it"
			ref="instance('post-data-instance')" validate="true"
			method="post"
			resource="/assign"
			replace="instance" instance="post-data-instance">
			<xf:message ev:event="xforms-submit-done" level="modal">邀请完成</xf:message>
			<xf:message ev:event="xforms-submit-error" level="modal">操作错误</xf:message>
	    </xf:submission>
	    <!-- <xf:bind nodeset="instance('post-data-instance')/*" relevant="count(instance('search-result')/document) gt 0 or not(instance('post-data-instance')/selectedUser='')"/> -->
		</xf:model>	
		<style type="text/css">
		.xforms-input input { width: 50em; margin-bottom: 2px }
		</style>		
	</jsp:attribute>
	<jsp:body>
    <h2 id="nav-tabs">用户筛选</h2>
	<ul class="nav nav-tabs" role="tablist" id="mytab">
	  <li class="active"><a href="#manual" role="tab" data-toggle="tab">批量添加</a></li>
	  <li><a href="#filter" role="tab" data-toggle="tab">XQuery筛选</a></li>
	</ul>
	<!-- Tab panes -->
	<div class="tab-content">
	  <div class="tab-pane" id="filter">
	<xf:group ref="instance('search-instance')">
		<h4>请输入查询条件：</h4>
		<xf:repeat ref="db2xquery" id="xqueries">
			<xf:input ref=".">
				<xf:label>与条件：</xf:label>
			</xf:input>
			<xf:trigger>
                <xf:label>去除</xf:label>
                <xf:delete ev:event="DOMActivate"
                           context="instance('search-instance')" nodeset="db2xquery" at="index('xqueries')"/>
            </xf:trigger>
            <br/>
		</xf:repeat>
		<xf:trigger>
		    <xf:label>增加条件</xf:label>
		    <xf:insert ev:event="DOMActivate" context="instance('search-instance')" 
		    nodeset="db2xquery" at="last()" position="after" origin="instance('search-template')"/>
		</xf:trigger>
		<xf:submit submission="xquery-it">
		    <xf:label>提交查询</xf:label>
		</xf:submit>
	</xf:group>
	<xf:group ref="instance('post-data-instance')">
		<xf:select ref="selectedUser" appearance="full">
			<xf:label>查询结果</xf:label>
				<xf:itemset nodeset="instance('search-result')/document">
					<xf:label ref="details/detail[1]/text()"/>
					<xf:value ref="details/detail[1]/text()"/>
				</xf:itemset>
		</xf:select>
		<xf:trigger appearance="minimal">
          <xf:label>全选</xf:label>
          <xf:setvalue ev:event="DOMActivate" ref="instance('post-data-instance')/selectedUser"
          	value="string-join(for $f in instance('search-result')/document return $f/details/detail[1]/text(),' ')"/>
        </xf:trigger>
       
		<xf:trigger appearance="minimal">
          <xf:label>不选</xf:label>
          <xf:setvalue ev:event="DOMActivate" ref="instance('post-data-instance')/selectedUser"/>
        </xf:trigger>
		</xf:group>
	  </div>
	  <div class="tab-pane active" id="manual">
		<h4>请输入用户Email清单</h4>
		<xf:group ref="instance('post-data-instance')">
			<xf:textarea ref="selectedUser" xxf:cols="80" xxf:rows="10">
				<xf:label>用户Email列表：</xf:label>
				<xf:hint>用半角逗号或空格、回车分隔</xf:hint>
			</xf:textarea>
		</xf:group>
		<br/>
	</div>
	</div>
	<hr/>
	<xf:group ref="instance('post-data-instance')">
	<xf:select1 appearance="minimal" ref="selectedRole">
	<xf:label>添加至角色：</xf:label>
	<xf:item>
      		<xf:label>请选择角色</xf:label>
      		<xf:value/>
  		</xf:item>
  		<exp:roleIterator mode="forms">
	<xf:item>
      		<xf:label>${pageScope.tagRoleName} [${pageScope.tagDescription}]</xf:label>
      		<xf:value>${pageScope.tagRoleName}</xf:value>
  		</xf:item>
  		</exp:roleIterator>
	</xf:select1>
	<br/>
	<xf:submit submission="post-it" ref="selectedRole">
    	<xf:label>发送邀请</xf:label>
	</xf:submit>
	</xf:group>
	<fr:xforms-inspector/>
    </jsp:body>
</t:template>