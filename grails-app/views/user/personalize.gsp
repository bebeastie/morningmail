<%@ page import="com.morningmail.domain.User" %>
<%@ page import="com.morningmail.domain.Interest" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<html>
	<head>
		<title>Wakeup E-mail Signup - Step 2</title>
		<g:javascript library="jquery" plugin="jquery"/>
		<script type="text/javascript" src="/js/jquery/jquery.asmselect.js"></script>
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.7/jquery-ui.min.js"></script>
		<link rel="stylesheet" type="text/css" href="/css/jquery.asmselect.css"/>
		
		<script type="text/javascript">
		$(document).ready(function() {
		    $("select[multiple]").asmSelect({sortable:true, animate:true, highlight:true, addItemTarget: 'bottom'});
		}); 
		</script>
				<style>
			a.bookmarklet { background:#ffe; border:1px dotted #aaa;color:#444;padding:1px 2px 0px 2px;font-size:90%}
		</style>
	</head>
	<body>
		<h1>Hello ${user?.email}!</h1>
		<br>
		<g:form name="interests" action="personalize">
		<g:hiddenField name="saveInterests" value="${true}" />
		<label for="interests">What do you want in your email?</label> 
		<select id="interests" multiple="multiple" name="interests[]" title="Click to Select an Interest"> 
			<g:each var="interestKey" in="${user.interests}">
				<option value="${KeyFactory.keyToString(interestKey)}" selected="selected">${Interest.get(interestKey).displayName}</option>			
			</g:each>
			
			<g:each var="interest" in="${Interest.list(sort:'displayName', order:'asc')}">
				<g:if test="${!user.interests.contains(interest.id)}">
					<option value="${KeyFactory.keyToString(interest.id)}"
						<g:if test="${user.interests.contains(interest.id)}"> 
							selected="selected"
						</g:if>	
					>${interest.displayName}</option>
				</g:if>
			</g:each>
		</select>

		<g:actionSubmit value="Save" action="personalize" />
		</g:form>
		
				Drag this link to your toolbar:
		<a class="bookmarklet" href="javascript: q = location.href;
if (document.getSelection) {
	d = document.getSelection();
} else {
	d = '';
};
p = document.title;
void (open('http://apponthebowery.appspot.com/readLater/add?url=' + encodeURIComponent(q)
		+ '&description=' + encodeURIComponent(d) + '&title='
		+ encodeURIComponent(p), 'MorningMail', 'toolbar=no,width=275,height=140'));">Read Later</a>
		<br/>
				
	</body>
</html>