package gdg.beforeonebite.app.food.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SelectionTimeLabeler {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREAN);

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREAN);

    public static String dayLabel(LocalDate selectedDate, LocalDate today) {
        if (selectedDate.equals(today)) {
            return "오늘";
        }

        if (selectedDate.equals(today.minusDays(1))) {
            return "어제";
        }

        return selectedDate.format(DATE_FMT);
    }

    public static String timeLabel(LocalDateTime selectedAt) {
        return selectedAt.format(TIME_FMT);
    }
}
