package com.coffeemap.coffeemap.dto;

import java.util.List;

public record CafeListResponse(
        List<CafeSummary> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}