package tech.nmhillusion.n2mix.helper.render.image;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * created by: chubb
 * <p>
 * created date: 2023-10-05
 */
public class TiffImageToPdfHelper {
    public byte[] renderFirstPageOfTiffImage(InputStream tiffData) throws IOException {
        if (null == tiffData) {
            return new byte[0];
        }
        final BufferedImage bufferedImage = ImageIO.read(tiffData);
        final int imageWidth = bufferedImage.getWidth();
        final int imageHeight = bufferedImage.getHeight();

        try (final PDDocument pdDocument = new PDDocument()) {
            final PDPage pdPage = new PDPage();
            pdDocument.addPage(pdPage);
            pdPage.setMediaBox(
                    new PDRectangle(
                            imageWidth, imageHeight
                    )
            );

            final PDImageXObject pdImageXObject = LosslessFactory.createFromImage(pdDocument, bufferedImage);
            try (final PDPageContentStream pdPageContentStream = new PDPageContentStream(pdDocument, pdPage)) {
                pdPageContentStream.drawImage(pdImageXObject,
                        0,
                        0,
                        imageWidth,
                        imageHeight);
            }

            try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                pdDocument.save(byteArrayOutputStream);

                byteArrayOutputStream.flush();
                return byteArrayOutputStream.toByteArray();
            }
        }
    }
}
