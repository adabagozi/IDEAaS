package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTime {

	public static Date dateTimePickerToISO(String dateTimeFromPicker) {
		SimpleDateFormat outputDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		SimpleDateFormat inputDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(tz);

		try {
			String date_iso_string = outputDate.format(inputDate.parse(dateTimeFromPicker));
			Instant start_instant = Instant.parse(date_iso_string);
			Date start_date = Date.from(start_instant);
			return start_date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date endDateFormatter(String date_to_format) {
		java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		LocalDateTime date_time = LocalDateTime.parse(date_to_format, formatter);
		Instant instant = date_time.atZone(ZoneId.of("Z")).toInstant();
		Date end_date = Date.from(instant);
		return end_date;
	}

	public static Instant stringDateToInstant(String date_to_format) {
		java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		LocalDateTime date_time = LocalDateTime.parse(date_to_format, formatter);

		return date_time.atZone(ZoneId.of("Z")).toInstant();
	}

	public static LocalDateTime LocalDateTimeFormatter(String date_to_format) {
		java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		LocalDateTime date_time = LocalDateTime.parse(date_to_format, formatter);
		return date_time;
	}
	
	public static Date addMinuteToDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, 1);
		Date date_with_minute = cal.getTime();
		return date_with_minute;
	}

	public static Date sendAlertStartDate(String interval_type, int interval, Date end_local_date_time) {
		Date start_local_date_time = null;
		if(interval_type.equalsIgnoreCase("minutes")){
			Calendar cal = Calendar.getInstance();
			cal.setTime(end_local_date_time);
			cal.add(Calendar.MINUTE, -interval);
			start_local_date_time = cal.getTime();
			return start_local_date_time;
		}
		
		if(interval_type.equalsIgnoreCase("seconds")) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(end_local_date_time);
			cal.add(Calendar.SECOND, -interval);
			start_local_date_time = cal.getTime();
			return start_local_date_time;
		}

		return null;
	}

	public static Date addMinuteInterval(Date a) {
		Date b = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(a);
		cal.add(Calendar.MINUTE, 1);
		b = cal.getTime();
		return b;
	}
	
	public static Date addSecondInterval(Date a) {
		Date b = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(a);
		cal.add(Calendar.SECOND, 1);
		b = cal.getTime();
		return b;
	}

	public static Instant instantStartTime(String interval_type, long interval, LocalDateTime end_local_date_time) {
		LocalDateTime start_local_date_time = null;
		Instant instant = null;
		if(interval_type.equalsIgnoreCase("minutes")){
			start_local_date_time = end_local_date_time.minusMinutes(interval);
			instant = start_local_date_time.atZone(ZoneId.of("Z")).toInstant();
			return instant;
		}

		return null;

	}
}
