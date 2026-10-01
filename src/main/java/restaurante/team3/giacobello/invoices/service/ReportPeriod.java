package restaurante.team3.giacobello.invoices.service;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;

public enum ReportPeriod {
    DAY,
    MONTH,
    QUARTER,
    YEAR;

    public LocalDate from(LocalDate date) {
        return switch (this) {
            case DAY -> date;
            case MONTH -> date.with(TemporalAdjusters.firstDayOfMonth());
            case QUARTER -> date.with(IsoFields.DAY_OF_QUARTER, 1);
            case YEAR -> date.with(TemporalAdjusters.firstDayOfYear());
        };
    }

    public LocalDate to(LocalDate date) {
        return switch (this) {
            case DAY -> date;
            case MONTH -> date.with(TemporalAdjusters.lastDayOfMonth());
            case QUARTER -> date.with(
                    IsoFields.DAY_OF_QUARTER,
                    date.range(IsoFields.DAY_OF_QUARTER).getMaximum());
            case YEAR -> date.with(TemporalAdjusters.lastDayOfYear());
        };
    }
}
