package org.example.sokcsaccounting.dto;

import org.example.sokcsaccounting.data.type.OrderType;
import org.example.sokcsaccounting.data.type.SocksParameterType;

public record SocksSortDto(
    SocksParameterType parameter,
    OrderType orderType
) {
    public static SocksSortDto of(SocksParameterType parameter, OrderType orderType) {
        if (orderType == null) {
            return new SocksSortDto(
                null,
                OrderType.UNORDERED
            );
        }
        return new SocksSortDto(
            parameter,
            orderType
        );
    }
}
