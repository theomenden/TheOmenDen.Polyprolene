package theomenden.polyprolene.utils;

import org.apache.logging.log4j.core.appender.rolling.action.IfAll;
import org.slf4j.LoggerFactory;
import theomenden.polyprolene.client.PolyproleneClient;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class LoggerUtils {
    private LoggerUtils() {
    }

    public static Logger getLoggerInstance() {
        var logger = Logger.getLogger(PolyproleneClient.MODID);
        var handler = new ConsoleHandler();
        handler.setFormatter(new LogFormat());
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        return logger;
    }

    private static class LogFormat extends Formatter {
        @Override
        public String format(LogRecord record) {
            var currentZonedTime = ZonedDateTime.ofInstant(record.getInstant(), ZoneId.systemDefault());
            var timings = "[" + currentZonedTime.format(DateTimeFormatter.ISO_LOCAL_TIME) +"]";
            var recordingInformation = "[" + PolyproleneClient.MODID + ">" + record.getLevel().getLocalizedName() + " " + record.getSourceClassName() + "]:";
            var message = record.getMessage() + "\n";

            return timings + recordingInformation + message;
        }
    }

}
