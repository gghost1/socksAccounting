package org.example.sokcsaccounting.data.type;

public enum ComparisonOperatorType {
    GREATER,
    LESS,
    EQUAL,
    BETWEEN;

    public static ComparisonOperatorType fromString(String operator) {
        return switch (operator.toLowerCase().trim()) {
            case "morethan" -> GREATER;
            case "lessthan" -> LESS;
            case "equal" -> EQUAL;
            case "between" -> BETWEEN;
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }
}
