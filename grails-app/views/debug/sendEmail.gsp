<html>
<head>
	<meta name="layout" content="main"></meta>
	<title>Send Email Debug</title>
	
	<style type="text/css">
		body {
			margin-left: 10px;
		}
	</style>
</head>

<body>
	<br/>
	<g:form controller="debug" action="sendEmail">

	<div>From:</div>
	<div><input id="from" size="75" type="text" name="from" value="blake.barnes@gmail.com" title="from" spellcheck="false"/></div>
	<br/>
	
	
	<div>To:</div>
	<div><input id="to" size="75" type="text" name="to" value="" title="to" spellcheck="false"/></div>
	<br/>
	
	<div>Subject:</div>	
	<div><input id="subject" size="75" type="text" name="subject" value="" title="subject" spellcheck="false"/></div>
	<br/>
	
	<div>Plain text:</div>
	<div><textarea id="plainText" name="plainText" cols="75" rows="15"></textarea></div>		
	<br/>
	
	<div>HTML:</div>
	<div><textarea id="html" name="html" cols="75" rows="15"></textarea></div>
	<br/>
	
	<div>
	<g:submitButton name="submit" class="input-button" value="Send"/>
	</div>
	</g:form>
	
	<div>
		${returnValue}
	</div>
</body>
</html>