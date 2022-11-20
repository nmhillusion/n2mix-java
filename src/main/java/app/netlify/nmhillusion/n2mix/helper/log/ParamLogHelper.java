package app.netlify.nmhillusion.n2mix.helper.log;

import java.util.HashMap;
import java.util.Map;

import static app.netlify.nmhillusion.n2mix.helper.log.LogHelper.getLog;

/**
 * date: 2020-12-16
 * created-by: nmhillusion
 */

public class ParamLogHelper {
    private String keys = "";
    private Object[] values = {};

    public ParamLogHelper setKeys(String keys) {
        this.keys = keys;
        return this;
    }

    public ParamLogHelper setValues(Object... values) {
        this.values = values;
        return this;
    }

    public String build(Object object) {
        try {
            if (null == this.keys || null == this.values) {
                throw new Exception("keys or values is null.");
            }

            String[] keyList = this.keys.split(",");
            if (keyList.length != values.length) {
                throw new Exception("length of keys and values are not equal.");
            }

            int length = keyList.length;
            Map<String, Object> resultMap = new HashMap<>();
            for (int index = 0; index < length; index++) {
                resultMap.put(keyList[index].trim(), values[index]);
            }

            String methodName = "";
            Class<?> mClass = object instanceof Class<?> ? (Class<?>) object : object.getClass();
            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                if (mClass.getName().contains(stackTraceElement.getClassName())) {
                    methodName = stackTraceElement.getMethodName();
                    break;
                }
            }
            return "Param_of_Method: " + methodName + "(" + resultMap + ");";
        } catch (Exception ex) {
            getLog(this).warn(ex);
            return "-- Cannot wire param - method --";
        }
    }
}
