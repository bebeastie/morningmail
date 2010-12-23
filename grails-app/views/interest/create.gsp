<%@ page import="com.morningmail.domain.Interest" %>

<html>
<head>
	<title>MorningMail - Add New Feed</title>
</head>
	<body>
		<g:form controller="interest" action="create">
		<g:hiddenField name="save" value="${true}" />
		<table>
			<tr>
				<td>Name:</td>
				<td><input id="displayName" name="displayName" value="${interest?.displayName}" title="displayName" size="50"/></td>
			</tr>
			<tr>
				<td>Url: (e.g. http://feeds.nytimes.com/nyt/rss/HomePage)</td>
				<td><input id="url" name="url" value="${interest?.url}" title="url" size="100"/></td>
			</tr>
			<tr>
				<td>Max # items:</td>
				<td><g:select name="maxStories" from="${[1,2,3,4,5,6,7,8,9,10]}" value="${interest?.maxStories}"/></td>
			</tr>
			<tr>
			<td colspan="2" align="center"><g:submitButton name="save" value="Save"/></td>
			</tr>			
		</table>
		</g:form>
	</body>
</html>