package restaurante.team3.giacobello.invoices.pdf;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

class PdfReportWriterTest {

    private static final List<PdfColumn> COLUMNS = List.of(
            PdfColumn.left(PdfReportWriter.MARGIN, 120),
            PdfColumn.right(PdfReportWriter.RIGHT_EDGE));

    @Test
    void shouldReplaceCharactersTheFontCannotDraw() throws IOException {
        String text = render(writer -> {
            writer.text("Té matcha ☕ – especial");
            writer.startTable(List.of("Producto", "Precio"), COLUMNS);
            writer.row(List.of("Пельмени", "4,00 €"));
        });

        assertTrue(text.contains("Té matcha ? – especial"));
        assertTrue(text.contains("????????"));
    }

    @Test
    void shouldReplaceLineBreaksAndTabsWithSpaces() throws IOException {
        String text = render(writer -> writer.text("Tarta\nde\tqueso"));

        assertTrue(text.contains("Tarta de queso"));
    }

    @Test
    void shouldTruncateCellsLongerThanTheirColumn() throws IOException {
        String longName = "Hamburguesa de buey madurado con queso de cabra y cebolla caramelizada";

        String text = render(writer -> {
            writer.startTable(List.of("Producto", "Precio"), COLUMNS);
            writer.row(List.of(longName, "14,90 €"));
        });

        assertFalse(text.contains(longName));
        assertTrue(text.contains("Hamburguesa de bue…"));
        assertTrue(text.contains("14,90 €"));
    }

    @Test
    void shouldRejectRowsWithADifferentNumberOfCellsThanColumns() throws IOException {
        try (PdfReportWriter writer = new PdfReportWriter()) {
            writer.startTable(List.of("Producto", "Precio"), COLUMNS);

            assertThrows(IllegalArgumentException.class, () -> writer.row(List.of("Solo una celda")));
        }
    }

    private String render(WriterAction action) throws IOException {
        try (PdfReportWriter writer = new PdfReportWriter()) {
            action.apply(writer);
            try (PDDocument document = Loader.loadPDF(writer.toBytes())) {
                return new PDFTextStripper().getText(document);
            }
        }
    }

    @FunctionalInterface
    private interface WriterAction {
        void apply(PdfReportWriter writer) throws IOException;
    }
}
