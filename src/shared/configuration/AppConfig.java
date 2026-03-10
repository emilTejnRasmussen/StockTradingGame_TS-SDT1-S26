package shared.configuration;

import java.math.BigDecimal;

public class AppConfig
{
    private static AppConfig instance;

    private final BigDecimal startingBalance;
    private final int updateFrequencyInMs;
    private final int updateMaxVarianceInMs;
    private final BigDecimal transactionFee;
    private final BigDecimal stockResetValue;
    private final int bankruptTimeInTicks;
    private final BigDecimal bankruptcyThreshold;

    private AppConfig(){
        this.startingBalance = BigDecimal.valueOf(1500);
        this.updateFrequencyInMs = 1000;
        this.updateMaxVarianceInMs = updateFrequencyInMs / 2;
        this.transactionFee = BigDecimal.valueOf(25);
        this.stockResetValue = BigDecimal.valueOf(100);
        this.bankruptTimeInTicks = 5;
        this.bankruptcyThreshold = new BigDecimal("0.01");
    }

    public static AppConfig getInstance(){
        if (instance == null){
            instance = new AppConfig();
        }
        return instance;
    }

    public BigDecimal getStartingBalance()
    {
        return startingBalance;
    }

    public int getUpdateFrequencyInMs()
    {
        return updateFrequencyInMs;
    }

    public BigDecimal getTransactionFee()
    {
        return transactionFee;
    }

    public BigDecimal getStockResetValue()
    {
        return stockResetValue;
    }

    public int getBankruptTimeInTicks()
    {
        return bankruptTimeInTicks;
    }

    public int getUpdateMaxVarianceInMs()
    {
        return updateMaxVarianceInMs;
    }

    public BigDecimal getBankruptcyThreshold()
    {
        return bankruptcyThreshold;
    }
}
