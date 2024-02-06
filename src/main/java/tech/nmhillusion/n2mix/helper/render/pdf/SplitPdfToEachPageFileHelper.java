package tech.nmhillusion.n2mix.helper.render.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-06
 */
public class SplitPdfToEachPageFileHelper {


    public List<byte[]> splitData(InputStream inputPdfData) throws IOException {
        final List<byte[]> pagesData = new ArrayList<>();

        try (final PDDocument pdDocument = PDDocument.load(inputPdfData)) {
            final PDPageTree documentPages = pdDocument.getPages();
            final int pagesCount = documentPages.getCount();
            for (int pageIdx = 0; pageIdx < pagesCount; ++pageIdx) {
                pagesData.add(
                        exportToPage(documentPages.get(pageIdx))
                );
            }
        }

        return pagesData;
    }

    private byte[] exportToPage(PDPage pdPage) throws IOException {
        if (null == pdPage) {
            return new byte[0];
        }

        try (final PDDocument pdDocument = new PDDocument();
             final ByteArrayOutputStream byteArrayOutputStream_ = new ByteArrayOutputStream()) {
            pdDocument.addPage(
                    pdPage
            );

            pdDocument.save(byteArrayOutputStream_);

            byteArrayOutputStream_.flush();
            return byteArrayOutputStream_.toByteArray();
        }
    }
}
