package ru.crystals.pos.fiscalprinter.jpos.pirit.connector;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;


import jpos.FiscalPrinterConst;
import jpos.JposConst;
import jpos.JposException;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;
import jpos.services.EventCallbacks;
import jssc.SerialPort;
import jssc.SerialPortException;
import org.apache.log4j.Logger;
import ru.crystals.pos.fiscalprinter.jpos.pirit.PiritSvc112;


public class PiritConnector {

    private static final Logger Log = Logger.getLogger(PiritConnector.class);
//    private final byte STX = 2;
//    private final byte ETX = 3;
    private final byte ENQ = 5;
    private final byte ACK = 6;

    private final String SOH = "\u0001";
    private final String STX = "\u0002";
    private final String ETX = "\u0003";
    private final int THREAD_SLEEP_TIME = 2;

    private final String PASSWORD = "PIRI";
    private final long READ_TIME_OUT = 4000L; //TODO
    private int packetId;
    private int sendId;
//    private InputStream is = null;
//    private BufferedInputStream in = null;
//    private OutputStream os = null;
//    private BufferedOutputStream out = null;
    private SerialPort serialPort = null;

    private static final int MIN_PACKET_ID = 0x20;
    private static final int MAX_PACKET_ID = 0x3F;


    private InputStream is = null;
    private BufferedInputStream bis = null;
    private InputStreamReader isr = null;
    private OutputStream os = null;
    private BufferedOutputStream bos = null;
    private OutputStreamWriter osw = null;
    private String encoding = "cp866";

    private PropertyChangeSupport exeptionResolver = null;
    private boolean paperOut = false;

    public PiritConnector() {
        this.packetId = 32;
        this.exeptionResolver = new PropertyChangeSupport(this);
    }

    public void addListener(PropertyChangeListener listener) {
        exeptionResolver.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        exeptionResolver.removePropertyChangeListener(listener);
    }


    public void open(String portName, String baudRate, String dataBits, String stopBits, String parity, String useCTS) throws Exception {
        int _stopBits = 1;
        int _parity = 0;

        if (stopBits.equals("1.5"))
            _stopBits = 3;
        else if (stopBits.equals("2")) {
            _stopBits = 2;
        }

        parity = parity.toUpperCase();

        if (parity.equals("ODD"))
            _parity = 1;
        else if (parity.equals("EVEN"))
            _parity = 2;
        else if (parity.equals("MARK"))
            _parity = 3;
        else if (parity.equals("SPACE")) {
            _parity = 4;
        }

        try{
            this.serialPort  = new SerialPort(portName);
            this.serialPort.openPort();
            useCTS = useCTS.toUpperCase();
            if (useCTS.equals("YES")) {
                this.serialPort.setFlowControlMode(0x1 | 0x2);
            }
            this.serialPort.setParams(Integer.parseInt(baudRate), Integer.parseInt(dataBits), _stopBits, _parity);
        }
        catch(SerialPortException ex){
            throw new JposException(102, ex.getPortName()+" "+ex.getMessage());
        }

    }

    public void close() {
        try {
            serialPort.closePort();
        } catch (Exception localException) {
        }
    }

    // sendData
    public void sendData(byte data) throws Exception {
        Log.info(" sendData ");
        try {
            serialPort.writeByte(data);
            logDebugCommand(data);
        } catch (SerialPortException e) {
            Log.error("", e);
            throw new JposException(111, "Error send byte of data - "+e.getMessage());
        }
    }


    public void sendData(byte[] data) throws Exception {
        Log.info(" sendData ");
        try {
            serialPort.writeBytes(data);
            logDebugCommand(data);
        } catch (SerialPortException e) {
            Log.error("", e);
            throw new JposException(111, "Error send byte of data - "+e.getMessage());
        }
    }
/*
    // Очищение входного потока
    public void cleanInStream() throws IOException {
        StringBuilder garbage = new StringBuilder();
        while (is.available() > 0) {
            garbage.append(String.format("0x%X ", is.read()));
            garbage.append(',');
        }
        if (garbage.length() > 0) {
            if (garbage.charAt(garbage.length()-1)==',')
                garbage.deleteCharAt(garbage.length()-1);
            logDebugResponce("InStream garbage: ["+garbage.toString().getBytes()+"]");
        }
    }
    */

