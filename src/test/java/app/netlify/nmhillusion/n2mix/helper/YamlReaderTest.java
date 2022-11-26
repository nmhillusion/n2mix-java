package app.netlify.nmhillusion.n2mix.helper;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlReaderTest {

    @Test
    void getProperty() {
        assertDoesNotThrow(() -> {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data-test/vehicle.yml")) {
                final YamlReader yamlReader = new YamlReader(inputStream);
                {
                    final Integer carWheel = yamlReader.getProperty("car.wheel", Integer.class);
                    final String carColor = yamlReader.getProperty("car.color", String.class);
                    final String carSpeed = yamlReader.getProperty("car.speed", String.class);

                    assertEquals(4, carWheel, "check car wheel");
                    assertEquals("blue", carColor, "check car color");
                    assertEquals("fast", carSpeed, "check car speed");
                }

                {
                    final Integer bicycleWheel = yamlReader.getProperty("bicycle.wheel", Integer.class);
                    final String bicycleColor = yamlReader.getProperty("bicycle.color", String.class);
                    final String bicycleSpeed = yamlReader.getProperty("bicycle.speed", String.class);
                    final String bicycleSpeedStr = yamlReader.getProperty("bicycle.speed");

                    assertEquals(2, bicycleWheel, "check bicycle wheel");
                    assertEquals("white", bicycleColor, "check bicycle color");
                    assertEquals("slow", bicycleSpeed, "check bicycle speed");
                    assertEquals("slow", bicycleSpeedStr, "check bicycle speed with default string type");
                }

                {
                    final Integer session = yamlReader.getProperty("session", Integer.class);
                    assertEquals(9, session, "check session number");
                    final Integer sessionWithDefault = yamlReader.getProperty("session", Integer.class, 12);
                    assertEquals(9, sessionWithDefault, "check session number with default");

                    final String sessionStr = yamlReader.getProperty("session");
                    assertEquals("9", sessionStr, "check session number with string type");

                    final int lesson = yamlReader.getProperty("lesson", Integer.class, 10);
                    assertEquals(10, lesson, "check lesson with default value");
                }
            }
        });
    }
}