package business.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PortfolioHistoryDTO(
        LocalDateTime timeStamp,
        BigDecimal runningBalance
)
{}
