package app.netlify.nmhillusion.n2mix.util;

import app.netlify.nmhillusion.n2mix.helper.log.LogHelper;
import app.netlify.nmhillusion.n2mix.validator.StringValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ObjectMapperUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String convertObjectToJson(Object object) throws JsonProcessingException {
        if (null == object) {
            return null;
        }
        return mapper.writeValueAsString(object);
    }

    /**
     * @return object of class T to be converted <br>
     * If a field is not exist in json -> throw an exception
     */
    public static <T> T convertJsonToObject(String json, Class<T> clsToConvert) throws JsonProcessingException {
        if (null == json || StringValidator.isBlank(json)) {
            return null;
        }
        return mapper.readValue(json, clsToConvert);
    }

    /**
     * @return object of class T to be converted<br>
     * If a field is not exist in json -> this field will be null<br>
     * If a field is a complex object -> it will be a jsonObject or jsonArray, not an Array or a Map
     */
    public static <T> T convertJsonToObjectBaseJson(String json, Class<T> clsToConvert) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        if (null == json || StringValidator.isBlank(json)) {
            return null;
        }

        Constructor<T> constructor = clsToConvert.getConstructor();
        T data = constructor.newInstance();

        JSONObject jsonData = new JSONObject(json);
        for (String key : jsonData.keySet()) {
            try {
                Field field = clsToConvert.getDeclaredField(key);

                String setMethodName = "set" + StringUtil.convertPascalCaseFromCamelCase(key);
                Method setMethod = clsToConvert.getMethod(setMethodName, field.getType());
                setMethod.invoke(data, jsonData.get(key));
            } catch (Exception ex) {
                LogHelper.getLog(ObjectMapperUtil.class).warn(ex);
            }
        }

        return data;
    }
}
