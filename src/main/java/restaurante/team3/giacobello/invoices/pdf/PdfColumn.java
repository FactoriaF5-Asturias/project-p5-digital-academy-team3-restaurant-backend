package restaurante.team3.giacobello.invoices.pdf;

public record PdfColumn(float x, boolean rightAligned) {

    public static PdfColumn left(float x) {
        return new PdfColumn(x, false);
    }

    public static PdfColumn right(float rightEdge) {
        return new PdfColumn(rightEdge, true);
    }
}
