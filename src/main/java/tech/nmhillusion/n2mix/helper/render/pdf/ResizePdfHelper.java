package tech.nmhillusion.n2mix.helper.render.pdf;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

import java.io.IOException;
import java.io.InputStream;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-22
 */
public class ResizePdfHelper {

    private static final int MAXIMUM_BUFFER_PDF_IN_BYTES = 1024 * 1024 * 2;
    private final float width;
    private final float height;

    public ResizePdfHelper(float width, float height) {
        this.width = width;
        this.height = height;
    }

    private float calculateScaleFactor(PDRectangle mediaBox_) {
        return Math.min(
                width / mediaBox_.getWidth()
                , height / mediaBox_.getHeight()
        );
    }


    public byte[] render(InputStream originalData) throws IOException {
        byte[] renderedData_;

        try (final PDDocument pdDocumentSrc = PDDocument.load(originalData);
             final PDDocument pdDocumentOut = new PDDocument()) {
            final PDPageTree documentPages = pdDocumentSrc.getPages();
            for (final PDPage page_ : documentPages) {
                final PDRectangle currentMediaBox = page_.getMediaBox();
                final float scaleFactor_ = calculateScaleFactor(currentMediaBox);

                //-- Mark: for scaling
                try (final PDPageContentStream pdPageContentStream = new PDPageContentStream(
                        pdDocumentSrc
                        , page_
                        , PDPageContentStream.AppendMode.PREPEND
                        , true
                        , true
                )) {
                    pdPageContentStream.transform(
                            Matrix.getScaleInstance(scaleFactor_, scaleFactor_)
                    );
                }

                final PDRectangle newMediaBox_ = new PDRectangle(
                        currentMediaBox.getWidth() * scaleFactor_
                        , currentMediaBox.getHeight() * scaleFactor_
                );

                page_.setMediaBox(newMediaBox_);

                //-- Mark: for out content
                final PDPage outPage = pdDocumentOut.importPage(page_);
                outPage.setResources(page_.getResources());
                outPage.setMetadata(page_.getMetadata());
            }

            try (final ByteArrayOutputStream renderedDataOutputStream_ = new ByteArrayOutputStream()) {
                pdDocumentOut.save(renderedDataOutputStream_);
                renderedDataOutputStream_.flush();
                renderedData_ = renderedDataOutputStream_.toByteArray();
            }
        }

        return renderedData_;
    }

}
