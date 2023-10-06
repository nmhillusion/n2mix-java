package tech.nmhillusion.n2mix.helper.render.pdf;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-06
 */
public class MergePdfIntoOnePageHelper {

    private List<PDPage> createPdfPageFromByteArray(PDDocument pdDocumentData) throws IOException {
        if (null == pdDocumentData) {
            return Collections.emptyList();
        }

        final List<PDPage> pdPageList = new ArrayList<>();

        final PDPageTree documentPages = pdDocumentData.getPages();
        final int pagesCount = documentPages.getCount();

        for (int pageIdx = 0; pageIdx < pagesCount; ++pageIdx) {
            pdPageList.add(
                    documentPages.get(pageIdx)
            );
        }

        return pdPageList;
    }

    public byte[] mergePdfPages(List<InputStream> pdfPagesData) throws IOException {
        final List<PDDocument> pdDocumentPageList = new ArrayList<>();
        try (final PDDocument pdDocument = new PDDocument()) {

            for (InputStream pdfPagesDatum : pdfPagesData) {
                final PDDocument pdDocumentData = PDDocument.load(pdfPagesDatum);
                pdDocumentPageList.add(pdDocumentData);

                final List<PDPage> pdfPageList = createPdfPageFromByteArray(pdDocumentData);
                pdfPageList.forEach(pdDocument::addPage);
            }

            try (final ByteArrayOutputStream byteArrayOutputStream_ = new ByteArrayOutputStream()) {
                pdDocument.save(byteArrayOutputStream_);
                byteArrayOutputStream_.flush();

                return byteArrayOutputStream_.toByteArray();
            }
        } finally {
            for (PDDocument pdDocument : pdDocumentPageList) {
                pdDocument.close();
            }
        }
    }
}
