import shared.configuration.AppConfig;

void main() {
    for (int i = 0; i < 100; i++)
    {
        int base = AppConfig.getInstance().getUpdateFrequencyInMs();
        int variance = AppConfig.getInstance().getUpdateMaxVarianceInMs();

        int randomFrequency = base + ThreadLocalRandom.current().nextInt(-variance, variance + 1);
        System.out.println(randomFrequency);
    }
}