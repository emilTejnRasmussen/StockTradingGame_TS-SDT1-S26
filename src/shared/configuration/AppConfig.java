package shared.configuration;

import java.math.BigDecimal;

public class AppConfig
{
    private static AppConfig instance;

    private final BigDecimal startingBalance;
    private final int updateFrequencyInMs;
    private final BigDecimal transactionFee;
    private final BigDecimal stockResetValue;
    private final int bankruptTimeInTicks;

    private AppConfig(){
        this.startingBalance = BigDecimal.valueOf(1500);
        this.updateFrequencyInMs = 1000;
        this.transactionFee = BigDecimal.valueOf(25);
        this.stockResetValue = BigDecimal.valueOf(100);
        this.bankruptTimeInTicks = 5;
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
}
