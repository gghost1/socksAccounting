package org.example.sokcsaccounting.dto;

import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
public class SocksEditDto {
        @NotEmpty
        private String color;

        @NotNull
        @Min(0)
        @Max(100)
        private Double cottonPart;

        @NotNull
        @Min(0)
        private Integer quantity;

        public SocksEditDto() {
        }

        public SocksEditDto(String color, Double cottonPart, Integer quantity) {
                this.color = color;
                this.cottonPart = cottonPart;
                this.quantity = quantity;
        }

        public SocksDto to(UUID id) {
                return new SocksDto(id, color, cottonPart, quantity);
        }
}