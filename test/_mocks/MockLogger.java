package _mocks;

import shared.logging.LogLevel;
import shared.logging.Logger;

public class MockLogger extends Logger
{
    @Override
    public void log(LogLevel level, String message)
    {
    }

    @Override
    public void info(String message)
    {
    }

    @Override
    public void warning(String message)
    {
    }

    @Override
    public void error(String message)
    {
    }
}
