package shared.logging;

public class Logger
{
    private static volatile Logger instance;
    private volatile LogOutput output;

    private Logger() {
        this.output = new ConsoleLogOutput();
    }

    public static Logger getInstance() {
        Logger result = instance;
        if (result == null){
            synchronized (Logger.class){
                result = instance;
                if (result == null){
                    instance = result = new Logger();
                }
            }
        }

        return result;
    }

    public void log(LogLevel level, String message){
        this.output.log(level, message);
    }

    public void info(String message){
        this.output.log(LogLevel.INFO, message);
    }

    public void warning(String message){
        this.output.log(LogLevel.WARNING, message);
    }

    public void error(String message){
        this.output.log(LogLevel.ERROR, message);
    }

    public void setOutput(LogOutput output) {
        if (output == null) return;
        this.output = output;
    }
}
