<%@ page import="com.morningmail.domain.Interest" %>

<html>
<head>
	<title>MorningMail - Add New Feed</title>
</head>
	<body>
		<g:form controller="newsletter" action="addInterest">
		<g:hiddenField name="save" value="${true}" />
		<g:hiddenField name="newsletterId" value="${params.newsletterId}" />
		
		<table>
			<tr>
				<td align="center" colspan="2">
					<h3>Add an interest</h3>
				</td>
			</tr>
			<tr>
				<td align="left" colspan="2">
					<h3 style="color:red;">${flash.message}</h3>
				</td>
			</tr>
			<tr>
				<td colspan="2">Name:</td>
			</tr>
			<tr>
				<td colspan="2"><input id="displayName" name="displayName" value="${params.displayName}" title="displayName" size="50"/></td>
			</tr>
			<tr>
				<td colspan="2">Url (e.g. http://feeds.nytimes.com/nyt/rss/HomePage):</td>
			</tr>
			<tr>
				<td colspan="2"><input id="url" name="url" value="${params.url}" title="url" size="60"/></td>
			</tr>
			<tr> 
				<td colspan="2">Max # items:</td>		
			</tr>
			<tr>
				<td colspan="2"><g:select name="maxStories" from="${[1,2,3,4,5,6,7,8,9,10]}" value="${params.maxStories}"/></td>
			</tr>

			<tr>
			<td align="left"><a href="/dashboard">Back</a></td>
			<td align="right"><g:submitButton name="save" value="Save"/></td>
			</tr>			
		</table>
		</g:form>
	</body>
</html>