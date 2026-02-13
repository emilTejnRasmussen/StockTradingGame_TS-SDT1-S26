import shared.logging.LogLevel;
import shared.logging.LogOutput;
import shared.logging.Logger;

void main() {
    Logger logger = Logger.getInstance();

    // Test default console output
    System.out.println("=== Console logging ===");
    logger.info("Application started");
    logger.warning("Low balance warning");
    logger.error("Connection failed");

    // Test generic log method
    System.out.println("\n=== Direct log(LogLevel, message) ===");
    logger.log(LogLevel.INFO, "Info via generic log()");
    logger.log(LogLevel.WARNING, "Warning via generic log()");
    logger.log(LogLevel.ERROR, "Error via generic log()");

    // Test switching output
    System.out.println("\n=== Switching to custom test output ===");
    logger.setOutput(new LogOutput() {
        @Override
        public void log(LogLevel level, String message) {
            System.out.println("CUSTOM OUTPUT -> [" + level + "] " + message);
        }
    });

    logger.info("Now using custom output");
    logger.warning("Custom warning");
    logger.error("Custom error");

    // Test null protection
    System.out.println("\n=== Testing null output protection ===");
    logger.setOutput(null); // should do nothing
    logger.info("Logger should still work after null setOutput");

    System.out.println("\n=== Logger test complete ===");
}