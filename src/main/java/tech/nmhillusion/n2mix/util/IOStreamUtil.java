package tech.nmhillusion.n2mix.util;

import jakarta.servlet.ServletInputStream;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * date: 2020-10-26
 * created-by: nmhillusion
 */

public abstract class IOStreamUtil {
    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final StringBuilder stringBuilder = new StringBuilder();

            String line;
            while (null != (line = bufferedReader.readLine())) {
                stringBuilder.append(line).append("\n");
            }

            return stringBuilder.toString();
        }
    }

    public static byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
        if (inputStream instanceof ServletInputStream sis_) {
            try {
                if (!sis_.isReady()) {
                    return new byte[0];
                }
            } catch (IllegalStateException ex) {
                LogHelper.getLogger(IOStreamUtil.class).error("input stream is not ready to read. %s"
                        .formatted(ex.getMessage()));
                return new byte[0];
            }
        }

        return StreamUtils.copyToByteArray(inputStream);
    }

    public static String convertInputStreamToBase64(InputStream inputStream) throws IOException {
        final byte[] bytes = convertInputStreamToByteArray(inputStream);
        final String base64 = Base64.getEncoder().encodeToString(bytes);
        return base64;
    }

    public static String readUtf8File(String absoluteResourcePath) {
        try {
            final byte[] byteData = Files.readAllBytes(Paths.get(absoluteResourcePath));
            return new String(byteData, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            LogHelper.getLogger(IOStreamUtil.class).error(ex);
            return "";
        }
    }
}
