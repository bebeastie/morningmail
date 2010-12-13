<%@ page import="com.morningmail.domain.User" %>
<%@ page import="com.morningmail.domain.Interest" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%
Interest weather = Interest.findByType(Interest.TYPE_WEATHER)
Interest topNews = Interest.findByType(Interest.TYPE_TOP_NEWS)
Interest googleCal = Interest.findByType(Interest.TYPE_GOOGLE_CAL)
Interest wotd = Interest.findByType(Interest.TYPE_WOTD)
Interest techcrunch = Interest.findByType(Interest.TYPE_TECHCRUNCH)
 %>
<html>
	<head>
		<title>Wakeup E-mail Signup - Step 2</title>
		<g:javascript library="jquery" plugin="jquery"/>
		<script type="text/javascript" src="/js/jquery/jquery.asmselect.js"></script>
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.7/jquery-ui.min.js"></script>
		<link rel="stylesheet" type="text/css" href="/css/jquery.asmselect.css"/>
		
		<script type="text/javascript">
		$(document).ready(function() {
		    $("select[multiple]").asmSelect({sortable:true, animate:true, highlight:true, addItemTarget: 'bottom'});
		}); 
		</script>
	</head>
	<body>
		<h1>Hello ${user?.email}!</h1>
		<br>
		<g:form name="interests" action="personalize">
		<g:hiddenField name="saveInterests" value="${true}" />
		<label for="interests">What do you want in your email?</label> 
		<select id="interests" multiple="multiple" name="interests[]" title="Click to Select an Interest"> 
			<g:each var="interestKey" in="${user.interests}">
				<option value="${KeyFactory.keyToString(interestKey)}" selected="selected">${Interest.get(interestKey).displayName}</option>			
			</g:each>
			
			<g:each var="interest" in="${Interest.list(sort:'displayName', order:'asc')}">
				<g:if test="${!user.interests.contains(interest.id)}">
					<option value="${KeyFactory.keyToString(interest.id)}"
						<g:if test="${user.interests.contains(interest.id)}"> 
							selected="selected"
						</g:if>	
					>${interest.displayName}</option>
				</g:if>
			</g:each>
		</select>
		<!--
		//sort:"title", order:"asc"
		
                        <option value="${weather.id}" <g:if test="${user.interests.contains(weather.id)}"> selected="selected"</g:if>>${weather.displayName}</option>      
                        <option selected="selected">${topNews.displayName}</option> 
                        <option>${wotd.displayName}</option> 
                        <option>${techcrunch.displayName}</option> 
                        <option>${googleCal.displayName}</option> 
		-->
		<!--   <g:select name="user.company.id"
          from="${Interest.list()}"
          value="${user?.interests}"
          optionKey="id"
           />
        
		
		<table>
		<tr>
			<td>${weather.displayName}</td>
			<td><g:checkBox name="${weather.type}" value="${user.interests.contains(weather.id)}" /></td>
		</tr>
		<tr>
			<td>${topNews.displayName}</td>
			<td><g:checkBox name="${topNews.type}" value="${user.interests.contains(topNews.id)}" /></td>
		</tr>
		<tr>
			<td>${wotd.displayName}</td>
			<td><g:checkBox name="${wotd.type}" value="${user.interests.contains(wotd.id)}" /></td>
		</tr>
		<tr>
			<td>${googleCal.displayName}</td>
			<td><g:checkBox name="${googleCal.type}" value="${user.interests.contains(googleCal.id)}" /></td>
		</tr>
		<tr>
			<td>${techcrunch.displayName}</td>
			<td><g:checkBox name="${techcrunch.type}" value="${user.interests.contains(techcrunch.id)}" /></td>
		</tr>
		<tr>
		<td colspan="2" align="center"><g:actionSubmit value="Save" action="personalize" /></td>
		</tr>
		
		</table>
		-->
		<g:actionSubmit value="Save" action="personalize" />
		</g:form>
		
	</body>
</html>