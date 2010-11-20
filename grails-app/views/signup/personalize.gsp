<%@ page import="com.morningmail.domain.User" %>
<html>
	<head>
		<title>Wakeup E-mail Signup - Step 2</title>
	</head>
	<body>
		<h1>Hello ${user?.email }!</h1>
		
		<br>
		
		<a href="${authUrl}">Authorize Google Calendar Please!</a>
		
		
		<h1>GResults: ${calendars} </h1>
	</body>
</html>