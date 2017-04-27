package ru.crystals.pos.fiscalprinter.jpos.pirit.connector;

public class PiritErrorMsg
{
  public static String getErrorMessage(int errorCode)
  {
    switch (errorCode) {
    case 0:
      return "SUCCESSFULLY EXECUTED";
    case 1:
      return "ERROR STATE FP";
    case 2:
      return "ERROR NUMBER OF FUNCTION";
    case 3:
      return "ERROR FORMAT";
    case 4:
      return "ERROR BUFFER OVERFLOW";
    case 5:
      return "ERROR TIME OUT SEND DATA";
    case 6:
      return "ERROR PASSWORD";
    case 7:
      return "ERROR CRC";
    case 8:
      return "WARN END OF PAPER";
    case 9:
      return "WARN PRINTER NOT READY";
    case 10:
      return "WARN CURRENT SHIFT MORE 24 H";
    case 11:
      return "WARN DIFFERENCE TIME MORE 8 MIN";
    case 12:
      return "WARN ERROR NEW DATA";
    case 13:
      return "ERROR PASSWORD FOR ACCESS TO FISCAL MEMORY";
    case 14:
      return "ERROR NEGATIVE RESULT";
    case 15:
      return "WARN YOU MAST CLOSE SHIFT";
    case 32:
      return "FATAL ERROR";
    case 33:
      return "ERROR FREE FISCAL MEMORY";
    case 65:
      return "ERROR FORMAT EKLZ COMMAND";
    case 66:
      return "ERROR STATE EKLZ";
    case 67:
      return "FAILURE EKLZ";
    case 68:
      return "FAILURE CPU EKLZ";
    case 69:
      return "ERROR TIME LIFE EKLZ";
    case 70:
      return "ERROR EKLZ IS FULL";
    case 71:
      return "ERROR DATE TIME";
    case 72:
      return "WARN NO REQUESTED DATA";
    case 73:
      return "ERROR DOCUMENT OVERFLOW";
    case 74:
      return "ERROR NO EKLZ RESPONSE";
    case 75:
      return "ERROR EKLZ COMMUNICATION";
    }
    return "UNKNOWN ERROR";
  }
}