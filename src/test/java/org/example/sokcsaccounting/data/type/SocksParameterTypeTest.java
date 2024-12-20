package org.example.sokcsaccounting.data.type;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SocksParameterTypeTest {

    @ParameterizedTest
    @MethodSource("positiveData")
    void creationPositiveTest(String parameter) {
        SocksParameterType socksParameterType = SocksParameterType.fromString(parameter);

        assertEquals(parameter, socksParameterType.getValue());
    }

    @Test
    void creationNegativeTest() {
        assertThrows(IllegalArgumentException.class, () -> SocksParameterType.fromString("unknown"));
    }

    private static Stream<String> positiveData() {
        return Stream.of(
                "color",
                "cottonPart",
                "quantity"
        );
    }

}
