package business.dto.transaction;

import java.util.UUID;

public record BuyStockRequestDTO(
        String stockSymbol,
        UUID portfolioId,
        int quantity
) implements StockTransactionRequest{}
