package backend;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import backend.dtos.MoviePurchasedDto;

/**
 * Utility class providing static methods for common data operations.
 * <p>
 * This class contains methods for handling strings, numbers, dates, and durations.
 * It also includes utilities for checking the state of movie purchases.
 * </p>
 */
public class DataUtils {

    /**
     * Checks if a string is blank.
     * <p>
     * A string is considered blank if it is {@code null} or contains only whitespace characters.
     * </p>
     * 
     * @param str the string to check
     * @return {@code true} if the string is blank; {@code false} otherwise
     */
    public static boolean isBlank(String str) {
        return !isNotBlank(str);
    }

    /**
     * Checks if a string is not blank.
     * <p>
     * A string is considered not blank if it is not {@code null} and contains at least one non-whitespace character.
     * </p>
     * 
     * @param str the string to check
     * @return {@code true} if the string is not blank; {@code false} otherwise
     */
    public static boolean isNotBlank(String str) {
        return str != null && !str.isBlank();
    }
    
    /**
     * Checks if a list is empty.
     * <p>
     * A list is considered empty if it is {@code null} or contains no elements.
     * </p>
     * 
     * @param list the list to check
     * @return {@code true} if the list is empty; {@code false} otherwise
     */
    public static boolean isListEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
    
    /**
     * Compares two strings, ignoring case.
     * <p>
     * Returns {@code true} if both strings are {@code null} or equal ignoring case; {@code false} otherwise.
     * </p>
     * 
     * @param str1 the first string
     * @param str2 the second string
     * @return {@code true} if the strings are equal ignoring case; {@code false} otherwise
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 != null && str1.equalsIgnoreCase(str2);
    }

    /**
     * Parses a string into a {@code Double}.
     * <p>
     * Returns {@code null} if the string cannot be parsed as a {@code Double}.
     * </p>
     * 
     * @param str the string to parse
     * @return the parsed {@code Double} or {@code null} if parsing fails
     */
    public static Double getNumber(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Throwable e) {
            return null;
        }
    }
    
    /**
     * Parses a string into an {@code Integer}.
     * <p>
     * Returns {@code null} if the string cannot be parsed as an {@code Integer}.
     * </p>
     * 
     * @param str the string to parse
     * @return the parsed {@code Integer} or {@code null} if parsing fails
     */
    public static Integer getIntegerNumber(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Parses a string into a {@code BigDecimal}.
     * <p>
     * Returns {@code null} if the string cannot be parsed as a {@code BigDecimal}.
     * </p>
     * 
     * @param str the string to parse
     * @return the parsed {@code BigDecimal} or {@code null} if parsing fails
     */
    public static BigDecimal getBigDecimal(String str) {
        if (DataUtils.isNotBlank(str))
            return new BigDecimal(getNumber(str));
        return null;
    }
    
    /**
     * Converts a {@code LocalDateTime} to a string formatted according to the system's default time zone.
     * 
     * @param localDateTime the {@code LocalDateTime} to convert
     * @return the formatted date-time string
     */
    public static String getLocalDateTimeInCurrentZone(LocalDateTime localDateTime) {
        ZonedDateTime date = localDateTime.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy HH:mm:ss");
        return date.format(formatter);
    }
    
    /**
     * Converts a {@code Duration} into a string representation.
     * <p>
     * The format is "X days: HH:MM:SS", where X is the number of days, and HH, MM, SS are hours, minutes, and seconds respectively.
     * </p>
     * 
     * @param duration the {@code Duration} to convert
     * @return the formatted duration string
     */
    public static String durationToString(Duration duration) {
        String time  = String.format("%02d days: %02d:%02d:%02d", duration.toDays(), duration.toHoursPart(), duration.toMinutesPart(),
                duration.toSecondsPart());
        return time;
    }
    
    /**
     * Determines if a {@code MoviePurchasedDto} is usable.
     * <p>
     * The movie is considered usable if it is not rented (it is bought) or if it is rented but still within the allowed rental period.
     * </p>
     * 
     * @param moviePurchased the {@code MoviePurchasedDto} to check
     * @return {@code true} if the movie is usable; {@code false} otherwise
     */
    public static boolean isUseable(MoviePurchasedDto moviePurchased) {
        return isUseable(moviePurchased.isRented(), moviePurchased.getRentTimeSincePurchase());
    }
    
    /**
     * Determines if a movie is usable based on its rental status and rental time.
     * <p>
     * The movie is considered usable if it is not rented (it is bought) or if it is rented but still within the allowed rental period.
     * </p>
     * 
     * @param isRented whether the movie is currently rented
     * @param timeSince the time since the movie was rented
     * @return {@code true} if the movie is usable; {@code false} otherwise
     */
    public static boolean isUseable(boolean isRented, LocalDateTime timeSince) {
        if (!isRented)
            return true;
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(timeSince);
    }
    
    public static String emailFormatted(String email) {
    	return email != null ? email.trim().toLowerCase() : null;
    }
}