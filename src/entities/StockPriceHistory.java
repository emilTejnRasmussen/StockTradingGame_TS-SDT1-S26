package entities;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

public record StockPriceHistory(UUID id, String stockSymbol, BigDecimal price, LocalDateTime timeStamp){}
