package pdfcrawler.adesso.de;

import pdfcrawler.adesso.de.gui.FrameFactory;

import javax.swing.*;
import java.util.Scanner;


public class Controller {
    private static final Scanner sn = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
            // handle exception
        }
        catch (ClassNotFoundException e) {
            // handle exception
        }
        catch (InstantiationException e) {
            // handle exception
        }
        catch (IllegalAccessException e) {
            // handle exception
        }

        JFrame jFrame = FrameFactory.initializeFrame();
        jFrame.setVisible(true);

    }

    private static String showMenu() {

        System.out.println("\n\n********** Menue **********");
        System.out.println("[1] Standardkonfiguration");
        System.out.println("[2] Pfad zu den PDF Dateien eingeben");
        System.out.println("[3] Beenden");

        System.out.print("Option: ");

        return sn.next();

    }

    private static String getPathFromUserInput() {
        System.out.print("Ordner zu den PDF-Dateien: ");
        return sn.next();
    }
}
