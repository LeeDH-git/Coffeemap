package com.coffeemap.coffeemap.dto;

import java.time.Instant;
import java.util.UUID;

public record CafeSummary(
        UUID id,
        String name,
        String memo,
        double lat,
        double lng,
        double rating,
        Instant createdAt
) {}