package app.netlify.nmhillusion.n2mix.type.function;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapFunctionFromListTest {

    @Test
    void testFromStringList() {
        assertEquals(Arrays.asList("abc", "a", "45effectiveefficient", "bc", "e", "def"),
                Stream.of("Abc", "   a", "45EffectiveEfficient", "       bc", "  e ", "DeF")
                        .map(new MapFunctionFromList<String>()
                                .add(String::trim)
                                .add(String::toLowerCase)
                                .combine()
                        )
                        .collect(Collectors.toList()));

    }

    @Test
    void testWithNumber() {
        assertEquals(Arrays.asList(12, 16, 18, 20, 22),
                Stream.of(4, 6, 7, 8, 9)
                        .map(new MapFunctionFromList<Integer>()
                                .add(n -> n + 2)
                                .add(n -> n * 2)
                                .combine()
                        )
                        .collect(Collectors.toList())
        );
    }
}