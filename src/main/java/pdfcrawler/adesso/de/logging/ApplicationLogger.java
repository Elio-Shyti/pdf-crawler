package pdfcrawler.adesso.de.logging;

import pdfcrawler.adesso.de.gui.FrameFactory;
import pdfcrawler.adesso.de.utilities.Config;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ApplicationLogger {

    private static final DateTimeFormatter appLogFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
    private static String logFilename = "applogs.log";
    static {
        logFilename = getCurrentTime(appLogFormatter) + logFilename;
    }
    private static String outputFile;
    private static PrintWriter writer;

    public static void log(String msg) {
        noFormattingLog(msg + Config.ls);
    }

    public static void noFormattingLog(String msg) {
        // Append text to logs area (GUI).
        FrameFactory.getLogsArea().append(msg);

        if (outputFile == null) {
            LoggingService.log("ApplicationLogger.outpufile ist nicht initializiert.");
            return;
        }

        if (writer == null) {
            try {
                writer = new PrintWriter(outputFile);
            } catch (FileNotFoundException e) {
                LoggingService.addExceptionToLog(e, true);
            }
        }

        writer.append(msg);
        writer.flush();
    }

    public static void close() {
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }

    public static String getOutputFile() {
        return outputFile;
    }

    public static void setOutputFile(String outputFile) {
        // Add File.separator at the end of directory path.
        ApplicationLogger.outputFile = (outputFile.lastIndexOf(File.separator) + 1) == outputFile.length() ?
                outputFile :
                outputFile + File.separator;
        new File(ApplicationLogger.outputFile).mkdir();

        ApplicationLogger.outputFile += logFilename;

        // If outputfile changes, then the writer should be closed.
        close();
    }

    private static String getCurrentTime(DateTimeFormatter formatter) {
        return formatter.format(LocalDateTime.now()) + " ";
    }
}
