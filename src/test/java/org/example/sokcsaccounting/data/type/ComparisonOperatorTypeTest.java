package org.example.sokcsaccounting.data.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComparisonOperatorTypeTest {

    @Test
    void creationPositiveTest() {
        ComparisonOperatorType greater = ComparisonOperatorType.fromString("morethan");
        ComparisonOperatorType less = ComparisonOperatorType.fromString("lessthan");
        ComparisonOperatorType equal = ComparisonOperatorType.fromString("equal");
        ComparisonOperatorType between = ComparisonOperatorType.fromString("between");

        assertEquals(ComparisonOperatorType.GREATER, greater);
        assertEquals(ComparisonOperatorType.LESS, less);
        assertEquals(ComparisonOperatorType.EQUAL, equal);
        assertEquals(ComparisonOperatorType.BETWEEN, between);
    }

    @Test
    void creationNegativeTest() {
        assertThrows(IllegalArgumentException.class, () -> ComparisonOperatorType.fromString("unknown"));
    }

}
