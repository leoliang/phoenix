<%@ page contentType="text/html; charset=utf-8"%>

<%@ taglib prefix="a" uri="http://www.dianping.com/phoenix/console"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>

<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.home.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.home.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.home.Model" scope="request" />

<a:layout>
	<ul class="breadcrumb">
		<li><a href="${model.webapp}/console/home">Home</a><span class="divider">/</span></li>
		<c:choose>
			<c:when test="${payload.type=='phoenix-agent'}">
				<li class="active">Agent</li>
			</c:when>
			<c:otherwise>
				<li class="active">Kernel</li>
			</c:otherwise>
		</c:choose>
	</ul>
	<div class="alert alert-success" style="display: none;">
            <button type="button" class="close" data-dismiss="alert">×</button>
            <strong>Query Condition: </strong>&emsp;<span id="queryInfo"></span>
    </div>
	<input type="hidden" id="payload_dependencies" value="${payload.dependencies}">
	<input type="hidden" id="payload_operators" value="${payload.operators}">
	<input type="hidden" id="payload_versions" value="${payload.versions}">
	<input type="hidden" id="payload_joints" value="${payload.joints}">
	<input type="hidden" id="payload_agentversion" value="${payload.agentVersion}">
	<input type="hidden" id="payload_agentoperator" value="${payload.agentOperator}">
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span12">
				<ul class="nav nav-tabs" id="myTab">
					<c:set var="flag" scope="page" value="true" />
					<c:forEach var="product" items="${model.products}">
						<c:choose>
							<c:when test="${flag}">
								<li class="active bl"><c:set var="flag" scope="page" value="false" />
							</c:when>
							<c:otherwise>
								<li class="bl">
							</c:otherwise>
						</c:choose>
						<a href="#${product.name}">${product.name}</a>
						</li>
					</c:forEach>
					<li class="pull-right"><input type="text" style="margin-bottom: -12px;" placeholder="输入项目名...">
						<button class="btn" style="margin-bottom: -12px;">Search</button></li>
				</ul>
			</div>
		</div>
		<div class="tab-content">
			<c:set var="flag" scope="page" value="true" />
			<c:forEach var="product" items="${model.products}">
				<c:choose>
					<c:when test="${flag}">
						<div class="tab-pane active row-fluid" id="${product.name}">
							<c:set var="flag" scope="page" value="false" />
					</c:when>
					<c:otherwise>
						<div class="tab-pane row-fluid" id="${product.name}">
					</c:otherwise>
				</c:choose>
				<div class="span12">
					<table class="table table-bordered table-striped table-condensed table-page">
						<thead>
							<tr>
								<th width="35">Status</th>
								<th width="220">Project</th>
								<th width="120">Owner</th>
								<th width="150">Machine(Active/Inactive)</th>
								<th>Kernel Versions</th>
								<th>App Versions</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="domain" items="${product.domains}">
								<tr>
									<td><img src="${model.webapp}/img/green.gif"></td>
									<td><a class="toProject" href="?op=project&type=${payload.plan.warType}&project=${domain.value.name}">${domain.value.name}</a></td>
									<td><c:choose>
											<c:when test="${fn:length(domain.value.owners) eq 0}">N/A</c:when>
											<c:otherwise>
												<c:forEach var="owner" items="${domain.value.owners}">
													${owner}&nbsp;
													</c:forEach>
											</c:otherwise>
										</c:choose></td>
									<td>${domain.value.activeCount}/${domain.value.inactiveCount}</td>
									<td><c:choose>
											<c:when test="${fn:length(domain.value.kernelVersions) eq 0}">N/A</c:when>
											<c:otherwise>
												<c:forEach var="kerVersion" items="${domain.value.kernelVersions}">
													${kerVersion}&nbsp;
													</c:forEach>
											</c:otherwise>
										</c:choose></td>
									<td><c:choose>
											<c:when test="${fn:length(domain.value.appVersions) eq 0}">N/A</c:when>
											<c:otherwise>
												<c:forEach var="appVersion" items="${domain.value.appVersions}">
													${appVersion}&nbsp;
													</c:forEach>
											</c:otherwise>
										</c:choose></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
		</div>
		</c:forEach>
	</div>
	</div>
	<res:useJs value="${res.js.local['console-home.js']}" target="home-js" />
	<res:jsSlot id="home-js" />
</a:layout>