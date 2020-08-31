package pdfcrawler.adesso.de.utilities;

import pdfcrawler.adesso.de.csv.CSVErrorStatus;
import pdfcrawler.adesso.de.exception.DuplicateDataException;
import pdfcrawler.adesso.de.exception.ErroneousExtractedDataException;

import java.util.HashMap;
import java.util.Map;

public class Helper {
    // Check Data uniqueness.
    static public void checkDataUniqueness(Map<String, String> data, Map<String, String> from) throws DuplicateDataException {
        if (data.entrySet().isEmpty() || data == null || from == null) {
            return;
        }

        if (from.get(data.entrySet().iterator().next().getKey()) != null) {
            CSVErrorStatus.addDuplicateData(data.keySet().stream().findFirst().orElse(""));
            throw new DuplicateDataException(String.format("Der Name:[%s] wurde mehr als einmal gefunden.", data.keySet().stream().findFirst().orElse("")));
        }
    }

    // Check if data are extracted correctly.
    static public void checkDataCorrectness(String fileAbsolutePath, HashMap<String, String> readData) throws ErroneousExtractedDataException {
        if (readData.isEmpty()) {
            throw new ErroneousExtractedDataException(
                    String.format("Daten konnten nicht extrahiert werden. Datei:[%s]", fileAbsolutePath)
            );
        } else if (readData.keySet().stream().findFirst().orElse("").isBlank() ||
                readData.values().stream().findFirst().orElse("").isBlank()) {
            throw new ErroneousExtractedDataException(
                    String.format("Die eingelesene Daten sind fehlerhaft. Datei:[%s] | Name[%s]:Datum[%s]",
                            fileAbsolutePath,
                            readData.keySet().stream().findFirst().orElse(""),
                            readData.values().stream().findFirst().orElse("")
                    )
            );
        }
    }
}
