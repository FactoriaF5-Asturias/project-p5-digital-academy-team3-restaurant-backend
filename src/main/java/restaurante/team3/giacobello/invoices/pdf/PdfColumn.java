package restaurante.team3.giacobello.invoices.pdf;

public record PdfColumn(float x, boolean rightAligned, float maxWidth) {

    private static final float NO_LIMIT = Float.MAX_VALUE;

    public static PdfColumn left(float x) {
        return new PdfColumn(x, false, NO_LIMIT);
    }

    public static PdfColumn left(float x, float maxWidth) {
        return new PdfColumn(x, false, maxWidth);
    }

    public static PdfColumn right(float rightEdge) {
        return new PdfColumn(rightEdge, true, NO_LIMIT);
    }
}
