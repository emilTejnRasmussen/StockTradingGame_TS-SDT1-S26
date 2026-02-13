package shared.logging;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleLogOutput implements LogOutput
{
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm:ss");

    private final String RED = "\u001B[31m";
    private final String YELLOW = "\u001B[33m";
    private final String RESET = "\u001B[0m";

    @Override
    public void log(LogLevel level, String message)
    {
        String color = getColor(level);
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.printf("%s[%s] [%s] %s%s%n", color, timestamp, level, message, RESET);
    }

    private String getColor(LogLevel level){
        return switch (level){
            case WARNING -> YELLOW;
            case ERROR -> RED;
            default -> RESET;
        };
    }
}
