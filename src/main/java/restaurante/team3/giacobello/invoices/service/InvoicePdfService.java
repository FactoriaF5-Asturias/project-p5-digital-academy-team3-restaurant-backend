package restaurante.team3.giacobello.invoices.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import org.springframework.stereotype.Service;

import restaurante.team3.giacobello.invoices.dto.InvoiceDTOResponse;
import restaurante.team3.giacobello.invoices.pdf.PdfColumn;
import restaurante.team3.giacobello.invoices.pdf.PdfFormat;
import restaurante.team3.giacobello.invoices.pdf.PdfReportWriter;
import restaurante.team3.giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.giacobello.orders.dto.OrderItemDTOResponse;
import restaurante.team3.giacobello.orders.service.OrderService;

@Service
public class InvoicePdfService {

    private static final List<String> TABLE_HEADER = List.of("Producto", "Cant.", "Precio", "Subtotal");
    private static final List<PdfColumn> TABLE_COLUMNS = List.of(
            PdfColumn.left(PdfReportWriter.MARGIN, 260),
            PdfColumn.right(360),
            PdfColumn.right(450),
            PdfColumn.right(PdfReportWriter.RIGHT_EDGE));

    private final InvoiceService invoiceService;
    private final OrderService orderService;

    public InvoicePdfService(InvoiceService invoiceService, OrderService orderService) {
        this.invoiceService = invoiceService;
        this.orderService = orderService;
    }

    public byte[] generate(Integer orderId) {
        InvoiceDTOResponse invoice = invoiceService.findByOrderId(orderId);
        OrderDTOResponse order = orderService.findById(orderId);

        try (PdfReportWriter writer = new PdfReportWriter()) {
            writer.title("Giacobello - Factura");
            writer.skipLine();
            writer.text("Factura: " + invoice.invoiceNumber());
            writer.text("Pedido: " + order.id());
            writer.text("Fecha: " + PdfFormat.dateTime(invoice.issuedAt()));
            writer.text("Método de pago: " + order.paymentMethodName());
            writer.skipLine();
            writer.startTable(TABLE_HEADER, TABLE_COLUMNS);
            for (OrderItemDTOResponse item : order.items()) {
                writer.row(itemCells(item));
            }
            writer.endTable();
            writer.skipLine();
            writer.boldText("Total: " + PdfFormat.amount(invoice.totalAmount()));
            return writer.toBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el PDF de la factura", e);
        }
    }

    private List<String> itemCells(OrderItemDTOResponse item) {
        return List.of(
                item.productName(),
                String.valueOf(item.quantity()),
                PdfFormat.amount(item.unitPrice()),
                PdfFormat.amount(item.subtotal()));
    }
}
