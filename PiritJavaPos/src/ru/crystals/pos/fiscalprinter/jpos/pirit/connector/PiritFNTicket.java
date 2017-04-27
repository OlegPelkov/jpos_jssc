package ru.crystals.pos.fiscalprinter.jpos.pirit.connector;

/**
 * Created by o.pelkov on 27.03.2017.
 */
public class PiritFNTicket
{
    private final int docNumber;
    private final byte[] hexData;
    private boolean signed = false;
    private String dataString = "";
    private String dateOfDoc = "";
    private String dateOfDocCool = "";
    private String ticketNumber = "";
    private String valueFPD = "";
    private int startIndex = 0;

    public PiritFNTicket(String data, int docNumber){
        this.signed = data.startsWith("1");
        this.dataString = data.substring(2);
        this.hexData =  this.dataString.getBytes();
        this.docNumber = docNumber;
        makeDataFromHexFn();
        makeTiketNumber();
        makeFPD();
    }

    public PiritFNTicket(int docNumber){
        this.signed = false;
        this.dataString = "";
        this.hexData = new byte[]{0};
        this.docNumber = docNumber;
    }

    /**
     * @return the docNumber
     */
    public int getDocNumber() {
        return docNumber;
    }

    /**
     * @return the hexData
     */
    public byte[] getHexData() {
        return hexData;
    }

    /**
     * @return the rawHexData
     */
    public boolean isSigned() {
        return signed;
    }

    /**
     * @return the dateOfDocCool
     */
    public String getDateOfDocCool() {
        return dateOfDocCool;
    }

    /**
     * @return the dateOfDoc
     */
    public String getDateOfDoc() {
        return dateOfDoc;
    }

    /**
     * @return the ticketNumber
     */
    public String getTicketNumber() {
        return ticketNumber;
    }

    /**
     * @return the valueFPD
     */
    public String getValueFPD() {
        return valueFPD;
    }

    private void makeDataFromHexFn() {
        String year = hexToDecimal(dataString.substring(startIndex, startIndex + 2));
        String month = hexToDecimal(dataString.substring(startIndex + 2, startIndex + 4));
        String day = hexToDecimal(dataString.substring(startIndex + 4, startIndex + 6));
        String hours = hexToDecimal(dataString.substring(startIndex + 6, startIndex + 8));
        String mins = hexToDecimal(dataString.substring(startIndex + 8, startIndex + 10));
        this.dateOfDoc = day+month+"20"+year+hours+mins;
        this.dateOfDocCool = day+"."+month+"."+year+" "+hours+":"+mins;
    }

    private void makeTiketNumber() {
        String partNumber = "";
        StringBuilder number = new StringBuilder();
        for(int i = 10; i<18; i+=2) {
            partNumber = dataString.substring(startIndex + i, startIndex + i+2);
            partNumber= partNumber+number.toString();
            number.delete(0,7);
            number.append(partNumber);
        }
        this.ticketNumber = hexToDecimal(number.toString());
    }

    private void makeFPD() {
        String partNumber = "";
        StringBuilder number = new StringBuilder();
        for(int i = 18; i<26; i+=2) {
            partNumber = dataString.substring(startIndex + i, startIndex + i+2);
            partNumber= partNumber+number.toString();
            number.delete(0,8);
            number.append(partNumber);
        }
        this.valueFPD = hexToDecimalFPD(number.toString());
    }

    private static String hexToDecimal(String hex) {
        String digits = "0123456789ABCDEF";
        String result = "";
        hex = hex.toUpperCase();
        long val = 0;
        for (int i = 0; i < hex.length(); i++)
        {
            char c = hex.charAt(i);
                int d = digits.indexOf(c);
                val = 16 * val + d;
        }
        if(val<10){
            result = "0"+String.valueOf(val);
        } else
            result =  String.valueOf(val);
        return result;
    }

    private static String hexToDecimalFPD(String hex) {
        String digits = "0123456789ABCDEF";
        String result = "";
        hex = hex.toUpperCase();
        long val = 0;
        for (int i = 0; i < hex.length(); i++)
        {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        result = String.valueOf(val);
        if(result.length()<10){
            result = "0"+result;
        }
        return result;
    }


}