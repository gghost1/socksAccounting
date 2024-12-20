package org.example.sokcsaccounting.dto;

import lombok.Getter;
import org.example.sokcsaccounting.data.Socks;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
public class SocksDto {
    @NotNull
    private UUID id;

    @NotEmpty
    private String color;

    @NotNull
    @Min(0)
    @Max(100)
    private Double cottonPart;

    @NotNull
    @Min(0)
    private Integer quantity;

    public SocksDto() {
    }

    public SocksDto(UUID id, String color, Double cottonPart, Integer quantity) {
        this.id = id;
        this.color = color;
        this.cottonPart = cottonPart;
        this.quantity = quantity;
    }

    public static SocksDto from(Socks socks) {
        return new SocksDto(
                socks.getId(),
                socks.getColor(),
                socks.getCottonPart(),
                socks.getQuantity()
        );
    }

}
