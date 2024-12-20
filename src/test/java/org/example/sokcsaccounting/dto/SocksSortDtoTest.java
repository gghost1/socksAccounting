package org.example.sokcsaccounting.dto;

import org.example.sokcsaccounting.data.type.OrderType;
import org.example.sokcsaccounting.data.type.SocksParameterType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SocksSortDtoTest {

    @Test
    void creationWithDataTest() {
        SocksSortDto socksSortDto = SocksSortDto.of("color", "ascend");

        assertEquals(SocksParameterType.COLOR, socksSortDto.parameter());
        assertEquals(OrderType.ASCENDING, socksSortDto.orderType());
    }

    @Test
    void creationWithoutDataTest() {
        SocksSortDto socksSortDto = SocksSortDto.of(null, null);

        assertNull(socksSortDto.parameter());
        assertEquals(OrderType.UNORDERED, socksSortDto.orderType());
    }


}
