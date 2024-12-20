package org.example.sokcsaccounting.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SocksEditDtoTest {

    @Test
    void creationTest() {
        SocksEditDto socksEditDto = new SocksEditDto(
                "black",
                0.9,
                10
        );

        assertEquals("black", socksEditDto.getColor());
        assertEquals(0.9, socksEditDto.getCottonPart());
        assertEquals(10, socksEditDto.getQuantity());
    }

    @Test
    void conversionTest() {
        SocksEditDto socksEditDto = new SocksEditDto(
                "black",
                0.9,
                10
        );
        UUID id = UUID.randomUUID();
        SocksDto socksDto = socksEditDto.to(id);

        assertEquals(id, socksDto.getId());
        assertEquals("black", socksDto.getColor());
        assertEquals(0.9, socksDto.getCottonPart());
        assertEquals(10, socksDto.getQuantity());
    }

}
