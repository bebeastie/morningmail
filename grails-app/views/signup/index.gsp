<%@ page import="com.morningmail.domain.User" %>
<html>
	<head>
		<title>Wakeup E-mail Signup - Step 1</title>
	</head>
	<body>
		<g:form controller="signup" action="index">
			<input id="email" type="text" name="email" value="${user?.email}" title="email" spellcheck="false"/>
			<g:submitButton name="signup" class="input-button" value="Sign Up"/>
		</g:form>
	</body>
</html>