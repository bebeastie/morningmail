<%@ page import="com.morningmail.domain.User" %>
<%@ page import="com.morningmail.utils.DateUtils" %>
<%
	def timeZones = DateUtils.TIME_ZONES
	def deliveryTimes = DateUtils.DELIVERY_TIMES

	def selectedTimeZone = newsletter ? newsletter.timeZone : "Eastern"
	def selectedLocalDeliveryTime = newsletter ? newsletter.localDeliveryTime : "8:00 AM"
%>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="/css/main.css"/>
		<title>Wakeup E-mail Signup - Step 1</title>
	</head>
	<body>
	<table>
		<tr>
			<td valign="top" align="center">
				<!--  BEGIN NEW USER AREA -->
				<g:form controller="user" action="register">
				<g:hiddenField name="save" value="${true}" />
				
				<table>
					<tr>
						<td colspan="2">
						<h1>New User Sign-up</h1>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<g:hasErrors>
								<div class="errors">
									<g:renderErrors bean="${user}" as="list" />
									<g:renderErrors bean="${newsletter}" as="list" />
								</div>
							</g:hasErrors>
						</td>
					</tr>
					<tr>
						<td>Invite code:</td>
						<td>
							<input id="inviteCode" type="text" name="inviteCode" value="${inviteCode}" title="inviteCode" spellcheck="false"/>
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
							<input id="email" type="text" name="regEmail" value="${user?.email}" title="regEmail" spellcheck="false"/>
						</td>
					</tr>
					<tr>
						<td>Password:</td>
						<td>
							<input id="password" type="password" name="regPassword" value="${user?.password}" title="regPassword" spellcheck="false"/>
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
							<input id="deliveryTime" type="text" name="deliveryTime" value="${selectedLocalDeliveryTime}" title="deliveryTime" spellcheck="false"/> 
							<!--  <g:select name="deliveryTime" from="${deliveryTimes}" value="${selectedLocalDeliveryTime}"/> -->
							<g:select name="timeZone" from="${timeZones}" value="${selectedTimeZone}"/> 
						</td>
					</tr>
					<tr>
						<td>Newsletter Name:</td>
						<td>
							<input id="newsletterName" type="text" name="newsletterName" value="" title="newsletterName" spellcheck="false"/>
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
				<g:form controller="user" action="login">
				<input type="hidden" name="jump" value="${jump}"/>
				<input type="hidden" name="display" value=""/>
				
				<table>
					<tr>
						<td colspan="2"><h1>Existing User Login</h1></td>
				
					</tr>
					
					<tr>
						<td>E-mail:</td>
						<td>
							<input id="email2" type="text" name="email" value="" title="email" spellcheck="false"/>
						</td>	
					</tr>
					
					<tr>
						<td>Password:</td>
						<td>
							<input id="password2" type="password" name="password" value="" title="password" spellcheck="false"/>
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