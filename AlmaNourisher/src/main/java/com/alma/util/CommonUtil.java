package com.alma.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

public class CommonUtil {
	public static String getAgeInDateAndMonthFormat(double age) {
		String year = String.format("%.0f", new Object[] { Double.valueOf(age) });

		BigDecimal bd = new BigDecimal((age - Math.floor(age)) * 100.0D);
		bd = bd.setScale(4, RoundingMode.HALF_DOWN);

		int month = bd.intValue();

		String ageInDateAndMonthFormat = year + "Y" + " " + month + "M";

		return ageInDateAndMonthFormat;
	}

	public static String convertFileToBase64(MultipartFile multipartFile) {
		String fileString = null;
		try {
			byte[] base = Base64.encodeBase64(multipartFile.getBytes());
			fileString = new String(base);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("==============================>" + fileString);
		return fileString;
	}

	public static String appendBase64StringInImageUrl(String imageUrl) {
		return "data:image/png;base64," + imageUrl;
	}

	public static String appendBase64StringInPdfUrl(String pdfUrl) {
		return "data:application/pdf;base64," + pdfUrl;
	}

	public static boolean isDateEqual(Date startDate, Date endDate) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(startDate);
		cal2.setTime(endDate);
		boolean sameDay = (cal1.get(1) == cal2.get(1)) && (cal1.get(6) == cal2.get(6));

		return sameDay;
	}

	public static Date convertEqualEndDateTo_xx_59_59(Date endDate) {
		Calendar calEnd = new GregorianCalendar();
		calEnd.setTime(endDate);
		calEnd.set(6, calEnd.get(6));
		calEnd.set(11, 23);
		calEnd.set(12, 59);
		calEnd.set(13, 59);
		calEnd.set(14, 0);
		Date midnightTonight = calEnd.getTime();

		return midnightTonight;
	}

	public static String dateToMonthAndYear(Date date) {
		String month_name = null;
		try {
			SimpleDateFormat month_date = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			String actualDate = date.toString();

			Date date_ = sdf.parse(actualDate);

			month_name = month_date.format(date_);
		} catch (ParseException pe) {
			pe.getMessage();
		} catch (Exception e) {
			e.getMessage();
		}

		return month_name;
	}

	public static String dateToNumericMonthAndYear(Date date) {
		String month_name = null;
		try {
			SimpleDateFormat month_date = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			String actualDate = date.toString();

			Date date_ = sdf.parse(actualDate);

			month_name = month_date.format(date_);
		} catch (ParseException pe) {
			pe.getMessage();
		} catch (Exception e) {
			e.getMessage();
		}

		return month_name;
	}

	public static String monthAndYearToDate(boolean isStartDate, String date) {
		String d = null;

		if (isStartDate)
			d = date + "-01";
		else {
			d = date + "-31";
		}

		return d;
	}

	public static boolean validateDoubleValue(double arg) {
		if (arg != 0.0D) {
			String regexDecimal = "^-?\\d*\\.\\d+$";
			String regexInteger = "^-?\\d+$";
			String regexDouble = regexDecimal + "|" + regexInteger;

			Pattern pattern = Pattern.compile(regexDouble);
			Matcher matcher = pattern.matcher(String.valueOf(arg));

			return matcher.find();
		}

		return false;
	}

	public static boolean isValidEmailAddress(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

		Pattern p = Pattern.compile(ePattern);
		Matcher m = p.matcher(email);

		return m.matches();
	}

	public static boolean isValidMobileNumber(String mobileNumber) {
		Pattern p = Pattern.compile("\\d{10}");
		Matcher m = p.matcher(mobileNumber);

		return m.matches();
	}

	public static String getCategoryColor(String category) {
		if (category.trim().equals("Above Ideal Ref. Range".trim()))
			category = "aboveHealthy";
		else if (category.trim().equals("Healthy".trim()))
			category = "healthy";
		else if (category.trim().equals("Below Ideal Ref. Range".trim()))
			category = "belowHealthy";
		else {
			category = "";
		}
		return category;
	}
}
