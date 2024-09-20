package tech.nmhillusion.n2mix.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import tech.nmhillusion.n2mix.constant.ContentType;
import tech.nmhillusion.n2mix.exception.ApiResponseException;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.model.ApiErrorResponse;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidNoInputFunction;

import java.io.IOException;
import java.io.OutputStream;

public abstract class ResponseEntityUtil {
    private static int isWriteResponse = -1;
    private static int maxResponseLog = 0;

    private static <T> void writeLogResponse(T body) {
        //-- Mark: WRITE LOG
        if (-1 == isWriteResponse) {
            try {
                final String writeResponse = "true";
                final String maxResponseLogRaw = "4000";

                if ("true".equalsIgnoreCase(writeResponse)) {
                    isWriteResponse = 1;
                }

                maxResponseLog = Integer.parseInt(maxResponseLogRaw);
            } catch (Exception ex) {
                LogHelper.getLogger(ResponseEntityUtil.class).error(ex.getMessage());
            }
        }

        if (1 == isWriteResponse) {
            try {
                LogHelper.getLogger(ResponseEntityUtil.class).info(StringUtil.truncate(String.valueOf(body), maxResponseLog));
            } catch (Exception ex) {
                LogHelper.getLogger(ResponseEntityUtil.class).error(ex);
            }
        }
    }

    public static <T> ResponseEntity<T> make(T body) {
        writeLogResponse(body);

        //-- Mark: RETURN RESPONSE
        if (null == body) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(body);
        }
    }

    public static <T> ResponseEntity<T> makeNoContent(ThrowableVoidNoInputFunction func) {
        try {
            func.apply();
        } catch (Exception ex) {
            LogHelper.getLogger(ResponseEntityUtil.class).error(ex);
            throw new ApiResponseException(ex);
        }
        //-- Mark: RETURN RESPONSE
        return ResponseEntity.noContent().build();
    }

    public static <T> T sendResponse(HttpServletResponse response, ApiErrorResponse data) throws IOException {
        response.setHeader("Content-Type", ContentType.JSON);
        response.setStatus(data.getStatus());
        final OutputStream os = response.getOutputStream();
        os.write(data.toJSON().toString().getBytes());
        os.flush();
        os.close();
        return null;
    }
}
