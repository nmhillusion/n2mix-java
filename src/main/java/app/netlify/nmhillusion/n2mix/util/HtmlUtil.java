package app.netlify.nmhillusion.n2mix.util;

import app.netlify.nmhillusion.n2mix.validator.StringValidator;
import org.springframework.web.util.HtmlUtils;

/**
 * date: 2023-03-05
 * <p>
 * created-by: nmhillusion
 */

public abstract class HtmlUtil {

    public static String htmlUnescape(String data) {
        if (StringValidator.isBlank(data)) {
            return data;
        }
        final String translatedData = data.replace("&nbsp;", " ");
        return HtmlUtils.htmlUnescape(translatedData)
                ;
    }

    public static String removeTagSyntax(String data) {
        final String unescapeData = htmlUnescape(data);
        return StringUtil.trimWithNull(unescapeData).replaceAll("</?\\w *.*?>", "");
    }
}
