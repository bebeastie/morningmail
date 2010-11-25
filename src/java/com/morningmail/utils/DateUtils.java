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
	private static final String BASE_DATE = new String("01/01/1970");
	
	public static final List<String> TIME_ZONES = new ArrayList<String>();
	
	static {
		TIME_ZONES.add("Eastern");
		TIME_ZONES.add("Central");
		TIME_ZONES.add("Mountain");
		TIME_ZONES.add("Pacific");
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

		Date date = dFormat.parse(BASE_DATE + " " + time + " " + timeZone);
		dFormat.setTimeZone(TimeZone.getTimeZone("EST"));

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
	
	public static void main(String[] args) {
		DateFormat dFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a z");
		String BASE_DATE1 = new String("01/01/1970");
		
		String time1 = new String("7:00 AM +0800");
		
		try {
			Date date1 = dFormat.parse(BASE_DATE1 + " " + time1);
			
			dFormat.setTimeZone(TimeZone.getTimeZone("EST"));
			System.out.println("date1, time:" + date1.getTime());
			System.out.println("date1, time:" + date1.toString());
			System.out.println("date1, format:" + dFormat.format(date1).toString());
			
			Calendar cal2 = Calendar.getInstance();
			cal2.setTimeZone(TimeZone.getTimeZone("GMT"));
			cal2.setTime(date1);	
			
			//now make sure we didnt roll any other fields
			cal2.set(Calendar.DAY_OF_MONTH, 1);
			cal2.set(Calendar.MONTH, 0);
			cal2.set(Calendar.YEAR, 1970);
			
			Date date2 = cal2.getTime();
			System.out.println("date2, time:" + date2.getTime());
			System.out.println("date2, time:" + date2.toString());
			System.out.println("date2, format:" + dFormat.format(date2).toString());
			
			
		} catch (ParseException e) {
			System.out.println("problems with date1");
			e.printStackTrace();
		}
		
		
	}
	
	
	
}
