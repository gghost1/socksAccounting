package org.example.sokcsaccounting.data.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderTypeTest {

    @Test
    void creationPositiveTest() {
        OrderType orderTypeAsc = OrderType.fromString("ascend");
        OrderType orderTypeDesc = OrderType.fromString("descend");
        OrderType orderTypeUnordered = OrderType.fromString(null);

        assertEquals(OrderType.UNORDERED, orderTypeUnordered);
        assertEquals(OrderType.ASCENDING, orderTypeAsc);
        assertEquals(OrderType.DESCENDING, orderTypeDesc);
    }

    @Test
    void creationNegativeTest() {
        assertThrows(IllegalArgumentException.class, () -> OrderType.fromString("unknown"));
    }

}
