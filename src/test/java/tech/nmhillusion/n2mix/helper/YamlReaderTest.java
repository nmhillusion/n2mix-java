package tech.nmhillusion.n2mix.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

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

                {
                    final Object bicycleColorObj = yamlReader.getProperty("bicycle.color", Object.class);
                    final Object bicycleWheelObj = yamlReader.getProperty("bicycle.wheel", Object.class);

                    assertEquals("white", bicycleColorObj, "check object obtained: bicycle.color");
                    assertEquals(2, bicycleWheelObj, "check object obtained: bicycle.wheel");
                }
            }
        });
    }

    @Test
    void testNumberWithUnderscore() {
        Assertions.assertDoesNotThrow(() -> {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data-test/vehicle.yml")) {
                final YamlReader yamlReader = new YamlReader(inputStream);
                {
                    final long longVal1 = yamlReader.getProperty("numeric_value.long_value", Long.class);
                    getLogger(this).info("long value 1 = %s".formatted(longVal1));
                    assertEquals(900_000, longVal1);
                }
                {
                    final long longVal2 = yamlReader.getProperty("numeric_value.long_value2", Long.class);
                    getLogger(this).info("long value 2 = %s".formatted(longVal2));
                    assertEquals(900_000, longVal2);
                }
                {
                    final double doubleVal1 = yamlReader.getProperty("numeric_value.double_value", Double.class);
                    getLogger(this).info("double value 1 = %s".formatted(doubleVal1));
                    assertEquals(8009.001, doubleVal1);
                }
                {
                    final double doubleVal2 = yamlReader.getProperty("numeric_value.double_value2", Double.class);
                    getLogger(this).info("double value 2 = %s".formatted(doubleVal2));
                    assertEquals(8009.001, doubleVal2);
                }
            }
        });
    }

    @Test
    void testWithPrimitiveValue() {
        Assertions.assertDoesNotThrow(() -> {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data-test/vehicle.yml")) {
                final YamlReader yamlReader = new YamlReader(inputStream);

                {
                    final long longVal = yamlReader.getProperty("numeric_value.long_value", long.class);
                    getLogger(this).info("long val with primitive type: %s".formatted(longVal));
                    assertEquals(900_000, longVal);
                }
                {
                    final Long clzLongVal = yamlReader.getProperty("numeric_value.long_value", Long.class);
                    getLogger(this).info("long val with class type: %s".formatted(clzLongVal));
                    assertEquals(900_000, clzLongVal);
                }

                ///////////////////////////////////
                {
                    final int intVal_ = yamlReader.getProperty("numeric_value.long_value2", int.class);
                    getLogger(this).info("int val with class type: %s".formatted(intVal_));
                    assertEquals(900_000, intVal_);
                }

                {
                    final Integer clzIntegerVal = yamlReader.getProperty("numeric_value.long_value2", Integer.class);
                    getLogger(this).info("int val with class type: %s".formatted(clzIntegerVal));
                    assertEquals(900_000, clzIntegerVal);
                }

                ////////////////////////////////////
                {
                    final float floatVal_ = yamlReader.getProperty("numeric_value.double_value", float.class);
                    getLogger(this).info("float val with class type: %s".formatted(floatVal_));
                    assertEquals(8009.001f, floatVal_);
                }

                {
                    final Float clzFloatVal = yamlReader.getProperty("numeric_value.double_value", Float.class);
                    getLogger(this).info("float val with class type: %s".formatted(clzFloatVal));
                    assertEquals(8009.001f, clzFloatVal);
                }

                ////////////////////////////////////
                {
                    final double doubleVal_ = yamlReader.getProperty("numeric_value.double_value2", double.class);
                    getLogger(this).info("double val with class type: %s".formatted(doubleVal_));
                    assertEquals(8009.001, doubleVal_);
                }

                {
                    final Double clzDoubleVal = yamlReader.getProperty("numeric_value.double_value2", Double.class);
                    getLogger(this).info("double val with class type: %s".formatted(clzDoubleVal));
                    assertEquals(8009.001, clzDoubleVal);
                }
            }
        });
    }

    @Test
    void testWithBoolean() {
        Assertions.assertDoesNotThrow(() -> {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data-test/vehicle.yml")) {
                final YamlReader yamlReader = new YamlReader(inputStream);

                {
                    final Boolean clzTrueVal = yamlReader.getProperty("binary_value.T", Boolean.class);
                    getLogger(this).info("boolean val with class type: %s".formatted(clzTrueVal));
                    assertEquals(true, clzTrueVal);
                }

                {
                    final boolean trueVal = yamlReader.getProperty("binary_value.T", boolean.class);
                    getLogger(this).info("boolean val with primitive type: %s".formatted(trueVal));
                    assertTrue(trueVal);
                }

                //////////////////////////////////////////
                {
                    final Boolean clzFalseVal = yamlReader.getProperty("binary_value.F", Boolean.class);
                    getLogger(this).info("boolean val with class type: %s".formatted(clzFalseVal));
                    assertEquals(false, clzFalseVal);
                }

                {
                    final boolean falseVal = yamlReader.getProperty("binary_value.F", boolean.class);
                    getLogger(this).info("boolean val with primitive type: %s".formatted(falseVal));
                    assertFalse(falseVal);
                }
            }
        });
    }

    @Test
    void testWithReferenceProperty() {
        Assertions.assertDoesNotThrow(() -> {
            try (final InputStream vehicleStream = getClass().getClassLoader().getResourceAsStream("data-test/vehicle.yml")) {
                final YamlReader yamlReader = new YamlReader(vehicleStream);

                {
                    Object carBrand = yamlReader.getProperty("car.brand", Object.class);
                    getLogger(this).info("brand of car: %s".formatted(carBrand));
                    assertNotNull(carBrand);
                    assertTrue(Map.class.isAssignableFrom(carBrand.getClass()));
                }

                {
                    Object bicycleBrand = yamlReader.getProperty("bicycle.brand", Object.class);
                    getLogger(this).info("brand of bicycle: %s".formatted(bicycleBrand));
                    assertNotNull(bicycleBrand);
                    assertTrue(Map.class.isAssignableFrom(bicycleBrand.getClass()));
                }

                {
                    String carBrandCountry = yamlReader.getProperty("car.brand.country", String.class);
                    getLogger(this).info("brand country of car: %s".formatted(carBrandCountry));
                    assertNotNull(carBrandCountry);
                    assertEquals("USA", carBrandCountry);
                    //////
                    String carBrandName = yamlReader.getProperty("car.brand.name", String.class);
                    getLogger(this).info("brand name of car: %s".formatted(carBrandName));
                    assertNotNull(carBrandName);
                    assertEquals("BMW", carBrandName);
                }

                {
                    String bicycleBrandCountry = yamlReader.getProperty("bicycle.brand.country", String.class);
                    getLogger(this).info("brand country of bicycle: %s".formatted(bicycleBrandCountry));
                    assertNotNull(bicycleBrandCountry);
                    assertEquals("Japan", bicycleBrandCountry);
                    //////
                    String bicycleBrandName = yamlReader.getProperty("bicycle.brand.name", String.class);
                    getLogger(this).info("brand name of bicycle: %s".formatted(bicycleBrandName));
                    assertNotNull(bicycleBrandName);
                    assertEquals("Honda", bicycleBrandName);
                }
            }
        });
    }

    @Test
    void testNotFoundKeyProperty() {
        Assertions.assertDoesNotThrow(() -> {
            try (final InputStream vehicleStream = getClass().getClassLoader().getResourceAsStream("data-test/vehicle.yml")) {
                final YamlReader yamlReader = new YamlReader(vehicleStream);

                {
                    final int wheelCount = yamlReader.getProperty("car.wheel", int.class);
                    assertEquals(4, wheelCount);
                }

                Assertions.assertThrows(IOException.class, () -> {
                    final String color2_ = yamlReader.getProperty("car.color2", String.class);
                });
            }
        });
    }

}