    public void logDebugResponce(String text) {
        Log.debug("<--" + convertDebugLog(text));
    }

    public void logDebugResponce(byte[] responce) {
        Log.debug("<--" + getForLogBytesAsString(responce));
    }

    public void logDebugResponce(int responce) {
        Log.debug("<--" + String.format("0x%X ", responce));
    }

    private synchronized int packetIdIncrementAndGet(){
        packetId++;
        if (packetId > MAX_PACKET_ID)
            packetId = MIN_PACKET_ID;
        return packetId;
    }

    public synchronized OutputStreamWriter getOutputStreamWriter() {
        return osw;
    }
    public void flushStreams() throws IOException {
        osw.flush();
        bos.flush();
        os.flush();
    }
    public int SendPacket(int cmd, String data) throws Exception {
        StringBuilder packet = new StringBuilder();
        try {
            int expectedPacketId = packetIdIncrementAndGet();
            packet.append(STX);
            packet.append(PASSWORD);
            packet.append((char) expectedPacketId);
            packet.append(String.format("%02X", cmd));
            packet.append(data);
            packet.append(ETX);
            packet.append(String.format("%02X", calculateCrc(packet.toString())));
            serialPort.writeBytes(packet.toString().getBytes());
            logDebugCommand(packet.toString());
            return expectedPacketId;
        } catch (IOException e) {
            Log.error("", e);
            throw new JposException(111, "Error send array of data");
        }
    }

    private int calculateCrc(String string) throws UnsupportedEncodingException{
        int crc = 0;
        byte[] arr = string.getBytes(getEncoding());

        for (int i = 1; i < arr.length; i++) {
            crc = (crc ^ arr[i]) & 0xFF;
        }

        return crc ;
    }

    public String getEncoding() {
        return encoding;
    }

 /*
    public void SendPacket(int cmd, String data) throws Exception {
        ByteArrayOutputStream packet = new ByteArrayOutputStream();
        packet.write(STX);
        packet.write(PASSWORD.getBytes());
        packet.write(this.packetId);
        packet.write(String.format("%02X", new Object[] { Integer.valueOf(cmd) }).getBytes());
        packet.write(data.getBytes());
        packet.write(ETX);

        this.sendId = this.packetId;
        byte[] arr = packet.toByteArray();
        int crc = 0;
        for (int i = 1; i < arr.length; ++i)
            crc = (crc ^ arr[i]) & 0xFF;

        packet.write(String.format("%02X", new Object[] { Integer.valueOf(crc) }).getBytes());

        this.packetId += 1;
        if (this.packetId > 63)
            this.packetId = 32;

        try {
            this.out.write(packet.toByteArray());
            this.out.flush();
            this.os.flush();
        } catch (IOException e) {
            Log.error("", e);
            throw new JposException(111, "Error send array of data");
        }

        Log.trace("-->" + packet.toString());
    }
*/
    public void logDebugCommand(String text) {
        Log.debug("-->" + convertDebugLog(text));
    }

    public void logDebugCommand(byte[] command) {
        Log.debug("-->" + getForLogBytesAsString(command));
    }

    public void logDebugCommand(byte command) {
        Log.debug("-->" + String.format("0x%X ", command));
    }

    public void logDebugCommand(int command) {
        Log.debug("-->" + String.format("0x%X ", command));
    }

    private String getForLogBytesAsString(byte[] command) {
        StringBuilder result = new StringBuilder();
        for (byte i : command) {
            result.append(String.format("0x%X ", i));
        }
        return result.toString();
    }

