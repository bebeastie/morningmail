<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<g:javascript library="jquery" plugin="jquery"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<style type="text/css">
body {
	background-image: url('/images/high-res-grass.jpg'); 
	font-family: Verdana;
	font-size: 12px;
	color: #333333; 
	margin: 0;
	padding: 0;
}

#header {
	background-color:white;
	height:70px;
	border-bottom: solid;
	border-color: grey;
	border-width: 1px;
	opacity: .95;
	padding-top:5px;
	padding-bottom:5px;
	min-width:800px;
}

#getyourown {
	position: absolute;
	right: 0;
	top: 0;
	text-align:right;
	float:right;
	padding-right:8px;
}

#main-header {
	height:47px;
	border-bottom: solid;
	border-color: grey;
	border-width: 1px;
	opacity: .95;
	padding-top:5px;
	padding-bottom:5px;
	width:100%;
	background-color:white;
}
  
#logo{
  	margin-top:3px;
	float:left;
}

#title{
	font-size: 18pt; 
	font-weight: bold;
	text-align:center;
	vertical-align: middle;
	height:70px;
	min-height: 10em;
    display: table-cell;
    width:500px;
}
  
#date {
  	float:right;
  	text-align:right;
}


a:link {
	color: #186200; 
	text-decoration: underline; 
}

a:active {
	color: #0000ff; 
	text-decoration: underline; 
}

a:visited {
	color: #1E7D00; 
	text-decoration: underline;
}

a:hover {
	color: #4CB02C; 
	text-decoration: none; 
}

div#sample{
	background-color:#FFFFFF;
}

div#profile {
	background-color:#FFFFFF;
}

td#profile {
	border-style:solid;
	border-width:1px;
	background-color:#FFFFFF;
	padding:5px;
	min-width:300px;
	max-width:300px;
	opacity:.93;
	-moz-border-radius: 8px;
	border-radius: 8px;
}

td#sample {
	border-style:solid;
	border-width:1px;
	background-color:#FFFFFF;
	padding-top:15px;
	padding-left:20px;
	padding-right:20px;
	padding-bottom:20px;
	max-width:450px;
	min-width:400px;
	opacity:.93;
	-moz-border-radius: 8px;
	border-radius: 8px;
}

.bigheadline {
	font-size: 18pt; 
	font-weight: bold;
	color: #0F4000;
}
.headline {
	font-size: 10pt; 
	font-weight: bold;
}

.headline2 {
	font-size: 14pt; 
	font-weight: bold;
}

.bigbutton {
	cursor:pointer; 
	border-radius: 4px; 
	-moz-border-radius: 4px; 
	font-size:17pt; 
	font-weight:bold; 
	background-color:#329D27; 
	color:#FFFFFF; 
	font-family:arial;
	border-style:solid;
	border-width:1px;
	padding:10px;
	width:100%;
}

.email {
	font-family:arial; 
	border:1px solid #333333; 
	font-size:17pt;
	width:100%;
	height:1.5em;
	background-color:#FFF;
	color:#000; 
}

.error {
	color:red;
	font-size:12pt;
}


</style>

<script>
/*
function doClear(theText) {
	    if (theText.value == theText.defaultValue) {
	        theText.value = ""
	    }
	}
*/
</script>
</head>
<body>
	<div id="main-header">
  		<div id="logo"><img src="http://localhost:8080/images/logo2.png"/></div>
  		<div id="title">Lean Startups Edition</div>
  	</div>
  	<div id="getyourown"><a href="#">Want to personalize your own newsletter?</a></div>  


<!-- 
<div id="header">
	<img src="/images/logo2.png">
	
	<div id="getyourown">Want to personalize your own newsletter? <a href="#">Request an invite</a></div>
</div>
 -->

<center>
<table cellspacing="15px">
	<tr>
		<td rowspan="2" id="sample" align="center" valign="center">
			<img src="/images/lean_startups_sample.png"/>

		</td>
		<td id="profile">
		<table>
			<tr>
				<td colspan="3">
				<div class="headline2">Curated by:</div>
				</td>
			</tr>
			<tr>
				<td valign="top" align="center"><img src="/images/editor_andres.jpg" /></td>
				<td valign="top" rowspan="2">
				<div class="headline">Andres Glusman</div>
				Howdy, I'm one of the organizers of the NYC Lean Startup Meetup and an avid enthusiast of lean startup and customer development.</td>
			</tr>
			<tr>
				<td>
				<table cellspacing="2">
					<tr>
						<td><img src="/images/twitter-16x16.png"></td>
						<td><img src="/images/facebook-16x16.png"></td>
						<td><img src="/images/linkedin-16x16.png"></td>
						<td><img src="/images/foursquare-16x16.png"></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td valign="top" align="center"><img src="/images/editor_blake.jpg" /></td>
				<td valign="top" rowspan="2">
				<div class="headline">Blake Barnes</div>
				Hi! I run the product team at YellowJacket and built MorningMail. I'll make sure you're always up to speed on lean startup best practices.</td>
			</tr>
			<tr>
				<td>
				<table cellspacing="2">
					<tr>
						<td><a href="http://www.twitter.com/blakebarnes"><img src="/images/twitter-16x16.png"></a></td>
						<td><img src="/images/facebook-16x16.png"></td>
						<td><a href="http://www.linkedin.com/in/blaketech"><img src="/images/linkedin-16x16.png"></a></td>
						<td><a href="http://foursquare.com/user/29811"><img src="/images/foursquare-16x16.png"></a></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				
				<td colspan="3"><br/><strong>Enter your email address to subscribe:</strong>
				<g:form controller="newsletter" action="subscribe">
				<g:hiddenField name="save" value="${true}"/>
				<g:hiddenField name="newsletterId" value="${newsletter.id}"></g:hiddenField>
				
				<input class="email" name="email" value="${flash.email}" spellcheck="false"/>
				<input type="submit" class="bigbutton" value="Subscribe"/> 
				<div class="error">${flash.message}</div>
				</td>
				</g:form>
				
			</tr>
		</table>

		</td>
	</tr>
</table>
</center>
</body>
</html>