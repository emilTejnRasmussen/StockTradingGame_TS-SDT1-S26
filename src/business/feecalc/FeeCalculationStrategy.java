package business.feecalc;

import java.math.BigDecimal;

public interface FeeCalculationStrategy
{
    double calculateFee(BigDecimal amount);
}
