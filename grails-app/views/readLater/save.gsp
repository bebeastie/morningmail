<%@ page import="com.morningmail.domain.ReadLaterItem"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MorningMail - Read Later Saved!</title>
<script type="text/javascript">
	var count=3;
	var counter=setInterval("timer()",1000); //1000 will  run it every 1 second

	function timer() {
		count=count-1;
	  	if (count <= 0) {
			window.close();
	     	return;
	  	}
	 	document.getElementById("timer").innerHTML= "This window will close in " + count + " secs";
	}
</script>

<style>
	body {
		font-family: Verdana;
		background-color:white;
	}
	
	.title {
		font-family: Verdana;
		font-size: 12px;	
		font-weight: bold;
	}
	.item {
		font-family: Verdana;
		font-size: 12px;	
	}
	
	.subtle {
		font-family: Verdana;
		font-size: 10px;
		color: #7A7A7A;
	}
</style>
</head>
<body>
	<div id="title" class="title">MorningMail - Saved</div>
	<div class="item">${readLaterItem?.title}</div>
	<br/>
	<div class="subtle" id="timer">This window will close in 5 secs</div>
</body>
</html>