    private String convertDebugLog(String text) {
        if (Log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            for (Character c : text.toCharArray()) {
                if (c > 255 || c.toString().matches("[\\p{Print}]"))
                    sb.append(c);
                else
                    sb.append(String.format("{%02X}", Integer.valueOf(c)));
            }
            return sb.toString();
        } else
            return text;
    }

/*
    public String ReadPacket(int cmd) throws Exception {
    ByteArrayOutputStream packet = new ByteArrayOutputStream();
    long startTime = System.currentTimeMillis();
    boolean timeOut = false;

    StringBuilder packett = new StringBuilder();

    int receivedId = 0;
    int codeCmd = 0;
    int readCRC = 0;
    int calcCRC = 0;
    int errorCode;
    int repeatCount = 10;

    try {
        while (!Thread.currentThread().isInterrupted()) {
            if (getInputStreamReader().ready()) {
                int c = getInputStreamReader().read();

                if ((c == 0x06) && (cmd == 0x15)) { // load logo
                    return "";
                } else if (c == 0x06) {//ответ на проверку связи с устройством
                    Log.trace("<--" + (char) c);
                    if (repeatCount-- > 0) {
                        timeOut = false;
                    }
                    continue;
                }
                packet.append((char) c);
                if (packet.length() == 1 && c == STX.charAt(0)) {
                    // Начали получать данные, следовательно если timeOut истек незачем посылать 0x05
                    timeOut = true;
                }
                if (packet.indexOf(STX) == -1) {
                    packet = new StringBuilder();
                    continue;
                } else if ((packet.length() >= 9) && (packet.indexOf(ETX) == packet.length() - 3)) {
                    break;
                }
            } else if (System.currentTimeMillis() - startTime > readTimeOut) {
                if (timeOut) {
                    throw new Exception("TimeOut read of data");
                } else {
                    logDebugResponce(packet.toString());
                    this.sendData((byte) 0x05);//проверка связи с устройством
                    startTime = System.currentTimeMillis();
                    timeOut = true;
                }
            } else {
                Thread.sleep(THREAD_SLEEP_TIME);
            }
        }
    } catch (Exception e) {
        logWarn(e);
       // throw new FiscalPrinterCommunicationException(ResBundleFiscalPrinterPirit.getString("ERROR_READ_DATA"), PiritErrorMsg.getErrorType());
        throw new FiscalPrinterException(ResBundleFiscalPrinterPirit.getString("ERROR_READ_DATA"));
    } finally {
        sizeBuffer = 0;
        logDebugResponce(packet.toString());
    }

    int ptrAfterETX = 0;
    try {
         do {
             do {
                 int c = 0;
                 while (true) {
                               if (this.in.available() <= 0) break;
                               c = this.in.read();

                               if (c == STX) { packet.reset(); break; }
                               if (c == ETX) { ptrAfterETX = 0; break; }
                               if ((c == ACK) && (cmd == 0x15)) return "";
                               if (c != ACK) break;
                               Log.trace("<--" + c);
                               timeOut = false;
                              }
                              packet.write(c);
                }
                while ((ptrAfterETX++ != 2) || (packet.size() < 9));

      }
      while (System.currentTimeMillis() - startTime <= READ_TIME_OUT);
      if (timeOut) {
        throw new JposException(112, "TimeOut read of data");
      }

      SendData(ENQ);
      startTime = System.currentTimeMillis();
    }
    catch (IOException e)
    {
      throw new JposException(111, "Error read of data");
    }
    finally {
      Log.trace("<--" + packet.toString());
      }

    Log.trace("<--" + packet.toString());

    byte[] data = packet.toByteArray();
    String strData = packet.toString();

    int receivedId = data[1];
    int codeCmd = Integer.parseInt(strData.substring(2, 4), 16);
    int readCRC = Integer.parseInt(strData.substring(packet.size() - 2, packet.size()), 16);
    int calcCRC = 0;

    for (int i = 1; i < data.length - 2; ++i) {
      calcCRC ^= data[i] & 0xFF;
    }

    int errorCode = Integer.parseInt(packet.toString().substring(4, 6), 16);

    if (errorCode == 4) {
      throw new JposException(111, "Error buffer overflow");
    }

    if (this.sendId != receivedId) {
      if (errorCode != 0) {
        Log.error("Error packet Id: sendId=" + (char)this.sendId + " receivedId=" + (char)receivedId);
        throw new JposException(111, errorCode, PiritErrorMsg.getErrorMessage(errorCode));
      }
      throw new JposException(111, "Error packet Id: sendId=" + (char)this.sendId + " receivedId=" + (char)receivedId);
    }

    if (codeCmd != cmd) {
      throw new JposException(111, "Error code Cmd: txCodeCmd=" + cmd + " rxCodeCmd=" + codeCmd);
    }

    if (calcCRC != readCRC) {
      throw new JposException(111, "Error CRC: readCRC=" + readCRC + " calcCRC=" + calcCRC);
    }

    if (errorCode != 0) {
      Log.error("Error code=" + errorCode + ", error message=" + PiritErrorMsg.getErrorMessage(errorCode));
      throw new JposException(111, errorCode, PiritErrorMsg.getErrorMessage(errorCode));
    }

    return strData.substring(6, packet.size() - 3);
  }
*/
    public InputStreamReader getInputStreamReader() {
        return isr;
    }

