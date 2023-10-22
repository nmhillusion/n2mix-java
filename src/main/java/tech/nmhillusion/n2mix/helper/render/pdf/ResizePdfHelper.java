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

        try (final PDDocument pdDocument = PDDocument.load(originalData)) {
            final PDPageTree documentPages = pdDocument.getPages();
            for (PDPage page_ : documentPages) {
                final PDRectangle currentMediaBox = page_.getMediaBox();
                final float scaleFactor_ = calculateScaleFactor(currentMediaBox);

                try (final PDPageContentStream pdPageContentStream = new PDPageContentStream(
                        pdDocument
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
            }

            try (final ByteArrayOutputStream renderedDataOutputStream_ = new ByteArrayOutputStream()) {
                pdDocument.save(renderedDataOutputStream_);
                renderedData_ = renderedDataOutputStream_.toByteArray();
            }
        }

        return renderedData_;
    }

}
