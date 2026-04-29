package business.feecalc;

import java.math.BigDecimal;

public class FlatFeeStrategy implements FeeCalculationStrategy
{
    @Override
    public double calculateFee(BigDecimal amount)
    {
        return 25.0;
    }
}
