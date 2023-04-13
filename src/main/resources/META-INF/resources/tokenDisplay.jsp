<%@ include file="/init.jsp" %>

<script>
showRawTokens = function() {
	var win = window.open("", "Tokens", "toolbar=no,location=no,directories=no,status=no,menubar=no," 
			+ "scrollbars=yes,resizable=yes,width=" + (screen.width/4) 
			+ ",height=" + (screen.height/2)); 
	win.document.body.innerHTML = "<%=HtmlUtil.escapeJS((String)request.getAttribute("frontendTokensHTML")) %>";
	win.focus();
};
</script>


<label class="align-baseline mb-0 mr-3">
	<clay:link
		displayTye="secondary"
		href="javascript:showRawTokens();"
		icon="list"
		label="stylebook-tokens"
		outline="<%= true %>"
		small="<%= true %>"
		title="stylebook-tokens"
		type="button"
	/>
</label>