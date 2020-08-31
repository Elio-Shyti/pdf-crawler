package pdfcrawler.adesso.de.csv;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CSVErrorStatus {
    public static Set<String> selectedDocuments = new HashSet<>();
    // Documents that will be read.
    public static Set<String> readDocuments = new HashSet<>();
    // Documents that will be read with errors.
    public static Set<String> documentsWithErrors = new HashSet<>();
    // Document that will be read successfully.
    public static Set<String> documentsWithoutErrors = new HashSet<>();
    // Documents that will not be read at all.
    public static Set<String> notReadDocuments = new HashSet<>();
    // Duplicate data
    public static Map<String, Integer> duplicateData = new HashMap<>();

    public static void addReadSuccess(String documentName) {
        readDocuments.add(documentName);
        documentsWithoutErrors.add(documentName);
    }

    public static void addReadError(String documentName) {
        readDocuments.add(documentName);
        documentsWithErrors.add(documentName);
    }

    public static void resetCounters() {
        selectedDocuments = new HashSet<>();
        readDocuments = new HashSet<>();
        notReadDocuments = new HashSet<>();
        documentsWithErrors = new HashSet<>();
        documentsWithoutErrors = new HashSet<>();
    }

    public static void addDuplicateData(String name) {
        duplicateData.merge(name, 1, Integer::sum);
    }

}
