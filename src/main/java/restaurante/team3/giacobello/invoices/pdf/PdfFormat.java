package restaurante.team3.giacobello.invoices.pdf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class PdfFormat {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Locale SPAIN = Locale.of("es", "ES");

    private PdfFormat() {
    }

    public static String amount(BigDecimal amount) {
        return String.format(SPAIN, "%,.2f €", amount);
    }

    public static String date(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    public static String dateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMAT) : "-";
    }
}
