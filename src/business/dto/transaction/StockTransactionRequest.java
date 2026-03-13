package business.dto.transaction;

import java.util.UUID;

public interface StockTransactionRequest
{
    String stockSymbol();
    UUID portfolioId();
    int quantity();
}
