package presentation.views.utility;

import java.math.BigDecimal;

public class PortfolioInputParser
{
    public static BigDecimal parseMoney(String text)
    {
        if (text == null || text.isBlank()) return BigDecimal.ZERO;

        return new BigDecimal(
                text.replace("¤", "")
                        .trim()
        );
    }

    public static int parseOwned(String text)
    {
        if (text == null || text.isBlank()) return 0;
        return Integer.parseInt(text.trim());
    }


}
