<html>
    <head>
        <title><g:layoutTitle default="MorningMail"/></title>
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'main.css')}" />
        <g:layoutHead />
        <script type="text/javascript">
		  var _gaq = _gaq || [];
		  _gaq.push(['_setAccount', 'UA-21870730-1']);
		  _gaq.push(['_trackPageview']);
		
		  (function() {
		    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
		    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
		    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
		  })();
		</script>
    </head>
    <body>
 		<div id="logo"><a href="/"><img src="${resource(dir:'images',file:'logo2.png')}"></a></div>
        <g:layoutBody />
    </body>
</html>