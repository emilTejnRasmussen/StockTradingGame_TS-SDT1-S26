package business.dto.transaction;

import java.util.UUID;

public record SellStockRequestDTO(
        String stockSymbol,
        UUID portfolioId,
        int quantity
) implements StockTransactionRequest{}