    public String ReadPacket(int cmd) throws Exception{
        return ReadPacket(cmd, READ_TIME_OUT, packetId);

    }

    public String ReadPacket(int cmd, long readTimeOut, int expectedPacketId) throws Exception{
        StringBuffer packet = new StringBuffer();
        long startTime = System.currentTimeMillis();
        boolean timeOut = false;
        int receivedId = 0;
        int codeCmd = 0;
        int readCRC = 0;
        int calcCRC = 0;
        int errorCode;
        int repeatCount = 10;
        int c = 0;
        byte[] tmpbuffer = new byte[1];
        try {
            while (!Thread.currentThread().isInterrupted()) {
                    tmpbuffer = serialPort.readBytes();
                    if(tmpbuffer!=null && tmpbuffer.length>0) {
                        for (int i = 0; i < tmpbuffer.length; i++) {
                            c = tmpbuffer[i];
                            if ((c == 0x06) && (cmd == 0x15)) { // load logo
                                return "";
                            } else if (c == 0x06) {//ответ на проверку связи с устройством
                                Log.trace("<--" + (char) c);
                                if (repeatCount-- > 0) {
                                    timeOut = false;
                                }
                                continue;
                            }
                            packet.append((char) c);
                        }
                        if (packet.length() == 1 && c == STX.charAt(0)) {
                        /* Начали получать данные, следовательно если timeOut истек незачем посылать 0x05 */
                            timeOut = true;
                        }
                        if (packet.indexOf(STX) == -1) {
                            packet = new StringBuffer();
                            continue;
                        } else if ((packet.length() >= 9) && (packet.indexOf(ETX) == packet.length() - 3)) {
                            break;
                        }
                    } else if (System.currentTimeMillis() - startTime > readTimeOut) {
                    if (timeOut) {
                        throw new Exception("TimeOut read of data");
                    } else {
                        logDebugResponce(packet.toString());
                        this.sendData((byte) 0x05);//проверка связи с устройством
                        startTime = System.currentTimeMillis();
                        timeOut = true;
                    }
                } else {
                    Thread.sleep(THREAD_SLEEP_TIME);
                }
            }
        } catch (Exception e) {
            Log.warn(e);
            throw new JposException(111, "Error read of data");
        } finally {
          //  sizeBuffer = 0;
            logDebugResponce(packet.toString());
        }

        try {
            receivedId = packet.charAt(1);
            codeCmd = Integer.parseInt(packet.substring(2, 4), 16);
            readCRC = Integer.parseInt(packet.substring(packet.length() - 2, packet.length()), 16);
        } catch (NumberFormatException nfe) {
            // Это тоже надо обработать
            Log.warn("", nfe);
           // throw new FiscalPrinterCommunicationException(ResBundleFiscalPrinterPirit.getString("ERROR_READ_DATA"), PiritErrorMsg.getErrorType());
            throw new JposException(111, "Error read of data");
        }

        try {
            byte[] arr = packet.toString().getBytes(getEncoding());

            for (int i = 1; i < arr.length - 2; i++) {
                calcCRC = (calcCRC ^ arr[i]) & 0xFF;
            }
        } catch (IOException e) {
            Log.warn("", e);
           // throw new FiscalPrinterCommunicationException(ResBundleFiscalPrinterPirit.getString("ERROR_READ_DATA"), PiritErrorMsg.getErrorType());
            throw new JposException(111, "Error read of data");
        }

        errorCode = Integer.parseInt(packet.substring(4, 6), 16);

        if (errorCode == 4) {
            throw new JposException(111, "Error buffer overflow");
        }

        if (expectedPacketId != receivedId) {

                if (errorCode != 0) {
                  Log.error("Error packet Id: sendId=" + (char)this.sendId + " receivedId=" + (char)receivedId);
                  throw new JposException(111, errorCode, PiritErrorMsg.getErrorMessage(errorCode));
                }
                throw new JposException(111, "Error packet Id: sendId=" + (char)this.sendId + " receivedId=" + (char)receivedId);
        }

        if (codeCmd != cmd) {
            Log.error("Error code Cmd: txCodeCmd=" + cmd + " rxCodeCmd=" + codeCmd);
            //throw new FiscalPrinterCommunicationException(ResBundleFiscalPrinterPirit.getString("ERROR_READ_DATA"), PiritErrorMsg.getErrorType());
            throw new JposException(111, "Error code Cmd: txCodeCmd=" + cmd + " rxCodeCmd=" + codeCmd);
        }

        if (calcCRC != readCRC) {
            Log.error("Error CRC: readCRC=" + readCRC + " calcCRC=" + calcCRC);
            //throw new FiscalPrinterCommunicationException(ResBundleFiscalPrinterPirit.getString("ERROR_READ_DATA"), PiritErrorMsg.getErrorType());
            throw new JposException(111, "Error CRC: readCRC=" + readCRC + " calcCRC=" + calcCRC);
        }

        if (errorCode != 0) {
            Log.error("Error code=" + errorCode + ", error message=" + PiritErrorMsg.getErrorMessage(errorCode));
            if(errorCode == 8 | errorCode == 9){
                 exeptionResolver.firePropertyChange("PaperEnd", false, true);
            } else {

                throw new JposException(111, errorCode, PiritErrorMsg.getErrorMessage(errorCode));
            }
        }
        return packet.substring(6, packet.length() - 3);
    }



