package org.example.sokcsaccounting.data.type;

import lombok.Getter;

@Getter
public enum SocksParameterType {
    COLOR("color"),
    COTTON_PART("cottonPart"),
    QUANTITY("quantity");

    private final String value;

    SocksParameterType(String color) {
        this.value = color;
    }

    public static SocksParameterType fromString(String parameter) {
        return switch (parameter.toLowerCase().trim()) {
            case "color" -> COLOR;
            case "cottonpart" -> COTTON_PART;
            case "quantity" -> QUANTITY;
            default -> throw new IllegalArgumentException("Invalid parameter: " + parameter);
        };
    }
}
