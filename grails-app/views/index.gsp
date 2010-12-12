<%@ page import="com.morningmail.domain.User" %>
<%@ page import="com.morningmail.utils.DateUtils" %>
<%
	def timeZones = DateUtils.TIME_ZONES
	def deliveryTimes = DateUtils.DELIVERY_TIMES

	def selectedTimeZone = user ? user.timeZone : "Eastern"
	def selectedLocalDeliveryTime = user ? user.localDeliveryTime : "8:00 AM"
%>
<html>
	<head>
		<title>Wakeup E-mail Signup - Step 1</title>
	</head>
	<body>
	<table border="1">
		<tr>
			<td valign="top" align="center">
				<!--  BEGIN NEW USER AREA -->
				<g:form controller="login" action="register">
				
				<table>
					<tr>
						<td colspan="2">
						<h1>New User Sign-up</h1>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<g:hasErrors bean="${user}">
								<g:renderErrors bean="${user}" as="list" />
							</g:hasErrors>
						</td>
					</tr>
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
						<td>Password:</td>
						<td>
							<input id="password" type="password" name="password" value="${user?.password}" title="password" spellcheck="false"/>
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
							<!-- <input id="deliveryTime" type="text" name="deliveryTime" value="${selectedLocalDeliveryTime}" title="deliveryTime" spellcheck="false"/> -->
							<g:select name="deliveryTime" from="${deliveryTimes}" value="${selectedLocalDeliveryTime}"/> 
							<g:select name="timeZone" from="${timeZones}" value="${selectedTimeZone}"/> 
						</td>
					</tr>
					
					<tr>
						<td colspan="2" align="center">
							<g:submitButton name="register" class="input-button" value="Sign Up"/>
						</td>
					</tr>
				</table>
				</g:form>
				<!--  END NEW USER AREA -->
			</td>
			
			<td valign="top" align="top">
				<g:form controller="login" action="login">
				<table>
					<tr>
						<td colspan="2"><h1>Existing User Login</h1></td>
				
					</tr>
					
					<tr>
						<td>E-mail:</td>
						<td>
							<input id="email2" type="text" name="email2" value="" title="email2" spellcheck="false"/>
						</td>	
					</tr>
					
					<tr>
						<td>Password:</td>
						<td>
							<input id="password2" type="password" name="password2" value="" title="password2" spellcheck="false"/>
						</td>	
					</tr>
					<tr>
						<td colspan="2" align="center">
							<g:submitButton name="login" class="input-button" value="Login"/>
						</td>
					</tr>
				</table>
				</g:form>
			</td>
		</tr>
	</table>
	

	</body>
</html>