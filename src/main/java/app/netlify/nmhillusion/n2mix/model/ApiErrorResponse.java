package app.netlify.nmhillusion.n2mix.model;

import app.netlify.nmhillusion.n2mix.util.DateUtil;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Calendar;

public class ApiErrorResponse implements Serializable {
    private static final Calendar calendar = Calendar.getInstance();
    private final HttpStatus status;
    private final String errorName;
    private final String message;
    private final String timestamp;
    private final String timestampPattern;

    public ApiErrorResponse(HttpStatus status, String errorName, String message) {
        this.status = status;
        this.errorName = errorName;
        this.message = message;
        this.timestampPattern = "dd/MM/yyyy HH:mm:ss";
        this.timestamp = DateUtil.format(calendar.getTime(), timestampPattern);
    }

    public JSONObject toJSON() {
        JSONObject data = new JSONObject();
        data.put("status", status.value());
        data.put("errorName", errorName);
        data.put("message", message);
        data.put("timestamp", timestamp);
        data.put("timestampPattern", timestampPattern);

        return data;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

    public int getStatus() {
        return status.value();
    }

    public String getErrorName() {
        return errorName;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTimestampPattern() {
        return timestampPattern;
    }
}
