package org.example.sokcsaccounting.dto;

import org.example.sokcsaccounting.data.type.ComparisonOperatorType;

import java.util.List;

public record SocksFilterDto(
    String color,
    ComparisonOperatorType comparisonOperatorType,
    List<Double> parameter
) {
    public static SocksFilterDto of(String color, ComparisonOperatorType comparisonOperator, List<Double> parameter) {
        if (comparisonOperator == ComparisonOperatorType.BETWEEN) {
            if (parameter.size() != 2) {
                throw new IllegalArgumentException("Invalid count of parameters");
            }
        } else {
            if (parameter.size() != 1) {
                throw new IllegalArgumentException("Invalid count of parameters");
            }
        }
        return new SocksFilterDto(
                color,
                comparisonOperator,
                parameter
        );
    }
}
