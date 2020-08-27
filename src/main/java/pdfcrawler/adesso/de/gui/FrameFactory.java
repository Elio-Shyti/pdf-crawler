package pdfcrawler.adesso.de.gui;

import pdfcrawler.adesso.de.csv.CSVErrorStatus;
import pdfcrawler.adesso.de.csv.CsvWriter;
import pdfcrawler.adesso.de.PdfScanner;
import pdfcrawler.adesso.de.logging.ApplicationLogger;
import pdfcrawler.adesso.de.logging.LoggingService;
import pdfcrawler.adesso.de.utilities.Config;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FrameFactory {
    public static final String ICONS_BROWSE_PNG = "/icons/browse.png";
    public static final String ICONS_EXECUTE_PNG = "/icons/execute.png";
    private static String BASE_DIR;

    static {
        BASE_DIR = System.getProperty("user.dir") + File.separator + ".pdf-crawl/";
    }

    private static JFrame frame;
    private static Container pane;

    private static final PdfScanner pdfScanner = new PdfScanner();

    private static JLabel fileInputPathLabel = new JLabel("Eingabe Verzeichnis(e) / Detei(en) ausw\u00e4hlen:");
    private static JLabel fileOutputPathLabel = new JLabel("Ausgabe Verzeichnis ausw\u00e4hlen:");

    private static JFileChooser inputPathFileChooser = new JFileChooser();
    private static JFileChooser outputPathFileChooser = new JFileChooser();

    private final static int TEXTFIELD_WIDTH = 30;
    private static JTextArea inputPathTextArea = new JTextArea(5, TEXTFIELD_WIDTH);
    private static JTextField outputPathTextField = new JTextField(TEXTFIELD_WIDTH);

    private static JTextArea logsArea = new JTextArea(10, 50);
    private static JScrollPane logsAreaScroll = new JScrollPane(logsArea);

    private static JScrollPane inputPathTextAreaScrollPane = new JScrollPane(inputPathTextArea);

    private static JButton browseInputButton = new JButton("Ausw\u00e4hlen");
    private static JButton browseOutputButton = new JButton("Ausw\u00e4hlen");
    private static JButton submitButton = new JButton("Ausf\u00fchren");

    public static JFrame initializeFrame() {
        if (frame == null) {
            frame = new JFrame("PDF Crawler");
        }

        pane = frame.getContentPane();
        GridBagLayout mgr = new GridBagLayout();
        pane.setLayout(mgr);

        GridBagConstraints c = new GridBagConstraints();

        inputPathFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        inputPathFileChooser.setMultiSelectionEnabled(true);
        inputPathFileChooser.setPreferredSize(new Dimension(1000, 600));
        outputPathFileChooser.setPreferredSize(new Dimension(1000, 600));
        outputPathFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        outputPathFileChooser.setMultiSelectionEnabled(false);

        logsAreaScroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        logsArea.setAutoscrolls(true);
        logsArea.setEditable(false);
        logsAreaScroll.setPreferredSize(new Dimension(600, 250));
        ( (DefaultCaret) logsArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        ButtonsActionListener buttonsActionListener = new ButtonsActionListener();

        browseInputButton.addActionListener(buttonsActionListener);
        browseOutputButton.addActionListener(buttonsActionListener);
        submitButton.addActionListener(buttonsActionListener);

        try {
            Image browseIcon = ImageIO.read(FrameFactory.class.getResource(ICONS_BROWSE_PNG));
            browseInputButton.setIcon(new ImageIcon(browseIcon.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
            browseOutputButton.setIcon(new ImageIcon(browseIcon.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

            Image submitIcon = ImageIO.read(FrameFactory.class.getResource(ICONS_EXECUTE_PNG));
            submitButton.setIcon(new ImageIcon(submitIcon.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            LoggingService.addExceptionToLog(e);
        }


        inputPathTextAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Edit Element display properties
        inputPathTextAreaScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        // Add input path label
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(fileInputPathLabel, c);

        // Add input path text area scroll pane
        c.gridx = 1;
        c.gridy = 0;
        pane.add(inputPathTextAreaScrollPane, c);

        // Add browse input button
        c.gridx = 2;
        c.gridy = 0;
        pane.add(browseInputButton, c);

        // Add output path label
        c.gridx = 0;
        c.gridy = 1;
        pane.add(fileOutputPathLabel, c);

        // Add output path text field
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 1;
        pane.add(outputPathTextField, c);

        // Add browse output button
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 1;
        pane.add(browseOutputButton, c);

        // Add convert button
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 3;
        pane.add(submitButton, c);

        // Add logs text area
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        pane.add(logsAreaScroll, c);

        frame.pack();
        frame.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // Set Windows in the middle of the screen.
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        URL iconURL = FrameFactory.class.getResource("/icons/favicon.png");
        // iconURL is null when not found
        ImageIcon icon = new ImageIcon(iconURL);
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set focus on startup
        browseInputButton.requestFocusInWindow();

        return frame;
    }

    static class ButtonsActionListener implements ActionListener {
        @Override
        // Browse input file
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == browseInputButton) {
                int returnVal = inputPathFileChooser.showOpenDialog(pane);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File[] inputPaths = inputPathFileChooser.getSelectedFiles();

                    inputPathTextArea.setText("");
                    // Fill textarea with file paths.
                    Arrays.stream(inputPaths).forEach(path -> {
                        if (Files.isDirectory(Paths.get(path.getAbsolutePath()))) {
                            File[] files = path.listFiles();
                            if (files != null && files.length > 0) {
                                Arrays.stream(files).forEach(filepath -> inputPathTextArea.append(filepath.getAbsolutePath() + Config.ls));
                            }
                        } else {
                            inputPathTextArea.append(path.getAbsolutePath() + Config.ls);
                        }
                    });
                }

                if (inputPathTextArea.getText().isBlank()) {
                    browseInputButton.requestFocus();
                } else if (outputPathTextField.getText().isBlank()) {
                    browseOutputButton.requestFocus();
                } else {
                    submitButton.requestFocus();
                }
                // Browse output file
            } else if (actionEvent.getSource() == browseOutputButton) {
                int returnVal = outputPathFileChooser.showOpenDialog(pane);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    outputPathTextField.setText(outputPathFileChooser.getSelectedFile().getAbsolutePath());
                    ApplicationLogger.setOutputFile(outputPathTextField.getText());
                } else {
                    LoggingService.log("Fehler beim Ausw\u00e4hlen des Ausgabepfad.");
                }

                if (outputPathTextField.getText().isBlank()) {
                    browseOutputButton.requestFocus();
                } else if (inputPathTextArea.getText().isBlank()) {
                    browseInputButton.requestFocus();
                } else {
                    submitButton.requestFocus();
                }
            } else if (actionEvent.getSource() == submitButton) {
                if (inputPathTextArea.getText().isBlank()) {
                    LoggingService.log("Konvertierung kann nicht stattfinden. Eingabedateipfad ist nicht definiert.");
                    JOptionPane.showMessageDialog(frame, "Bitte Eingabefeld definieren.");
                    return;
                } else if (outputPathTextField.getText().isBlank()) {
                    LoggingService.log("Konvertierung kann nicht stattfinden. Ausgabepfad ist nicht definiert");
                    JOptionPane.showMessageDialog(frame, "Bitte Ausgabefeld definieren.");
                    return;
                }

                ApplicationLogger.setOutputFile(outputPathTextField.getText());
                String[] inputPathLines = inputPathTextArea.getText().split(Config.ls);

                Map<String, String> pdfData = new HashMap<>();

                // Log all selected paths.
                LoggingService.logApplicationLogs("Diese Pfade wurden zum Bearbeiten ausgew\u00e4hlt: ");
                Arrays.stream(inputPathLines).forEach(inputPath -> {
                    ApplicationLogger.noFormattingLog(String.format("\t%s\n", inputPath));
                });

                ExecutorService executorService = Executors.newFixedThreadPool(2);
                Runnable processPDFsTask = () -> {
                    CSVErrorStatus.resetCounters();
                    submitButton.setEnabled(false);
                    for (String inputPath : inputPathLines) {
                        pdfData.putAll(pdfScanner.scanFile(inputPath));
                    }
                };
                Future<?> processPDFsTaskFuture = executorService.submit(processPDFsTask);

                Runnable writeToCSVTask = () -> {
                    try {
                        processPDFsTaskFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        LoggingService.addExceptionToLog(e, true);
                    }

                    try {
                        File outputFile = new File(outputPathTextField.getText());
                        outputFile.mkdir();
                        new CsvWriter().createCsv(pdfData, outputFile);
                    } catch (IOException e) {
                        // TODO: Display a GUI error message.
                        LoggingService.log("Fehler beim Erstellen von CSV:");
                        LoggingService.log(e.getMessage());
                    }
                    finally {
                        submitButton.setEnabled(true);
                        logStatistics();
                        LoggingService.log("VERARBEITUNG WURDE BEENDET");
                        ApplicationLogger.noFormattingLog("\n###VERARBEITUNG WURDE BEENDET###\n");
                        JOptionPane.showMessageDialog(frame,
                                String.format("Verarbeitung wurde %s beendet.",
                                        CSVErrorStatus.documentsWithErrors.isEmpty() ? "fehlerfrei" : "mit Fehlern"),
                                "Result",
                                CSVErrorStatus.documentsWithErrors.isEmpty() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
                    }
                };
                executorService.execute(writeToCSVTask);
            }
        }

        private void logStatistics() {
            logProcesedFiles(String.format("Gufunden (%d):", CSVErrorStatus.selectedDocuments.size()), CSVErrorStatus.selectedDocuments);
            logProcesedFiles(String.format("Ignoriert (%d):", CSVErrorStatus.notReadDocuments.size()), CSVErrorStatus.notReadDocuments);
            logProcesedFiles(String.format("Eingelesen (%d):", CSVErrorStatus.readDocuments.size()), CSVErrorStatus.readDocuments);
            logProcesedFiles(String.format("Fehlerfrei (%d):", CSVErrorStatus.documentsWithoutErrors.size()), CSVErrorStatus.documentsWithoutErrors);
            logProcesedFiles(String.format("Fehlerhaft (%d):", CSVErrorStatus.documentsWithErrors.size()
            ), CSVErrorStatus.documentsWithErrors);
            logCounters();
        }

        private void logProcesedFiles(String message, Set<String> documents) {
            ApplicationLogger.log(message);
            documents.forEach(documentPath -> {
                ApplicationLogger.noFormattingLog(String.format("\t%s\n", documentPath));
            });
        }

        private void logCounters() {

            ApplicationLogger.log(String.format("Statistiken:"));
            ApplicationLogger.noFormattingLog(String.format("\tEingegeben:\t\t%s\n" +
                            "\t===\n" +
                            "\tNich eingelesen:\t%d\n" +
                            "\t===\n" +
                            "\tEingelesen:\t\t%d\n" +
                            "\t===\n" +
                            "\tOhne Fehler:\t\t%d\n" +
                            "\t===\n" +
                            "\tMit Fehlern:\t\t%d\n",
                    CSVErrorStatus.selectedDocuments.size(),
                    CSVErrorStatus.notReadDocuments.size(),
                    CSVErrorStatus.readDocuments.size(),
                    CSVErrorStatus.documentsWithoutErrors.size(),
                    CSVErrorStatus.documentsWithErrors.size())
            );
        }
    }

    public static JTextArea getLogsArea() {
        return logsArea;
    }
}
