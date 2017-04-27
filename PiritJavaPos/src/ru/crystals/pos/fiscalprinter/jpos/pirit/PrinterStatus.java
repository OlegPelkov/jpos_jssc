package ru.crystals.pos.fiscalprinter.jpos.pirit;

/**
 * Created by o.pelkov on 21.03.2017.
 */
public class PrinterStatus {

    private static boolean bCoverOpen = false;
    private static boolean bPaperEnd = false;
    private static boolean bPrinterOnline = false;

    public static boolean isbCoverOpen() {
        return bCoverOpen;
    }

    public static void setbCoverOpen(boolean bCoverOpen) {
        PrinterStatus.bCoverOpen = bCoverOpen;
    }

    public static boolean isbPaperEnd() {
        return bPaperEnd;
    }

    public static void setbPaperEnd(boolean bPaperEnd) {
        PrinterStatus.bPaperEnd = bPaperEnd;
    }

    public static boolean isbPrinterOnline() {
        return bPrinterOnline;
    }

    public static void setbPrinterOnline(boolean bPrinterOnline) {
        PrinterStatus.bPrinterOnline = bPrinterOnline;
    }
}
