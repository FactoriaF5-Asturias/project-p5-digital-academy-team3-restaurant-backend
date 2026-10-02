package restaurante.team3.giacobello.invoices.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import restaurante.team3.giacobello.invoices.dto.InvoiceDTOResponse;
import restaurante.team3.giacobello.invoices.pdf.PdfColumn;
import restaurante.team3.giacobello.invoices.pdf.PdfFormat;
import restaurante.team3.giacobello.invoices.pdf.PdfReportWriter;

@Service
public class SalesReportPdfService {

    private static final List<String> TABLE_HEADER = List.of("Factura", "Pedido", "Fecha", "Importe");
    private static final List<PdfColumn> TABLE_COLUMNS = List.of(
            PdfColumn.left(PdfReportWriter.MARGIN),
            PdfColumn.left(170),
            PdfColumn.left(260),
            PdfColumn.right(PdfReportWriter.RIGHT_EDGE));

    private final InvoiceService invoiceService;

    public SalesReportPdfService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public byte[] generate(LocalDate from, LocalDate to) {

        List<InvoiceDTOResponse> invoices = invoiceService.findByDateRange(from, to);

        try (PdfReportWriter writer = new PdfReportWriter()) {
            writer.title("Giacobello - Resumen de ventas");
            writer.title(PdfFormat.date(from) + " - " + PdfFormat.date(to));
            writer.skipLine();
            if (invoices.isEmpty()) {
                writer.text("Sin ventas en el periodo");
            } else {
                writer.startTable(TABLE_HEADER, TABLE_COLUMNS);
                for (InvoiceDTOResponse invoice : invoices) {
                    writer.row(invoiceCells(invoice));
                }
                writer.endTable();
            }
            writer.skipLine();
            List<String> totals = totalsLines(invoices);
            writer.keepTogether(totals.size());
            for (String line : totals) {
                writer.boldText(line);
            }
            return writer.toBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el PDF", e);
        }
    }

    private List<String> totalsLines(List<InvoiceDTOResponse> invoices) {
        BigDecimal total = invoices.stream()
                .map(InvoiceDTOResponse::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageTicket = invoices.isEmpty()
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(invoices.size()), 2, RoundingMode.HALF_UP);

        return List.of(
                "Pedidos: " + invoices.size(),
                "Total facturado: " + PdfFormat.amount(total),
                "Ticket medio: " + PdfFormat.amount(averageTicket));
    }

    private List<String> invoiceCells(InvoiceDTOResponse invoice) {
        return List.of(
                invoice.invoiceNumber(),
                String.valueOf(invoice.orderId()),
                PdfFormat.dateTime(invoice.issuedAt()),
                PdfFormat.amount(invoice.totalAmount()));
    }
}
