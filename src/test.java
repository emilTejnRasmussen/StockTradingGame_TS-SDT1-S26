import shared.configuration.AppConfig;

void main() {
    Random random = new Random();

    BigDecimal value = AppConfig.getInstance().getStockResetValue();
    for (int i = 0; i < 100; i++)
    {
        double change = ((random.nextDouble() * 2 - 1) / 10) + 1;
        value = value.multiply(BigDecimal.valueOf(change));

        System.out.println(value);
    }
}