    public static void main(String[] args) {
        try{
    /*    PiritConnector pc = new PiritConnector();
        Configurator config = new Configurator();
        config.setBaudRate("57600");
        config.setPortName("COM3");
        pc.open(config.getPortName(), config.getBaudRate(), config.getDataBits(), config.getStopBits(), config.getParity(), config.getUseCTS());
        pc.ReadPacket(0,3000,pc.SendPacket(0, ""));
        System.out.println("nnn 'GHbdnt' dfgdfg");
    */
        PiritSvc112 piritSvc112 = new PiritSvc112();
        EventCallbacks callBacks = piritSvc112.createPiritCallBacks();

        StatusUpdateListener listener = new StatusUpdateListener() {
            @Override
            public void statusUpdateOccurred(StatusUpdateEvent statusUpdateEvent) {
                if (statusUpdateEvent.getStatus() == JposConst.JPOS_PS_ONLINE) {
                    System.err.println("listener - ! - FR online");
                }
                if (statusUpdateEvent.getStatus() == JposConst.JPOS_SUE_POWER_OFFLINE) {
                    System.err.println("listener - ! - FR offline");
                }
                if (statusUpdateEvent.getStatus() == FiscalPrinterConst.FPTR_SUE_REC_EMPTY) {
                    System.err.println("listener - ! - NO PAPER");
                }
                if (statusUpdateEvent.getStatus() == FiscalPrinterConst.FPTR_SUE_JRN_PAPEROK) {
                    System.err.println("listener - ! - PAPER OK");
                }
                if (statusUpdateEvent.getStatus() == FiscalPrinterConst.FPTR_SUE_COVER_OPEN) {
                    System.err.println("listener - ! - COVER OPEN");
                }
                if (statusUpdateEvent.getStatus() == FiscalPrinterConst.FPTR_SUE_COVER_OK) {
                    System.err.println("listener - ! - COVER OK");
                }
            }
        };
        piritSvc112.addStatusUpdateListener(listener);
        piritSvc112.open("PIRIT-2F", callBacks);
        piritSvc112.claim(1000);
        piritSvc112.setDeviceEnabled(true);

        System.out.println("  --42--  ");

        int[] arg1 = {6058,100};
        Object[] result1 = new Object[1];
        piritSvc112.directIO(42, arg1, result1);
        Vector<PiritFNTicket> resultV = (Vector<PiritFNTicket>) result1[0];
        for (PiritFNTicket s : resultV) {
            System.out.println("DN "+s.getDocNumber() + " TN "+s.getTicketNumber() + " FPD " + s.getValueFPD());
        }

        System.out.println("  -- REGISTER_UNNULL_TOTAL_COMING_START_SHIFT --  ");
        arg1[0] = 112;
        result1 = new String[1];
        piritSvc112.directIO(27, arg1, result1);
        System.out.println(result1[0]);

        System.out.println("  -- REGISTER_UNNULL_TOTAL_REFOUND_START_SHIFT --  ");
        arg1[0] = 113;
        result1 = new String[1];
        piritSvc112.directIO(27, arg1, result1);
        System.out.println(result1[0]);

        System.out.println("  -- REGISTER_UNNULL_TOTAL_EXPEND_START_SHIFT --  ");
        arg1[0] = 114;
        result1 = new String[1];
        piritSvc112.directIO(27, arg1, result1);
        System.out.println(result1[0]);

        System.out.println("  -- REGISTER_UNNULL_TOTAL_EXPEND_REFOUND_START_SHIFT --  ");
        arg1[0] = 115;
        result1 = new String[1];
        piritSvc112.directIO(27, arg1, result1);
        System.out.println(result1[0]);

        System.out.println("  -- REGISTER_UNNULL_TOTAL_COMING_START_REG_FR --  ");
        arg1[0] = 212;
        result1 = new String[1];
        piritSvc112.directIO(27, arg1, result1);
        System.out.println(result1[0]);

        System.out.println("  -- REGISTER_UNNULL_TOTAL_REFOUND_START_REG_FR --  ");
        arg1[0] = 213;
        result1 = new String[1];
        piritSvc112.directIO(27, arg1, result1);
        System.out.println(result1[0]);

        System.out.println("  -- REGISTER_UNNULL_TOTAL_EXPEND_START_REG_FR --  ");
        arg1[0] = 214;
        result1 = new String[1];
        piritSvc112.directIO(27, arg1, result1);
        System.out.println(result1[0]);

        System.out.println("  -- REGISTER_UNNULL_TOTAL__EXPEND_REFOUND_START_REG_FR --  ");
        arg1[0] = 215;
        result1 = new String[1];
        piritSvc112.directIO(27, arg1, result1);
        System.out.println(result1[0]);

/*
            piritSvc112.beginFiscalReceipt(true);
            piritSvc112.printRecItemAdjustment(1, "<Уценка>", 0, 0);
        piritSvc112.printRecItem("*11196 KAMIS Зелень петрушки в пак. 8г *", 20021, 1000, 1, 20021, "tax");
        arg1[0] = 200000;
        piritSvc112.directIO(54, arg1, null);
        piritSvc112.printRecTotal(0, 20000, "00");
        piritSvc112.endFiscalReceipt(true);
        */
        }
    catch(Exception e){
        System.err.println("main - "+e.getMessage());

    }}
        }