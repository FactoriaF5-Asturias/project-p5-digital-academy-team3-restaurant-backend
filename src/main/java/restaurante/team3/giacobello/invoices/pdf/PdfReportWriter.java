package restaurante.team3.giacobello.invoices.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public final class PdfReportWriter implements AutoCloseable {

    public static final float MARGIN = 50;
    public static final float RIGHT_EDGE = 545;

    private static final float LINE_HEIGHT = 22;
    private static final float TITLE_SIZE = 18;
    private static final float TEXT_SIZE = 11;

    private final PDDocument document;
    private final PDFont regular;
    private final PDFont bold;
    private PDPageContentStream content;
    private List<String> tableHeader = List.of();
    private List<PdfColumn> tableColumns = List.of();
    private float y;

    public PdfReportWriter() throws IOException {
        this.document = new PDDocument();
        this.regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        this.bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        newPage();
    }

    public void title(String text) throws IOException {
        writeLine(bold, TITLE_SIZE, text);
    }

    public void text(String text) throws IOException {
        writeLine(regular, TEXT_SIZE, text);
    }

    public void boldText(String text) throws IOException {
        writeLine(bold, TEXT_SIZE, text);
    }

    public void skipLine() {
        y -= LINE_HEIGHT;
    }

    public void keepTogether(int lines) throws IOException {
        if (y - (lines - 1) * LINE_HEIGHT < MARGIN) {
            newPage();
        }
    }

    public void startTable(List<String> header, List<PdfColumn> columns) throws IOException {
        tableHeader = List.copyOf(header);
        tableColumns = List.copyOf(columns);
        writeTableHeader();
    }

    public void row(List<String> cells) throws IOException {
        writeCells(regular, cells);
    }

    public void endTable() {
        tableHeader = List.of();
        tableColumns = List.of();
    }

    public byte[] toBytes() throws IOException {
        closeContent();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            document.save(output);
            return output.toByteArray();
        }
    }

    @Override
    public void close() throws IOException {
        closeContent();
        document.close();
    }

    private void writeLine(PDFont font, float size, String text) throws IOException {
        if (y < MARGIN) {
            newPage();
        }
        showText(font, size, MARGIN, text);
        y -= LINE_HEIGHT;
    }

    private void writeCells(PDFont font, List<String> cells) throws IOException {
        if (y < MARGIN) {
            newPage();
        }
        for (int i = 0; i < tableColumns.size(); i++) {
            PdfColumn column = tableColumns.get(i);
            String cell = cells.get(i);
            float x = column.rightAligned()
                    ? column.x() - font.getStringWidth(cell) / 1000 * TEXT_SIZE
                    : column.x();
            showText(font, TEXT_SIZE, x, cell);
        }
        y -= LINE_HEIGHT;
    }

    private void writeTableHeader() throws IOException {
        writeCells(bold, tableHeader);
        float lineY = y + LINE_HEIGHT - 7;
        content.moveTo(MARGIN, lineY);
        content.lineTo(RIGHT_EDGE, lineY);
        content.setLineWidth(0.5f);
        content.stroke();
    }

    private void showText(PDFont font, float size, float x, String text) throws IOException {
        content.beginText();
        content.setFont(font, size);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    private void newPage() throws IOException {
        closeContent();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        content = new PDPageContentStream(document, page);
        y = page.getMediaBox().getHeight() - MARGIN;
        if (!tableHeader.isEmpty()) {
            writeTableHeader();
        }
    }

    private void closeContent() throws IOException {
        if (content != null) {
            content.close();
            content = null;
        }
    }
}
