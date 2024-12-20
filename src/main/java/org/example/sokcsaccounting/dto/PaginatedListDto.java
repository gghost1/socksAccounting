package org.example.sokcsaccounting.dto;

import java.util.List;

public record PaginatedListDto<T>(
    List<T> socks,
    long totalCount
) {
}
