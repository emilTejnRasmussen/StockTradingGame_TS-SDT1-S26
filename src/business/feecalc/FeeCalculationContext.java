package business.feecalc;

import java.math.BigDecimal;

public class FeeCalculationContext
{
    private FeeCalculationStrategy strategy;

    public FeeCalculationContext(FeeCalculationStrategy strategy)
    {
        this.strategy = strategy;
    }

    public void setStrategy(FeeCalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculateFee(BigDecimal amount){
        return strategy.calculateFee(amount);
    }
}
