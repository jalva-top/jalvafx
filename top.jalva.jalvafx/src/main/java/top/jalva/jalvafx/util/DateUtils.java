package top.jalva.jalvafx.util;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

public class DateUtils {

	/** Result examples: [Вчера], [Сегодня], [Завтра], [12.05.2000] */
	public static String asShortString(LocalDate date) {

		String dayName = "";

		if (date != null) {
			if (date.equals(LocalDate.now()))
				dayName = "Сегодня".intern();
			else if (date.equals(LocalDate.now().minusDays(1)))
				dayName = "Вчера".intern();
			else if (date.equals(LocalDate.now().plusDays(1)))
				dayName = "Завтра".intern();
			else {
				dayName = ApplicationFormatter.formatDate(date);
			}
		}

		return dayName;
	}

	/** Result examples: [Вчера], [Сегодня], [Завтра], [12.05.2000] */
	public static String asShortString(java.sql.Date date) {
		if (date == null)
			return "";
		else
			return asShortString(date.toLocalDate());
	}

	/**
	 * Result examples: [Вчера], [Сегодня], [Завтра], [13.06.2018 (СРЕДА)] or
	 * [13.06.2018 (Ср)] when fullWeekDayName=false
	 */
	public static String asLongString(LocalDate date, Boolean fullWeekDayName) {

		String dayName = asShortString(date);

		String weekDayName;
		if (fullWeekDayName)
			weekDayName = getWeekdayName(date);
		else
			weekDayName = getWeekdayShortName(date);

		Boolean showWeekDayName = false;

		if (!date.equals(LocalDate.now()) && !date.equals(LocalDate.now().minusDays(1))
				&& !date.equals(LocalDate.now().plusDays(1)))
			showWeekDayName = true;

		return dayName + (showWeekDayName ? " (" + weekDayName + ")" : "");
	}

	/** Result examples: [Вчера], [Сегодня], [Завтра], [13.06.2018 (Ср)] */
	public static String asLongString(LocalDate date) {
		return asLongString(date, false);
	}

	/**
	 * Result examples: [Вчера 09:00:00], [Сегодня 09:00:00], [Завтра 09:00:00],
	 * [13.06.2018 09:00:00]
	 */
	public static String AsShortString(Timestamp timestamp) {
		if (timestamp == null)
			return "";
		else
			return AsShortString(timestamp.toLocalDateTime());
	}

	/**
	 * Result examples: [Вчера 09:00:00], [Сегодня 09:00:00], [Завтра 09:00:00],
	 * [13.06.2018 09:00:00]
	 */
	public static String AsShortString(LocalDateTime dateTime) {

		LocalDate date = dateTime.toLocalDate();
		String dayName = asShortString(date);

		return dayName + " " + ApplicationFormatter.formatTime(dateTime.toLocalTime());
	}

	/** Result example: [СРЕДА 13.06.2018] */
	public static String asFullString(LocalDate date) {
		return getWeekdayName(date) + " " + ApplicationFormatter.formatDate(date);
	}

	/** Result example: [СРЕДА 13.06.2018 09:00:00] */
	public static String asFullString(LocalDateTime dateTime) {
		return getWeekdayName(dateTime.toLocalDate()) + " " + ApplicationFormatter.formatDateTime(dateTime);
	}

	/**
	 * Result examples: [Вчера 09:00:00], [Сегодня 09:00:00], [Завтра 09:00:00],
	 * [13.06.2018 (СРЕДА) 09:00:00] or [13.06.2018 (Ср) 09:00:00] when
	 * fullWeekDayName=false
	 */
	public static String asLongString(LocalDateTime dateTime, Boolean fullWeekDayName) {
		return asLongString(dateTime.toLocalDate(), fullWeekDayName) + " "
				+ ApplicationFormatter.formatTime(dateTime.toLocalTime());
	}

	/**
	 * Result examples: [Вчера 09:00:00], [Сегодня 09:00:00], [Завтра 09:00:00],
	 * [13.06.2018 (Ср) 09:00:00]
	 */
	public static String asLongString(LocalDateTime dateTime) {
		return asLongString(dateTime, false);
	}

	/** Result examples: ПОНЕДЕЛЬНИК, ВТОРНИК */
	public static String getWeekdayName(LocalDate date) {

		String weekDayName;
		List<String> weekdays = ResourceBundleList.get("weekday.russian");

		weekDayName = weekdays.get(date.getDayOfWeek().getValue() - 1).toUpperCase().trim();

		return weekDayName;
	}

	/** Result examples: Пн, Вт */
	public static String getWeekdayShortName(LocalDate date) {

		String weekDayName;
		List<String> weekdays = ResourceBundleList.get("weekday.russian.short");

		weekDayName = weekdays.get(date.getDayOfWeek().getValue() - 1).trim();

		return weekDayName;
	}

	public static LocalDateTime toLocalDateTime(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime());
		return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
	}

	public static String getMonthName(LocalDate date) {
		String[] montName = { "январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь",
				"октябрь", "ноябрь", "декабрь" };

		return montName[date.getMonthValue() - 1];
	}

	public static LocalDate getNextBusinessDay() {
		long dateShift = 1;
		if (LocalDate.now().getDayOfWeek().equals(DayOfWeek.FRIDAY))
			dateShift = 3;
		else if (LocalDate.now().getDayOfWeek().equals(DayOfWeek.SATURDAY))
			dateShift = 2;

		return LocalDate.now().plusDays(dateShift);
	}

	public static LocalDate getMonthBegin(LocalDate date) {
		return LocalDate.of(date.getYear(), date.getMonth(), 1);
	}

	public static LocalDate getMonthEnd(LocalDate date) {
		return LocalDate.of(date.getYear(), date.getMonth(), 1).plusMonths(1).minusDays(1);
	}

	public static LocalDateTime getMonthBeginDateTime(LocalDate date) {
		return getDayBeginDateTime(getMonthBegin(date));
	}

	public static LocalDateTime getMonthEndDateTime(LocalDate date) {
		return getDayEndDateTime(getMonthEnd(date));
	}

	public static LocalDateTime getDayBeginDateTime(LocalDate date) {
		return LocalDateTime.of(date, LocalTime.of(0, 0));
	}

	public static LocalDateTime getDayEndDateTime(LocalDate date) {
		return LocalDateTime.of(date, LocalTime.of(23, 59));
	}

	public static LocalDateTime getApplicationEraBeginDateTime() {
		return getDayBeginDateTime(getApplicationEraBeginDate());
	}

	public static LocalDate getApplicationEraBeginDate() {
		return LocalDate.of(2018, 10, 01);
	}

	public static Boolean equalsMonthAndYear(LocalDateTime monthDateTime1, LocalDateTime monthDateTime2) {
		return monthDateTime1 != null && monthDateTime2 != null
				&& monthDateTime1.getMonth().equals(monthDateTime2.getMonth())
				&& (monthDateTime1.getYear() == monthDateTime2.getYear());
	}
}
