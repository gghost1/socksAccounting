package org.example.sokcsaccounting.dto;

import org.example.sokcsaccounting.data.type.ComparisonOperatorType;

import java.util.List;

public record SocksFilterDto(
    String color,
    ComparisonOperatorType comparisonOperatorType,
    List<Double> parameter
) {
    public static SocksFilterDto of(String color, String comparisonOperator, List<Double> parameter) {
        ComparisonOperatorType comparisonOperatorType = ComparisonOperatorType.fromString(comparisonOperator);
        if (comparisonOperatorType == ComparisonOperatorType.BETWEEN) {
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
                comparisonOperatorType,
                parameter
        );
    }
}
