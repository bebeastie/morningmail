import com.morningmail.domain.Interest

class BootStrap {

    def init = { servletContext ->
		if (Interest.count() == 0) {
			Interest topNews = new Interest()
			topNews.type = Interest.TYPE_TOP_NEWS
			topNews.displayName = "Top News"
			topNews.save()

			Interest weather = new Interest()
			weather.type = Interest.TYPE_WEATHER
			weather.displayName = "Weather"
			weather.save()
			
			Interest googleCal = new Interest()
			googleCal.type = Interest.TYPE_GOOGLE_CAL
			googleCal.displayName = "Google Calendar"
			googleCal.save()
		}
    }
    def destroy = {
    }
}
