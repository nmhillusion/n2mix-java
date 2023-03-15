package app.netlify.nmhillusion.n2mix.helper;

import app.netlify.nmhillusion.n2mix.constant.OkHttpContentType;
import app.netlify.nmhillusion.n2mix.helper.http.HttpHelper;
import app.netlify.nmhillusion.n2mix.helper.http.RequestHttpBuilder;
import app.netlify.nmhillusion.n2mix.helper.log.LogHelper;
import app.netlify.nmhillusion.n2mix.type.ChainMap;
import org.junit.jupiter.api.Test;

import static app.netlify.nmhillusion.n2mix.helper.log.LogHelper.getLogger;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class HttpHelperTest {

    HttpHelper httpHelper = new HttpHelper();

    @Test
    void get() {
        assertDoesNotThrow(() -> {
            final byte[] bytes = httpHelper.get(
                    new RequestHttpBuilder()
                            .setUrl("https://google.com")
            );
            LogHelper.getLogger(this).info("Content of get Google homepage: " + new String(bytes));
        });
    }

    @Test
    void post() {
        assertDoesNotThrow(() -> {
            LogHelper.getLogger(this).info("start post data");

            final byte[] binPostData = httpHelper.post(new RequestHttpBuilder()
                    .setUrl("https://httpbin.org/post")
                    .addHeader("token", "very-secret-token")
                    .addHeader("second-header", "my-test-second-header")
                    .setBody(new ChainMap<String, Object>()
                                    .chainPut("orderId", "KB-1902")
                                    .chainPut("itemName", "Keyboard K89"),
                            OkHttpContentType.JSON)
            );

            LogHelper.getLogger(this).info("binPostData: " + new String(binPostData));
        });
    }
}