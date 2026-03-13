package business.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BuyStockDTO(
        String stockSymbol,
        UUID portfolioID,
        int quantity,
        BigDecimal playerBalance
)
{
}
