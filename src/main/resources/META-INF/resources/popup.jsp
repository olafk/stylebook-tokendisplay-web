<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %><%@ 
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
page import="com.liferay.portal.kernel.service.LayoutLocalServiceUtil"%><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%><%@
page import="javax.portlet.PortletRequest"%><%@
page import="com.liferay.portal.kernel.portlet.PortletURLFactoryUtil"%><%@
page import="com.liferay.portal.kernel.portlet.LiferayPortletURL"%>
<liferay-frontend:defineObjects />
<liferay-theme:defineObjects />
<portlet:defineObjects />

<%
LiferayPortletURL modalPortletURL = PortletURLFactoryUtil.create(request, (String) request.getAttribute("styleGuidePortletId"), 
		LayoutLocalServiceUtil.getDefaultPlid(scopeGroupId), PortletRequest.RENDER_PHASE);
modalPortletURL.setWindowState(LiferayWindowState.POP_UP);
%>
<script>
showTokens = function() {
	var win = window.open("<%=modalPortletURL %>", "Tokens", "toolbar=no,location=no,directories=no,status=no,menubar=no," 
			+ "scrollbars=yes,resizable=yes,width=" + (screen.width/4) 
			+ ",height=" + (screen.height/2)); 
	win.focus();
};
</script>
<li>
	<label class="align-baseline mb-0 mr-3">
		<clay:link
			displayTye="secondary"
			href="javascript:showTokens();"
			icon="list"
			label="stylebook-tokens"
			outline="<%= true %>"
			small="<%= true %>"
			title="stylebook-tokens"
			type="button"
		/>
	</label>
</li>

