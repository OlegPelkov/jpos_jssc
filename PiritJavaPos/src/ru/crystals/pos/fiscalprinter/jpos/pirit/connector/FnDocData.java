package ru.crystals.pos.fiscalprinter.jpos.pirit.connector;

/**
 * Created by o.pelkov on 23.03.2017.
 */
public class FnDocData {

    private final  String FS = "\u001C";
    private String dateDoc = "";
    private String numberFN = "";
    private String recOFD = "";
    private String hexData = "";
    private int startIndex = 0;
    public FnDocData(String hexData) {
        this.hexData = hexData;
        this.startIndex = hexData.indexOf(FS);
        makeDataFromHexFn();
        makeNumberFNFromHexFn();
        makeOFDrecData();
    }

    private void makeDataFromHexFn() {
        String year = hexToDecimal(hexData.substring(startIndex+1, startIndex + 3));
        String month = hexToDecimal(hexData.substring(startIndex + 3, startIndex + 5));
        String day = hexToDecimal(hexData.substring(startIndex + 5, startIndex + 7));
        String hours = hexToDecimal(hexData.substring(startIndex + 7, startIndex + 9));
        String mins = hexToDecimal(hexData.substring(startIndex + 9, startIndex + 11));
        this.dateDoc = day+"."+month+"."+year+" "+hours+":"+mins;
    }

    private void makeNumberFNFromHexFn() {
        this.numberFN = hexToDecimal(hexData.substring(startIndex + 11, startIndex + 15));
    }

    private void makeOFDrecData() {
        this.recOFD = hexToDecimal(hexData.substring(startIndex + 15, startIndex + 33));
   }

    public String getFullDoc() {
        return "date = "+this.dateDoc+" FN number = "+this.numberFN+" OFD data = "+this.recOFD;
    }

    public static String hexToDecimal(String hex) {
        String digits = "0123456789ABCDEF";
        String result = "";
        hex = hex.toUpperCase();
        int val = 0;
        for (int i = 0; i < hex.length(); i++)
        {
            char c = hex.charAt(i);
            if(c!='0') {
                int d = digits.indexOf(c);
                val = 16 * val + d;
            }
        }
        if(val<10){
            result = "0"+String.valueOf(val);
        } else
            result =  String.valueOf(val);
        return result;
    }

    public String getDateDoc() {
        return dateDoc;
    }

    public String getNumberFN() {
        return numberFN;
    }

    public String getRecOFD() {
        return recOFD;
    }
}
