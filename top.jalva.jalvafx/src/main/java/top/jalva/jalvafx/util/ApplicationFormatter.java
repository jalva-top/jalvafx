package top.jalva.jalvafx.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ApplicationFormatter {

	static String datePattern_without_year = "dd.MM";
	static String datePattern = "dd.MM.yyyy";
	static String timePattern = "HH:mm";
	static String timePattern_sec = "HH:mm:ss";

	static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
	static DateTimeFormatter dateFormatter_without_year = DateTimeFormatter.ofPattern(datePattern_without_year);
	static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(timePattern);
	static DateTimeFormatter timeFormatter_sec = DateTimeFormatter.ofPattern(timePattern_sec);
	static NumberFormat decimalFormatter = new DecimalFormat("#0.00");

	/** @return <b>dd MMMM yyyy</b> if showYear-true */
	public static String formatDateMonthAsString(LocalDate date, Boolean showYear) {
		String datePattern_with_year = "dd MMMM yyyy";
		String datePattern_without_year = "dd MMMM";

		if (showYear)
			return DateTimeFormatter.ofPattern(datePattern_with_year).format(date);
		else
			return DateTimeFormatter.ofPattern(datePattern_without_year).format(date);
	}

	public static String formatDate(LocalDate date) {
		return dateFormatter.format(date);
	}

	public static String formatDate_withoutYear(LocalDate date) {
		return dateFormatter_without_year.format(date);
	}

	public static String formatPrice(Double price) {
		if (price == null)
			price = 0.0;
		return decimalFormatter.format(price).replaceFirst(",", ".");
	}

	public static String format(BigDecimal value) {
		return format(value, true);
	}

	public static String format(BigDecimal value, boolean showZeroFraction) {
		String pattern = "#,##0";
		String customPattern;

		if (!showZeroFraction && value.doubleValue() == value.longValue())
			customPattern = pattern;
		else {
			if (value.scale() > 0) {
				customPattern = pattern + ".";
				for (int i = 0; i < value.scale(); i++)
					customPattern += "0";

			} else
				customPattern = pattern;
		}

		DecimalFormat formatter = new DecimalFormat(customPattern);

		return formatter.format(value).replace(",", ".");
	}

	public static String formatDateTime(LocalDateTime dateTime) {
		return formatDate(dateTime.toLocalDate()) + " " + formatTime(dateTime.toLocalTime());
	}

	public static String formatTime(LocalTime time) {
		return timeFormatter.format(time);
	}

	public static String formatTime_sec(LocalTime time) {
		return timeFormatter_sec.format(time);
	}

	/**
	 * @param phoneNumber
	 *            should contain 10 digit
	 */
	public static String formatPhoneNumber(String phoneNumber) {
		phoneNumber = StringUtils.parsePhoneNumber(phoneNumber);

		if (phoneNumber.matches("[0-9]{10}"))
			return "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + "-"
					+ phoneNumber.substring(6, 8) + "-" + phoneNumber.substring(8, 10);
		else
			return "<error>";
	}

	public static String format(Date date) {
		if (date == null)
			return "";
		else
			return new SimpleDateFormat("dd.MM.yyyy").format(date);
	}

	public static String format(Timestamp timestamp) {
		if (timestamp == null)
			return "";
		else
			return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(timestamp);
	}

}
