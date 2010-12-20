<%@ page import="com.morningmail.controller.UserController" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MorningMail - Read Later</title>
</head>
<body>
<g:form controller="user" action="login">
	<input type="hidden" name="jump" value="${jump}"/>
	<input type="hidden" name="display" value="${UserController.LOGIN_DISPLAY_MINIMAL}"/>
	
	<table>
		<tr>
			<td colspan="2">
			Morning Mail - Please log in
			</td>

		</tr>
		<tr>
			<td>E-mail:</td>
			<td><input id="email" type="text" name="email" value=""
				title="email" spellcheck="false" /></td>
		</tr>

		<tr>
			<td>Password:</td>
			<td><input id="password" type="password" name="password"
				value="" title="password" spellcheck="false" /></td>
		</tr>
		<tr>
			<td colspan="2" align="center"><g:submitButton name="login"
				class="input-button" value="Login" /></td>
		</tr>
	</table>
</g:form>
</body>
</html>