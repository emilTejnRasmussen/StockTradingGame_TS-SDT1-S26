package shared.configuration;

public class AppConfig
{
    private static AppConfig instance;

    private final int startingBalance;
    private final int updateFrequencyInMs;
    private final double transactionFee;
    private final double stockResetValue;

    private AppConfig(){
        this.startingBalance = 20000;
        this.updateFrequencyInMs = 1000;
        this.transactionFee = 250;
        this.stockResetValue = 750;
    }

    public static AppConfig getInstance(){
        if (instance == null){
            instance = new AppConfig();
        }
        return instance;
    }

    public int getStartingBalance()
    {
        return startingBalance;
    }

    public int getUpdateFrequencyInMs()
    {
        return updateFrequencyInMs;
    }

    public double getTransactionFee()
    {
        return transactionFee;
    }

    public double getStockResetValue()
    {
        return stockResetValue;
    }
}
