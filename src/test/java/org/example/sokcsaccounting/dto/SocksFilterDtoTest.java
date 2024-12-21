package org.example.sokcsaccounting.dto;

import org.example.sokcsaccounting.data.type.ComparisonOperatorType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SocksFilterDtoTest {

    @Test
    void creationTest() {
        SocksFilterDto socksFilterDto = SocksFilterDto.of(
                "red",
                ComparisonOperatorType.BETWEEN,
                List.of(1.0, 2.0)
        );
        assertEquals("red", socksFilterDto.color());
        assertEquals(ComparisonOperatorType.BETWEEN, socksFilterDto.comparisonOperatorType());
        assertEquals(List.of(1.0, 2.0), socksFilterDto.parameter());
    }

}
