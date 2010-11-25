<%@ page import="com.morningmail.domain.User" %>
<html>
	<head>
		<title>Wakeup E-mail Signup - Step 1</title>
	</head>
	<body>
		<g:form controller="signup" action="index">
		<table>
			<tr>
				<td>Invite code:</td>
				<td>
					<input id="inviteCode" type="text" name="inviteCode" value="" title="inviteCode" spellcheck="false"/>
				</td>
			</tr>
			<tr>
				<td>Name:</td>
				<td>
					<input id="name" type="text" name="name" value="${user?.name}" title="name" spellcheck="false"/>
				</td>
			</tr>
			<tr>
				<td>E-mail:</td>
				<td>
					<input id="email" type="text" name="email" value="${user?.email}" title="email" spellcheck="false"/>
				</td>
			</tr>
			<tr>
				<td>Zip code:</td>
				<td>
					<input id="zipCode" type="text" name="zipCode" value="${user?.zipCode}" title="zipCode" spellcheck="false"/>
				</td>
			</tr>
			 
			<tr>
				<td>Delivery Time:</td>
				<td>
					<input id="deliveryTime" type="text" name="deliveryTime" value="${user?.localDeliveryTime}" title="deliveryTime" spellcheck="false"/>
					<!-- <g:select name="deliveryTime" from="${deliveryTimes}" value="${user?.localDeliveryTime}"/> -->
					<g:select name="timeZone" from="${timeZones}" value="${user?.timeZone}"/> 
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