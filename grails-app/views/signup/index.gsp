<%@ page import="com.morningmail.domain.User" %>
<html>
	<head>
		<title>Wakeup E-mail Signup - Step 1</title>
	</head>
	<body>
		<g:form controller="signup" action="index">
		<table>
			<tr>
				<td>E-mail:</td>
				<td>
					<input id="email" type="text" name="email" value="${user?.email}" title="email" spellcheck="false"/>
				</td>
			</tr>
			<tr>
				<td>Zip Code:</td>
				<td>
					<input id="zipCode" type="text" name="zipCode" value="${user?.zipCode}" title="zipCode" spellcheck="false"/>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<g:submitButton name="signup" class="input-button" value="Sign Up"/>
				</td>
			</tr>
		</table>
		</g:form>
	</body>
</html>