package org.example.sokcsaccounting.data;

import org.example.sokcsaccounting.exception.IllegalOperationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.util.Pair;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SocksTest {

    @Test
    void creationPositiveTest() {
        Socks socks = Socks.of(
            "black",
            0.9,
            10
        );

        assertEquals("black", socks.getColor());
        assertEquals(0.9, socks.getCottonPart());
        assertEquals(10, socks.getQuantity());
    }

    @ParameterizedTest
    @MethodSource("negativeData")
    void creationNegativeTest(Pair<Double, Integer> negativeData) {
        assertThrows(IllegalArgumentException.class, () -> Socks.of(
                "black",
                negativeData.getFirst(),
                negativeData.getSecond()
        ));
    }

    @Test
    void increaseQuantityTest() {
        Socks socks = Socks.of(
                "black",
                0.9,
                10
        );

        socks.increaseQuantity(5);
        assertEquals(15, socks.getQuantity());
        assertEquals("black", socks.getColor());
        assertEquals(0.9, socks.getCottonPart());

        assertThrows(IllegalArgumentException.class, () -> socks.increaseQuantity(-5));
    }

    @Test
    void decreaseQuantityTest() {
        Socks socks = Socks.of(
                "black",
                0.9,
                10
        );

        socks.decreaseQuantity(5);
        assertEquals(5, socks.getQuantity());
        assertEquals("black", socks.getColor());
        assertEquals(0.9, socks.getCottonPart());

        assertThrows(IllegalArgumentException.class, () -> socks.decreaseQuantity(-5));
        assertThrows(IllegalOperationException.class, () -> socks.decreaseQuantity(6));
    }

    @Test
    void updateTest() {
        Socks socks = Socks.of(
                "black",
                0.9,
                10
        );

        socks.update("white", 0.8, 5);
        assertEquals("white", socks.getColor());
        assertEquals(0.8, socks.getCottonPart());
        assertEquals(5, socks.getQuantity());
    }

    private static Stream<Pair<Double, Integer>> negativeData() {
        return Stream.of(
                Pair.of(-1.9, 5),
                Pair.of(0.9, -5),
                Pair.of(101.0, 5)
        );
    }

}
