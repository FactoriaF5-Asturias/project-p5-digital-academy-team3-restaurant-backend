package restaurante.team3.giacobello.invoices.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ReportPeriodTest {

    @ParameterizedTest
    @CsvSource({
            "DAY,     2026-08-15, 2026-08-15, 2026-08-15",
            "MONTH,   2026-08-15, 2026-08-01, 2026-08-31",
            "MONTH,   2028-02-10, 2028-02-01, 2028-02-29",
            "QUARTER, 2026-08-15, 2026-07-01, 2026-09-30",
            "QUARTER, 2026-01-01, 2026-01-01, 2026-03-31",
            "QUARTER, 2026-12-31, 2026-10-01, 2026-12-31",
            "YEAR,    2026-08-15, 2026-01-01, 2026-12-31"
    })
    void shouldReturnTheNaturalRangeThatContainsTheDate(
            ReportPeriod period, LocalDate date, LocalDate expectedFrom, LocalDate expectedTo) {
        assertEquals(expectedFrom, period.from(date));
        assertEquals(expectedTo, period.to(date));
    }
}
