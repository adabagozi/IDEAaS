package utils;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * @author Ada Bagozi
 *
 */
public class DateUtils {


	private static final String HOURS = "hours";
	private static final String MINUTES = "minutes";
	private static final String SECONDS = "seconds";

	/**
	 *
	 */
	public DateUtils() {
		super();
		// TODO Auto-generated constructor stub
	}

	private LocalDate getLocalDate(String datetime){
		return LocalDateTime.ofInstant(DateTimeFormatter.ISO_INSTANT.parse(datetime, Instant::from), ZoneId.of("Z")).toLocalDate();
	}

	public LocalDateTime getLocalDateTime(String localDateTime) {
		return LocalDateTime.ofInstant(DateTimeFormatter.ISO_INSTANT.parse(localDateTime, Instant::from), ZoneId.of("Z"));
	}


	/**
	 * Transform string into LocalDateTime
	 * @param datetime string in the format "yyyy/MM/dd HH:mm:ss"
	 * @return
	 */
	public LocalDateTime getDateTimeFromString2(String datetime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		return LocalDateTime.parse(datetime, formatter);
	}

	/**
	 * Transform LocalDateTime into string
	 * @param datetime
	 * @return string in the format "dd/MM/yyyy HH:mm:ss"
	 */
	public String getStringFromDateTime(LocalDateTime datetime) {
		String month = datetime.getMonthValue() > 9 ? (""+datetime.getMonthValue()) : ("0"+datetime.getMonthValue());
		String day = datetime.getDayOfMonth() > 9 ? (""+datetime.getDayOfMonth()) : ("0"+datetime.getDayOfMonth());
		String hours = datetime.getHour() > 9 ? (""+datetime.getHour()) : ("0"+datetime.getHour());
		String minutes = datetime.getMinute() > 9 ? (""+datetime.getMinute()) : ("0"+datetime.getMinute());
		String seconds = datetime.getSecond() > 9 ? (""+datetime.getSecond()) : ("0"+datetime.getSecond());
		String date = day+"/"+month+"/"+datetime.getYear()+" "+hours+":"+minutes+":"+seconds;
		return date;
	}

	/**
	 * Transform string into LocalDateTime
	 * @param datetime string in the format "dd/MM/yyyy HH:mm:ss"
	 * @return
	 */
	public LocalDateTime getDateTimeFromString(String datetime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		return LocalDateTime.parse(datetime, formatter);
	}


	/**
	 * Transform string into LocalDateTime
	 * @param datetime string in the format
	 * @return
	 */
	public LocalDateTime getDateTimeFromString(String datetime, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		return LocalDateTime.parse(datetime, formatter);
	}

	/**
	 * Metodo per ottenere la data dalla stringa inserita rappresentante la data nel file bin in input
	 * @param timeFromBin il timestamp dentro una riga di un file *.bin. Il timestmp segue un formato tipo "5/27/2015 11:07:32 AM.812"
	 * @return il timestamp in ingresso, ora di tipo {@link Instant}
	 */
	public Instant getTimestamp(String timeFromBin, String dateFormat) throws Exception, ParseException{
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				.appendPattern(dateFormat)
				.appendFraction(ChronoField.MICRO_OF_SECOND, 0, 3, true)
				.toFormatter()
				.withZone(ZoneId.of("Z"));

		return formatter
				.parse(timeFromBin, LocalDateTime::from)
				.atZone(ZoneId.of("Z"))
				.toInstant();

	}

	/**
	 * Metodo per ottenere il nome della collection dall'istante di tempo i
	 * @param i istante di tempo
	 * @return una stringa che rappresenta il nome della collection formata da "anno_mese", ad esempio "2016_01"
	 */
	public String getCollectionName(LocalDateTime ldt){
//		LocalDateTime ldt = LocalDateTime.ofInstant(i, ZoneId.of("Z"));
		String s = DateTimeFormatter.ofPattern("yyyy_MM").format(ldt);
		return s;
	}


	public LocalDateTime getOtherDateTime(LocalDateTime datetimeInput, String intervalType, int intervalValue, boolean increment) {
		if(datetimeInput != null) {
			 LocalDateTime datetime = LocalDateTime.from(datetimeInput);
			if(intervalType.equalsIgnoreCase(HOURS)){
				if (increment) {
					return datetime.plusHours(intervalValue);
				}else {
					datetime.minusHours(intervalValue);
				}
			} else if(intervalType.equalsIgnoreCase(MINUTES)){
				if (increment) {
					return datetime.plusMinutes(intervalValue);
				}else {
					datetime.minusMinutes(intervalValue);
				}

			} else if(intervalType.equalsIgnoreCase(SECONDS)){
				if (increment) {
					return datetime.plusSeconds(intervalValue);
				}else {
					datetime.minusSeconds(intervalValue);
				}
			}else {
				if (increment) {
					return datetime.plusDays(intervalValue);
				}else {
					datetime.minusDays(intervalValue);
				}
			}
		}
		return null;
	}
}
