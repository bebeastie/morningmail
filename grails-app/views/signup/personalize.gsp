<%@ page import="com.morningmail.domain.User" %>
<%@ page import="com.morningmail.domain.Interest" %>

<%
Interest topNews = Interest.findByType(Interest.TYPE_TOP_NEWS)
Interest googleCal = Interest.findByType(Interest.TYPE_GOOGLE_CAL)


 %>
<html>
	<head>
		<title>Wakeup E-mail Signup - Step 2</title>
	</head>
	<body>
		<h1>Hello ${user?.email}!</h1>
		<br>
		<g:form name="interests" action="personalize">
		<g:hiddenField name="saveInterests" value="${true}" />
		<table>
		<tr>
			<td>${topNews.displayName}</td>
			<td><g:checkBox name="${topNews.type}" value="${user.interests.contains(topNews.id)}" /></td>
		</tr>
		<tr>
			<td>${googleCal.displayName}</td>
			<td><g:checkBox name="${googleCal.type}" value="${user.interests.contains(googleCal.id)}" /></td>
		</tr>
		<tr>
		<td colspan="2" align="center"><g:actionSubmit value="Save" action="personalize" /></td>
		</tr>
		
		</table>
		
		</g:form>
		
	</body>
</html>