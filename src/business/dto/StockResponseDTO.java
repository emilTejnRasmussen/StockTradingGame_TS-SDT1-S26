package business.dto;

import entities.Stock;

import java.math.BigDecimal;

public record StockResponseDTO(
        String symbol,
        String name,
        BigDecimal currentPrice,
        Stock.State currentState
){}
