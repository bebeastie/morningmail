package com.morningmail.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
	private static final Integer BASE_MONTH = 0;
	private static final Integer BASE_DAY = 1;
	private static final Integer BASE_YEAR = 1970;
	private static final String BASE_DATE_STRING = new String("01/01/1970");
	public static final Date BASE_DATE = new Date(0);
	
	public static final List<String> TIME_ZONES = new ArrayList<String>();
	public static final List<String> DELIVERY_TIMES = new ArrayList<String>();
	
	static {
		TIME_ZONES.add("Eastern");
		TIME_ZONES.add("Central");
		TIME_ZONES.add("Mountain");
		TIME_ZONES.add("Pacific");
	} 

	
	static {
		DELIVERY_TIMES.add("5:30 AM");
		DELIVERY_TIMES.add("6:00 AM");
		DELIVERY_TIMES.add("6:30 AM");
		DELIVERY_TIMES.add("7:00 AM");
		DELIVERY_TIMES.add("7:30 AM");
		DELIVERY_TIMES.add("8:00 AM");
		DELIVERY_TIMES.add("8:30 AM");
		DELIVERY_TIMES.add("9:00 AM");
		DELIVERY_TIMES.add("9:30 AM");
		DELIVERY_TIMES.add("10:00 AM");
		DELIVERY_TIMES.add("10:30 AM");
	}
	
	/**
	 * Accepts a time and time zone (e.g. -0400) and returns a normalized date.
	 * 
	 * @param time
	 * @param timeZone
	 * @return
	 */
	public static Date getNormalizedDeliveryTime(String time, String timeZone) throws ParseException {
		DateFormat dFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a z");

		Date date = dFormat.parse(BASE_DATE_STRING + " " + time + " " + timeZone);

		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		cal.setTime(date);	
		
		//now make sure we didnt roll any other fields
		cal.set(Calendar.DAY_OF_MONTH, BASE_DAY);
		cal.set(Calendar.MONTH, BASE_MONTH);
		cal.set(Calendar.YEAR, BASE_YEAR);
		
		
		return cal.getTime();
	}
	
	public static String getOffsetTimeZone(String timeZone) {
		String offsetTimeZone = new String();
		
		if ("Eastern".equals(timeZone))
			offsetTimeZone = "-0500";
		if ("Central".equals(timeZone))
			offsetTimeZone = "-0600";
		if ("Mountain".equals(timeZone))
			offsetTimeZone = "-0700";
		if ("Pacific".equals(timeZone))
			offsetTimeZone = "-0800";
		
		return offsetTimeZone;
	}
	
	public static Date getNormalizedTime(Date now, Long offset) {
		//calculate the offset
		Date offsetDate = new Date(now.getTime() + offset);
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		cal.setTime(offsetDate);
		
		//now make sure to reset other fields
		cal.set(Calendar.DAY_OF_MONTH, BASE_DAY);
		cal.set(Calendar.MONTH, BASE_MONTH);
		cal.set(Calendar.YEAR, BASE_YEAR);	
		
		return cal.getTime();
	}
	
	public static boolean isWithin24Hours(Date date) {
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		
		return (now.getTime() - date.getTime()) > 86400000; 
	}
	
}
