package business.dto;

import entities.Stock;

import java.math.BigDecimal;

public record StockDTO(String symbol, BigDecimal currentPrice, Stock.State currentState)
{
}
