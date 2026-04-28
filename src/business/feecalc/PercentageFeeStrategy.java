package business.feecalc;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentageFeeStrategy implements FeeCalculationStrategy
{
    @Override
    public double calculateFee(BigDecimal amount)
    {
        return amount.multiply(BigDecimal.valueOf(0.05)).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }
}
