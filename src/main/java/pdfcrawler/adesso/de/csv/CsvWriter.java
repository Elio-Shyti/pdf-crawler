package pdfcrawler.adesso.de.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import pdfcrawler.adesso.de.logging.LoggingService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class CsvWriter {
    private final String SUFFIX = ".csv";
    final String[] headers = {"Name", "Datum"};

    public void createCsv(Map<String, String> data, File outputDirectory) throws IOException {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        String dateTime = formatter.format(new Date());

        String filePath = outputDirectory.getAbsolutePath();
        // Make sure it contains File.separator at the end of the path.
        filePath = !filePath.substring(filePath.length() -1).equals(File.separator) ?  filePath + File.separator : filePath;
        // Add date and suffix as file name.
        filePath = filePath.concat(dateTime).concat(SUFFIX);

        try (FileWriter out = new FileWriter(filePath);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withDelimiter(';').withHeader(headers))
        ) {
            LoggingService.log(String.format("Ausgabedatei wurde angelegt: %s", new File(filePath).getAbsolutePath()), true);

            data.forEach((name, date) -> {
                try {
                    printer.printRecord(name, date);
                } catch (IOException e) {
                    LoggingService.addExceptionToLog(e, true);
                }
            });
        }
    }
}
