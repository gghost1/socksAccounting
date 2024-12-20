package org.example.sokcsaccounting.data.type;

public enum OrderType {
    ASCENDING,
    DESCENDING,
    UNORDERED;

    public static OrderType fromString(String sort) {
        if (sort == null) {
            return UNORDERED;
        }
        return switch (sort.toLowerCase().trim()) {
            case "ascend" -> ASCENDING;
            case "descend" -> DESCENDING;
            default -> throw new IllegalArgumentException("Invalid sort: " + sort);
        };
    }
}
