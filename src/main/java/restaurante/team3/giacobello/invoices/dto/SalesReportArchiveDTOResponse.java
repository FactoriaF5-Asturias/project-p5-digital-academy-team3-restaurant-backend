package restaurante.team3.giacobello.invoices.dto;

import java.time.LocalDate;

public record SalesReportArchiveDTOResponse(
        LocalDate date,
        String path) {
}
