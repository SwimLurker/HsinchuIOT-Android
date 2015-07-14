package org.slstudio.hsinchuiot.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReportUtil {
	private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdfDateTime = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static SimpleDateFormat sdfDate2 = new SimpleDateFormat(
			"yyyy/MM/dd");
	private static SimpleDateFormat sdfHour = new SimpleDateFormat("HH:00");
	private static SimpleDateFormat sdfHour2 = new SimpleDateFormat(
			"yyyy-MM-dd HH");

	public static Calendar[] get8HoursTimePeriod() {
		Calendar now = Calendar.getInstance();
		int hour = now.get(Calendar.HOUR_OF_DAY);

		String dateStr = sdfDate.format(now.getTime());

		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);
		String dateStr2 = sdfDate.format(yesterday.getTime());

		Calendar c0 = Calendar.getInstance();
		try {
			c0.setTime(sdfDateTime.parse(dateStr + " 00:00:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar c8 = Calendar.getInstance();
		try {
			c8.setTime(sdfDateTime.parse(dateStr + " 08:00:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar c16 = Calendar.getInstance();
		try {
			c16.setTime(sdfDateTime.parse(dateStr + " 16:00:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar c16_yesterday = Calendar.getInstance();
		try {
			c16_yesterday.setTime(sdfDateTime.parse(dateStr2 + " 16:00:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (now.before(c8)) {
			return new Calendar[] { c16_yesterday, c0 };
		} else if (now.after(c16)) {
			return new Calendar[] { c8, c16 };
		} else {
			return new Calendar[] { c0, c8 };
		}
	}

	public static Calendar[] get1HourTimePeriod() {
		Calendar currentHour = Calendar.getInstance();
		currentHour.set(Calendar.MINUTE, 0);
		currentHour.set(Calendar.SECOND, 0);

		Calendar lastHour = Calendar.getInstance();
		lastHour.add(Calendar.HOUR, -1);
		lastHour.set(Calendar.MINUTE, 0);
		lastHour.set(Calendar.SECOND, 0);

		return new Calendar[] { lastHour, currentHour };
	}

	public static String get8HoursTimePeriodString(final Calendar from, final Calendar to) {
		
		int fromHour = from.get(Calendar.HOUR_OF_DAY);
		int toHour = to.get(Calendar.HOUR_OF_DAY);

		String fromDate = sdfDate2.format(from.getTime());
		String toDate = sdfDate2.format(to.getTime());

		if (fromHour == 0) {
			return fromDate + " 00:00-08:00";
		} else if (fromHour == 8) {
			return fromDate + " 08:00-16:00";
		} else {
			return toDate + " 16:00-00:00";
		}

	}

	public static String get1HourTimePeriodString(final Calendar from, final Calendar to) {

		int fromHour = from.get(Calendar.HOUR_OF_DAY);
		int toHour = to.get(Calendar.HOUR_OF_DAY);

		String fromDate = sdfDate2.format(from.getTime());
		String toDate = sdfDate2.format(to.getTime());

		if (toHour == 0) {
			return fromDate + " 00:00-01:00";
		} else {
			return toDate + " " + sdfHour.format(from.getTime()) + "-"
					+ sdfHour.format(to.getTime());
		}
	}

	public static String getServerTimeString(final Calendar cal) {
		Calendar result = Calendar.getInstance();
		result.setTime(cal.getTime());
		result.add(Calendar.HOUR, -8);
		
		return sdfHour2.format(result.getTime()) + ":00:00";
	}
}
