package com.example.stockmap.dto;

import java.math.BigDecimal;

public record HeatmapStockDto(
        Long stockId,
        String symbol,
        String name,
        BigDecimal price,
        BigDecimal marketPriceChg,
        BigDecimal marketPriceChgPct,
        BigDecimal marketCap,
        String industry,
        String ipoDate,
        String webUrl,
        BigDecimal shareOutstanding,
        String logo
) {
}
