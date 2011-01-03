<%@ page import="com.morningmail.domain.User" %>
<%@ page import="com.morningmail.domain.Interest" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<html>
	<head>
		<title>Wakeup E-mail Signup - Step 2</title>
		<g:javascript library="jquery" plugin="jquery"/>
		<script type="text/javascript" src="/js/jquery/jquery.asmselect.js"></script>
		<script type="text/javascript" src="/js/jquery/jquery-ui.min.js"></script>
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
	<g:form name="interests" action="personalize">
	<table>
		<tr>
			<td align="left"><h1>Hello ${user?.name}!</h1></td>
			<td align="right"><a href="/user/logout">Sign out</a></td>
		</tr>
		<tr>
			<td colspan="2" align="left">
		
		
		<g:hiddenField name="saveInterests" value="${true}" />
		<label for="interests">Select your interests from the list below or <a href="/interest/create">add your own</a>.</label> 
		<select id="interests" multiple="multiple" name="interests[]" title="Click to Select an Interest"> 
			<g:each var="interestKey" in="${user.interests}">
				<option value="${KeyFactory.keyToString(interestKey)}" selected="selected">${interestMap.get(interestKey).displayName}</option>			
			</g:each>
			
			<g:each var="interest" in="${interestList}">
				<g:if test="${!user.interests.contains(interest.id)}">
					<option value="${KeyFactory.keyToString(interest.id)}"
						<g:if test="${user.interests.contains(interest.id)}"> 
							selected="selected"
						</g:if>	
					>${interest.displayName}</option>
				</g:if>
			</g:each>
		</select>
		</td></tr>
		<tr>
			<td colspan="2"><font color="red">Don't forget to press the save button.</font></td>
		</tr>
		<tr>
			<td colspan="2">
				<g:actionSubmit value="Save" action="personalize" />
			
		</td></tr>
		</table>
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