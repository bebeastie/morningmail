package com.morningmail.services

import com.morningmail.domain.User;
import com.morningmail.domain.PersonalFeed
import com.morningmail.services.PersonalFeedService
import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.net.URL;
import com.google.appengine.api.datastore.Text

class GoogleWeatherService implements PersonalFeedService{
	private static final BASE_URL = "http://www.google.com"
	private static final WEATHER_URL = "/ig/api?weather="
	
	public void fetch(User u) {
		URL url = new URL(BASE_URL + WEATHER_URL + u.zipCode)

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(url.openStream());

		String city = doc.getElementsByTagName("forecast_information").item(0)
			.getElementsByTagName("city").item(0).getAttribute("data");

		StringBuffer html = new StringBuffer()
		StringBuffer text = new StringBuffer()
			
		html.append("<div>")
		html.append("<b>").append("WEATHER - ").append(city.toUpperCase()).append("</b><br/>")
			
		text.append("WEATHER - ").append(city.toUpperCase()).append("\n")
		
		
		//current conditions
		StringBuffer current = new StringBuffer()
		Element currentConditions = doc.getElementsByTagName("current_conditions").item(0)
		current.append("Current conditions: ")
		current.append(currentConditions.getElementsByTagName("condition").item(0).getAttribute("data")).append(", ")
		current.append(currentConditions.getElementsByTagName("temp_f").item(0).getAttribute("data")).append("F, ")
		current.append(currentConditions.getElementsByTagName("wind_condition").item(0).getAttribute("data")).append(" ")
		
		html.append(current).append("<br/>")
		text.append(current).append("\n")
		
		//forecast
		StringBuffer forecast = new StringBuffer()
		Element forecastConditions = doc.getElementsByTagName("forecast_conditions").item(0)
		forecast.append("Today's forecast: ")
		forecast.append(forecastConditions.getElementsByTagName("condition").item(0).getAttribute("data")).append(", ")
		forecast.append("high of ").append(forecastConditions.getElementsByTagName("high").item(0).getAttribute("data")).append("F, ")
		forecast.append("low of ").append(forecastConditions.getElementsByTagName("low").item(0).getAttribute("data")).append("F")
		
		html.append(forecast).append("<br/></div>")
		text.append(forecast).append("\n")
		
		//now save the feed
		PersonalFeed feed = PersonalFeed.findByTypeAndUser(PersonalFeed.TYPE_WEATHER, u);
		
		if (!feed) {
			feed = new PersonalFeed()
			feed.type = PersonalFeed.TYPE_WEATHER
			feed.user = u
			u.pFeeds.add(feed)
			feed.save()
		}
		
		feed.html = new Text(html.toString())
		feed.plainText = new Text(text.toString().trim())
		feed.lastUpdated = new Date()
			
	} 
	
	public String getHtml(User u) {
		try {
			PersonalFeed feed = PersonalFeed.findByTypeAndUser(PersonalFeed.TYPE_WEATHER, u)
			return feed.html.getValue()
		}catch (Exception e) {
			   log.error("Couldn't find $PersonalFeed.TYPE_WEATHER feed for $u")
			return ""
		}
	}
	
	public String getPlainText(User u) {
		try {
			PersonalFeed feed = PersonalFeed.findByTypeAndUser(PersonalFeed.TYPE_WEATHER, u)
			return feed.plainText.getValue()
		}catch (Exception e) {
			   log.error("Couldn't find $PersonalFeed.TYPE_WEATHER feed for $u")
			return ""
		}
	}
}


//SAMPLE RESPONSE FOR 10002
//<?xml version="1.0"?>
//<xml_api_reply version="1">
//	<weather module_id="0" tab_id="0" mobile_row="0" mobile_zipped="1" row="0" section="0" >
//		<forecast_information>
//			<city data="New York, NY" />
//			<postal_code data="10002" />
//			<latitude_e6 data="" />
//			<longitude_e6 data="" />
//			<forecast_date data="2010-11-22" />
//			<current_date_time data="2010-11-23 00:36:36 +0000" />
//			<unit_system data="US" />
//		</forecast_information>
//		<current_conditions>
//			<condition data="Partly Cloudy" />
//			<temp_f data="58" />
//			<temp_c data="14" />
//			<humidity data="Humidity: 66%" />
//			<icon data="/ig/images/weather/partly_cloudy.gif" />
//			<wind_condition data="Wind: SW at 8 mph" />
//			</current_conditions>
//		<forecast_conditions>
//			<day_of_week data="Mon" />
//			<low data="49" />
//			<high data="64" />
//			<icon data="/ig/images/weather/mostly_cloudy.gif" />
//			<condition data="Mostly Cloudy" />
//			</forecast_conditions>
//		<forecast_conditions>
//			<day_of_week data="Tue" />
//			<low data="38" />
//			<high data="62" />
//			<icon data="/ig/images/weather/chance_of_rain.gif" />
//			<condition data="Scattered Showers" />
//			</forecast_conditions>
//		<forecast_conditions>
//			<day_of_week data="Wed" />
//			<low data="34" />
//			<high data="49" />
//			<icon data="/ig/images/weather/sunny.gif" />
//			<condition data="Sunny" />
//			</forecast_conditions>
//		<forecast_conditions>
//			<day_of_week data="Thu" />
//			<low data="42" />
//			<high data="45" />
//			<icon data="/ig/images/weather/rain.gif" />
//			<condition data="Showers" />
//			</forecast_conditions>
//		</weather>
//	</xml_api_reply>

