package com.test.docgen.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import com.test.docgen.entity.Barangay;

public final class PdfBoxBarangayWriter {

    private PdfBoxBarangayWriter() {}

    public static void write(OutputStream os, List<Barangay> barangays, String municipality, String province) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            float margin = 50f;
            float yStart = page.getMediaBox().getHeight() - margin;
            float y = yStart;

            float rowHeight = 18f;

            // Column X positions
            float xBarangay = margin;
            float xMunicipality = 260f;
            float xProvince = 410f;

            PDPageContentStream cs = new PDPageContentStream(doc, page);
            try {
                // Title
                y = writeLine(cs, fontBold, 14, margin, y, "Barangay Report");
                y = writeLine(cs, font, 11, margin, y - 6, "Municipality: " + municipality + " | Province: " + province);
                y -= 18;

                // Header row
                y = drawHeader(cs, fontBold, 11, xBarangay, xMunicipality, xProvince, y);

                // Rows
                cs.setFont(font, 10);

                for (Barangay barangay : barangays) {
                    if (y <= margin + rowHeight) {
                        cs.close(); // finish this page

                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        y = yStart;

                        cs = new PDPageContentStream(doc, page);
                        // repeat header on new page
                        y = drawHeader(cs, fontBold, 11, xBarangay, xMunicipality, xProvince, y);
                        cs.setFont(font, 10);
                    }

                    writeAt(cs, xBarangay, y, safe(barangay.getBarangay()));
                    writeAt(cs, xMunicipality, y, barangay.getMunicipality());
                    writeAt(cs, xProvince, y, barangay.getProvince());

                    y -= rowHeight;
                }
            } finally {
                cs.close();
            }

            doc.save(os);
        }
    }

    private static float drawHeader(PDPageContentStream cs, PDFont fontBold, int size,
                                    float xBarangay, float xMunicipality, float xProvince, float y) throws IOException {
        cs.setFont(fontBold, size);
        writeAt(cs, xBarangay, y, "Barangay");
        writeAt(cs, xMunicipality, y, "Municipality");
        writeAt(cs, xProvince, y, "Province");
        return y - 18f;
    }

    private static float writeLine(PDPageContentStream cs, PDFont font, int size, float x, float y, String text) throws IOException {
        cs.setFont(font, size);
        writeAt(cs, x, y, text);
        return y - (size + 4);
    }

    private static void writeAt(PDPageContentStream cs, float x, float y, String text) throws IOException {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}