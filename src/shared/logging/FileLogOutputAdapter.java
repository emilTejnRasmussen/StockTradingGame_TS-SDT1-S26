package shared.logging;

import provided.FileLogOutputter;

public class FileLogOutputAdapter implements LogOutput
{
    private final FileLogOutputter fileLogOutputter;

    public FileLogOutputAdapter(FileLogOutputter fileLogOutputter)
    {
        this.fileLogOutputter = fileLogOutputter;
    }


    @Override
    public void log(LogLevel level, String message)
    {
        switch (level){
            case INFO -> fileLogOutputter.logInfo(message);
            case WARNING -> fileLogOutputter.logWarning(message);
            case ERROR -> fileLogOutputter.logError(message);
        }
    }
}
