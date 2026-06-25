package com.example.stockmap.dto;

import java.math.BigDecimal;
import java.util.List;

public record StockOhlcResponse(
        Long stockId,
        String symbol,
        String companyName,
        BigDecimal marketCap,
        String industry,
        BigDecimal shareOutstanding,
        String logo,
        List<OhlcDto> ohlcs
) {
}
