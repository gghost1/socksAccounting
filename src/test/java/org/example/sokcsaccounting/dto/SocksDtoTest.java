package org.example.sokcsaccounting.dto;

import org.example.sokcsaccounting.data.Socks;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SocksDtoTest {

    @Test
    void creationTest() {
        UUID id = UUID.randomUUID();
        SocksDto socksDto = new SocksDto(
                id,
                "black",
                0.9,
                10
        );

        assertEquals(id, socksDto.getId());
        assertEquals("black", socksDto.getColor());
        assertEquals(0.9, socksDto.getCottonPart());
        assertEquals(10, socksDto.getQuantity());
    }

}
