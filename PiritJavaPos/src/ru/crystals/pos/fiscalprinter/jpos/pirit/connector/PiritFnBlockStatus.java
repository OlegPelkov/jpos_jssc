package ru.crystals.pos.fiscalprinter.jpos.pirit.connector;

/**
 * Created by o.pelkov on 03.04.2017.
 */
public class PiritFnBlockStatus {
    public static String gePiritFnBlockStatusMsg(int code) {
        switch (code) {
            case 1:
                return "FN NOT FOUND";
            case 2:
                return "THE NF ARCHIVE WAS NOT CLOSED";
            case 3:
                return "ERROR IN THE FN ARCHIVE TEST";
            case 4:
                return "ERROR COMMUNICATING WITH FN";
            case 5:
                return "SHIFT CLOSING OPERATION IS NOT COMPLETED";
            case 6:
                return "ERROR COMMUNICATING WITH FN";
        }
        return "SUCCESSFULLY EXECUTED "+code+" ";
    }
}
