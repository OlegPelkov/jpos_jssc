package ru.crystals.pos.fiscalprinter.jpos.pirit.connector;

/**
 * Created by o.pelkov on 03.04.2017.
 */
public class PiritExtendedErrorMsg {

    public static String getPiritExtendedErrorMsg(int errorCode) {
        switch (errorCode) {
            case 0:
                return "SUCCESSFULLY EXECUTED";
            case 1:
                return "THE -GET STARTED- FUNCTION WAS NOT CALLED";
            case 2:
                return "NON-FISCAL MODE";
            case 3:
                return "ARCHIVE FN CLOSED";
            case 4:
                return "FN NOT REGISTERED";
            case 5:
                return "FN ALREADY REGISTERED";
            case 7:
                return "NO CHANGES FOR RE-REGISTRATION OF FN";
            case 8:
                return "THE DOCUMENT WAS NOT OPENED";
            case 9:
                return "PREVIOUS DOCUMENT IS NOT CLOSED";
            case 10:
                return "REVERSAL ON A COPY OF THE DOCUMENT";
            case 11:
                return "THE DOCUMENT STATUS IS NOT 1 (SEE DOCUMENT STATUS)";
            case 12:
                return "THE DOCUMENT STATUS IS NOT 1 OR 2 (SEE DOCUMENT STATUS)";
            case 13:
                return "THE DOCUMENT STATUS IS NOT 1 OR 2 OR 3 (SEE DOCUMENT STATUS)";
            case 14:
                return "THE DOCUMENT STATUS IS NOT 4 (SEE DOCUMENT STATUS)";
            case 15:
                return "THE DOCUMENT IS CLOSED IN FN";
            case 16:
                return "THE DOCUMENT IS NOT A SALE (ARRIVAL) OR REFUND (RETURN ARRIVAL)";
            case 17:
                return "THE DOCUMENT IS NOT A DEPOSIT OR WITHDRAWAL";
            case 18:
                return "THE DOCUMENT IS NOT A SERVICE DOCUMENT";
            case 19:
                return "THE DOCUMENT IS A SERVICE DOCUMENT";
            case 20:
                return "SHIFT IS NOT OPEN";
            case 21:
                return "FATAL ERROR FN";
            case 22:
                return "FN IS NOT IN THE RECEIPT MODE FOR A OFD DOCUMENT";
        }
        return "UNKNOWN ERROR "+errorCode;
    }
}
