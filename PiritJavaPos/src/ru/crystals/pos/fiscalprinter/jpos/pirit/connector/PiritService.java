package ru.crystals.pos.fiscalprinter.jpos.pirit.connector;

import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import jpos.JposException;
import org.apache.log4j.Logger;

import jpos.config.JposEntry;

public class PiritService {
    private static final Logger Log = Logger.getLogger(PiritService.class);
    private static PiritService instance = null;
    private static PiritConnector pc = null;
    private static Configurator config = new Configurator();
    private static String physicalDescription = "Fiscal Printer PIRIT";
    private static String physicalName = "Fiscal Printer PIRIT";
    private static String getServiceDescription = "";
    private static int countStart = 0;
    protected boolean bEnablePacketPrint = false;
    private String bayerAddress = "";
    private long readTimeOut;
    private PropertyChangeListener exeptinListener = null;

    public long getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public boolean isbEnablePacketPrint() {
        return bEnablePacketPrint;
    }

    public void setbEnablePacketPrint(boolean bEnablePacketPrint) {
        this.bEnablePacketPrint = bEnablePacketPrint;
    }

    public static synchronized PiritService getInstance() {
        if (instance == null)
            instance = new PiritService();
        return instance;
    }

    public void setExeptinListener(PropertyChangeListener exeptinListener){
        this.exeptinListener = exeptinListener;
    }

    public void setBayerAddress(String bayerAddress) {
        this.bayerAddress = bayerAddress;
    }

    public void readConfig(JposEntry jposEntry) {
        JposEntry.Prop prop = jposEntry.getProp("productDescription");
        if (prop != null) {
            physicalDescription = prop.getValueAsString();
        }

        prop = jposEntry.getProp("productName");
        if (prop != null) {
            physicalName = prop.getValueAsString();
        }

        prop = jposEntry.getProp("serviceDescription");
        if (prop != null) {
            getServiceDescription = prop.getValueAsString();
        }

        prop = jposEntry.getProp("port");
        if (prop != null) {
            String portName = prop.getValueAsString();
            if (portName.length() > 0)
                config.setPortName(prop.getValueAsString());
        }

        prop = jposEntry.getProp("baudRate");
        if (prop != null) {
            String baudRate = prop.getValueAsString();
            if (baudRate.length() > 0)
                config.setBaudRate(baudRate);
        }

        prop = jposEntry.getProp("dataBits");
        if (prop != null) {
            String dataBits = prop.getValueAsString();
            if (dataBits.length() > 0)
                config.setDataBits(dataBits);
        }

        prop = jposEntry.getProp("stopBits");
        if (prop != null) {
            String stopBits = prop.getValueAsString();
            if (stopBits.length() > 0)
                config.setStopBits(stopBits);
        }

        prop = jposEntry.getProp("parity");
        if (prop != null) {
            String parity = prop.getValueAsString();
            if (parity.length() > 0)
                config.setParity(parity);
        }

        prop = jposEntry.getProp("useCTS");
        if (prop != null) {
            String useCTS = prop.getValueAsString();
            if (useCTS.length() > 0)
                config.setUseCTS(useCTS);
        }

        prop = jposEntry.getProp("depart");
        if (prop != null) {
            String depart = prop.getValueAsString();
            if (depart.length() > 0)
                config.setDepart(depart);
        }

        prop = jposEntry.getProp("password");
        if (prop != null) {
            String password = prop.getValueAsString();
            if (password.length() > 0)
                config.setPassword(password);
        }
    }

    public synchronized void start() throws Exception {
        if (countStart == 0) {
            if (pc != null)
                pc.close();
            pc = new PiritConnector();
            pc.addListener(exeptinListener);
            pc.open(config.getPortName(), config.getBaudRate(), config.getDataBits(), config.getStopBits(), config.getParity(), config.getUseCTS());

            pc.SendPacket(0, "");
            DataPacket dp = new DataPacket(pc.ReadPacket(0));
            boolean needStartCmd = getBit(dp.getLongValue(1), 0);
            Log.info("needStartCmd:" + needStartCmd);

            if (needStartCmd) {
                dp = new DataPacket();
                dp.putDateValue(new Date());
                dp.putTimeValue(new Date());

                pc.SendPacket(16, dp.getDataBuffer());
                pc.ReadPacket(16);
            }
        }
        countStart = 1;
    }

    public synchronized void stop() {
        if (countStart == 1)
            pc.close();

        countStart = 0;
    }

    public synchronized boolean isConnect() {
        try {
            pc.SendPacket(0, "");
            pc.ReadPacket(0);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public synchronized boolean isShiftOpen() throws Exception {
        pc.SendPacket(0, "");
        DataPacket dp = new DataPacket(pc.ReadPacket(0));
        long lVal = dp.getLongValue(1);

        return getBit(lVal, 2);
    }

    public synchronized String getPrinterState() throws Exception {
        StringBuilder result = new StringBuilder();

        pc.SendPacket(0, "");
        DataPacket dp = new DataPacket(pc.ReadPacket(0));
        int fatalStatus = (int) dp.getLongValue(0);

        if (getBit(fatalStatus, 0))
            result.append("Неверная контрольная сумма NVR\n");

        if (getBit(fatalStatus, 1))
            result.append("Неверная контрольная сумма в конфигурации\n");

        if (getBit(fatalStatus, 2))
            result.append("Ошибка интерфейса с ФП\n");

        if (getBit(fatalStatus, 3))
            result.append("Неверная контрольная сумма фискальной памяти\n");

        if (getBit(fatalStatus, 4))
            result.append("Ошибка записи в фискальную память\n");

        if (getBit(fatalStatus, 5))
            result.append("Фискальный модуль не авторизован\n");

        if (getBit(fatalStatus, 6))
            result.append("Фатальная ошибка ЭКЛЗ\n");

        if (getBit(fatalStatus, 7)) {
            result.append("Расхождение данных ФР и ЭКЛЗ\n");
        }

        int curState = (int) dp.getLongValue(1);

        if (getBit(curState, 0))
            result.append("Не была вызвана функция 'Начало работы'\n");

        if (getBit(curState, 1))
            result.append("Нефискальный режим\n");

        if (getBit(curState, 2))
            result.append("Смена открыта\n");

        if (getBit(curState, 3))
            result.append("Смена больше 24 часов\n");

        if (getBit(curState, 4))
            result.append("Архив ЭКЛЗ закрыт\n");

        if (getBit(curState, 5))
            result.append("ЭКЛЗ не активирована\n");

        if (getBit(curState, 6))
            result.append("Нет памяти для закрытия смены в ФП\n");

        if (getBit(curState, 7))
            result.append("Был введен неверный пароль доступа к ФП\n");

        if (getBit(curState, 8)) {
            result.append("Не было завершено закрытие смены, необходимо повторить операцию\n");
        }

        pc.SendPacket(4, "");
        dp = new DataPacket(pc.ReadPacket(4));

        int printerState = (int) dp.getLongValue(0);

        if (getBit(printerState, 0))
            result.append("Принтер не готов\n");

        if (getBit(printerState, 1))
            result.append("В принтере нет бумаги\n");

        if (getBit(printerState, 2))
            result.append("Открыта крышка принтера\n");

        if (getBit(printerState, 3))
            result.append("Ошибка резчика принтера\n");

        if (getBit(printerState, 7)) {
            result.append("Нет связи с принтером\n");
        }

        return result.toString();
    }

    public synchronized String getAddErrorInfo() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        pc.SendPacket(0x06, "1");
        DataPacket dp = new DataPacket(pc.ReadPacket(0x06));
        int printerState = (int) dp.getLongValue(1);
        stringBuilder.append("FR state - "+PiritExtendedErrorMsg.getPiritExtendedErrorMsg(printerState));
        pc.SendPacket(0x06, "2");
        dp = new DataPacket(pc.ReadPacket(0x06));
        printerState = (int) dp.getLongValue(1);
        stringBuilder.append("\nFN state - "+PiritFnBlockStatus.gePiritFnBlockStatusMsg(printerState));
        return stringBuilder.toString();
    }


    public synchronized boolean isPaperOut() throws Exception {
        pc.SendPacket(0x04, "");
        DataPacket dp = new DataPacket(pc.ReadPacket(0x04));
        int printerState = (int) dp.getLongValue(0);
        return getBit(printerState, 1);
    }

    public synchronized long getPrinterStatus() throws Exception {
        pc.SendPacket(0x04, "");
        DataPacket dp = new DataPacket(pc.ReadPacket(0x04));
        return dp.getLongValue(0);
    }

    public synchronized void beginSales(int typeDoc, String cashier, long checkNumber) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putLongValue(Long.valueOf(typeDoc));
        dp.putLongValue(Long.valueOf(Long.parseLong(config.getDepart())));
        dp.putStringValue(cashier);
        dp.putLongValue(Long.valueOf(checkNumber));
        pc.SendPacket(48, dp.getDataBuffer());
        pc.ReadPacket(48);
    }

    public synchronized void resetDoc() throws Exception {
        pc.SendPacket(0, "");
        DataPacket dp = new DataPacket(pc.ReadPacket(0));
        long stateDocument = dp.getLongValue(2);
        boolean errorCloseDoc = ((stateDocument & 0x80) == 0x80);
        boolean openDoc = ((stateDocument & 15) > 0);

        if (errorCloseDoc) {
            if(!bayerAddress.equals("")){
                DataPacket dpBayerAdress = new DataPacket();
                dpBayerAdress.putStringValue(bayerAddress);
                pc.SendPacket(0x31, dpBayerAdress.getDataBuffer());
                pc.ReadPacket(0x31);
            } else {
                pc.SendPacket(0x31, "");
                pc.ReadPacket(0x31);
            }
        } else if (openDoc) {
            pc.SendPacket(0x32, "");
            pc.ReadPacket(0x32);
        }
    }

    public synchronized void setTrailerLine(int lineNumber, String text) throws Exception {
        setStrValueTableSettings(31L, lineNumber, text);
    }

    public synchronized void setHeaderLine(int lineNumber, String text) throws Exception {
        setStrValueTableSettings(30L, lineNumber, text);
    }

    public synchronized String getDate() throws Exception {
        pc.SendPacket(19, "");
        DataPacket dp = new DataPacket(pc.ReadPacket(19));
        return dp.getDateValueFullFormat();
    }

    public synchronized void setDate(String sdate) throws Exception {
        DateFormat df = new SimpleDateFormat("ddMMyyyyHHmm");
        Date date = df.parse(sdate);

        DataPacket dp = new DataPacket();
        dp.putDateValue(date);
        dp.putTimeValue(date);

        pc.SendPacket(20, dp.getDataBuffer());
        pc.ReadPacket(20);
    }

    private synchronized DataPacket getDataFromPirit(int command, long req) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putLongValue(req);
        dp = new DataPacket(pc.ReadPacket(command, readTimeOut, pc.SendPacket(command, dp.getDataBuffer())));
        return dp;
    }

    public synchronized long getFirmWare() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x02, 2L);
        return dp.getLongValue(1);
    }

    public synchronized String getRegNumberFR() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x02, 4L);
        return dp.getStringValue(1);
    }

    public synchronized String getPrinterId() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x02, 1L);
        return dp.getStringValue(1);
    }

    public synchronized Date getEndDateTimeResouarcePrinter() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x02, 14L);
        return dp.getDateValue(1);
    }

    public synchronized long getNumberOfCurretnShift() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x01, 1L);
        long lRes = dp.getLongValue(1);
        return lRes;
    }

    public synchronized long getAmountReceiptsOfCurretnShift() throws Exception {
        DataPacket  dp = getDataFromPirit(0x01, 7L);
        long lRes1 = dp.getLongValue(1);
        long lRes2 = dp.getLongValue(2);
        long lRes3 = dp.getLongValue(3);
        long lRes4 = dp.getLongValue(4);
        long lRes5 = dp.getLongValue(5);
        long lRes6 = dp.getLongValue(6);
        long lRes = lRes1+lRes2+lRes3+lRes4+lRes5+lRes6;
        return lRes;
    }

    public synchronized long getDailyTotal(int type) throws Exception {
        Log.debug("begin getDailyTotal(" + type + ")");
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x01, 3L);

        long result = 0L;
        if (type == 0) {
            for (int i = 0; i < 16; i++) {
                result += (long) (dp.getDoubleValue(1 + i) * 100.0D);
            }
        } else {
            for (int i = 0; i < 16; i++) {
                if (type == i) {
                    result += (long) (dp.getDoubleValue(1 + i) * 100.0D);
                }
            }
        }
        Log.debug("end getDailyTotal:" + result);
        return result;
    }

    public synchronized long getCountVoidReceipts() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x01, 7L);
        return dp.getLongValue(3);
    }

    public synchronized long getCountFiscalReceipts(int num) throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x01, 7L);
        long lRes;

        switch (num) {
        case 0:// DOCTYPE_RETAIL_SALES_RECEIPT
            //lRes = dp.getLongValue(1); // Кол-во чеков продажи
             lRes = getLastFiscalNumberDoc(); //Номер фискального признака
            // документа
            break;
        case 1:// DOCTYPE_SIMPLIFIED_INVOICE
          //  lRes = dp.getLongValue(2); // Кол-во чеков возврата
             lRes = getLastFiscalNumberDoc(); //Номер фискального признака
            // документа
            break;
        case 2:// DOCTYPE_NON_FISCAL_DOCUMENT
            lRes = 0;
            break;
        case 3:// DOCTYPE_RETAIL_SALES_VOID_RECEIPT
            lRes = dp.getLongValue(3);
            break;
        case 4:// DOCTYPE_RETAIL_SALES_REFUND_RECEIPT
            lRes = dp.getLongValue(2);
            break;
        case 5:// DOCTYPE_CASH_IN_RECEIPT
            lRes = dp.getLongValue(5);
            break;
        case 6:// DOCTYPE_CASH_OUT_RECEIPT
            lRes = dp.getLongValue(6);
            break;
        default:
            lRes = (dp.getLongValue(1) + dp.getLongValue(2));
            break;
        }
        return lRes;
    }

    public synchronized long getDailyRefundTotal() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x01, 5L);
        long result = 0L;
        for (byte i = 0; i < 16; i = (byte) (i + 1)) {
            result += (long) (dp.getDoubleValue(1 + i) * 100.0D);
        }
        return result;
    }

    public synchronized long getGrandTotalSumms() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x02, 12L);
        long lRes;
        lRes = (long) (dp.getDoubleValue(1) * 100.0D);
        return lRes;
    }

    public synchronized long getCountFiscalSaleReceipts() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x01, 7L);
        return dp.getLongValue(1);
    }

    public synchronized long getDiscountDailyTotal() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x01, 9L);
        return (long) (dp.getDoubleValue(1) * 100.0D);
    }

    public synchronized long getReceiptTotal() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x03, 1L);
        return (long) (dp.getDoubleValue(1) * 100.0D);
    }

    public synchronized long getDiscountReceiptTotal() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x03, 1L);
        return (long) (dp.getDoubleValue(2) * 100.0D);
    }

    public synchronized long getSurchargeReceiptTotal() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x03, 1L);
        return (long) (dp.getDoubleValue(3) * 100.0D);
    }

    public synchronized long getSurchargeDailyTotal() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x01, 9L);
        return (long) (dp.getDoubleValue(2) * 100.0D);
    }

    public synchronized long getTypeFiscalReceipt() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x03, 2L);
        return dp.getLongValue(1);
    }

    public synchronized long getShiftNumber() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x01, 1L);
        return dp.getLongValue(1);
    }

    public synchronized long getCashAmount(int num) throws Exception {
        DataPacket dp = new DataPacket();
        long res;
        switch (num) {
        case 112:// REGISTER_UNNULL_TOTAL_COMING_START_SHIFT
            dp = getDataFromPirit(0x01, 12L);
            res = (long) (dp.getDoubleValue(5) * 100.0D);
            break;
        case 113:// REGISTER_UNNULL_TOTAL_REFOUND_START_SHIFT
            dp = getDataFromPirit(0x01, 12L);
            res = (long) (dp.getDoubleValue(7) * 100.0D);
            break;
        case 114:// REGISTER_UNNULL_TOTAL_EXPEND_START_SHIFT
            dp = getDataFromPirit(0x01, 16L);
            res = 0;
            for(int i = 1; i<16; i++){
                res =+ (long) (dp.getDoubleValue(i) * 100.0D);
            }
            break;
        case 115:// REGISTER_UNNULL_TOTAL_EXPEND_REFOUND_START_SHIFT
            dp = getDataFromPirit(0x01, 17L);
            res = 0;
            for(int i = 1; i<16; i++){
                res =+ (long) (dp.getDoubleValue(i) * 100.0D);
            }
            break;
        case 212:// REGISTER_UNNULL_TOTAL_COMING_START_REG_FR
            dp = getDataFromPirit(0x02, 12L);
            res = (long) (dp.getDoubleValue(1) * 100.0D);
            break;
        case 213:// REGISTER_UNNULL_TOTAL_REFOUND_START_REG_FR
            dp = getDataFromPirit(0x02, 12L);
            res = (long) (dp.getDoubleValue(2) * 100.0D);
            break;
        case 214:// REGISTER_UNNULL_TOTAL_EXPEND_START_REG_FR
            dp = getDataFromPirit(0x02, 12L);
            res = (long) (dp.getDoubleValue(3) * 100.0D);
            break;
        case 215:// REGISTER_UNNULL_TOTAL__EXPEND_REFOUND_START_REG_FR
            dp = getDataFromPirit(0x02, 12L);
            res = (long) (dp.getDoubleValue(4) * 100.0D);
            break;
        case 121:// REGISTER_DAY_TOTAL_SALE_ALL
             dp = getDataFromPirit(0x01, 3L);
             res = 0;
             for(int i = 1; i<16; i++){
                res =+ (long) (dp.getDoubleValue(i) * 100.0D);
             }
             break;
        case 123:// REGISTER_DAILY_TOTAL_REFUND_ALL
             dp = getDataFromPirit(0x01, 5L);
             res = 0;
             for(int i = 1; i<16; i++){
                  res =+ (long) (dp.getDoubleValue(i) * 100.0D);
             }
             break;
        case 185:// REGISTER_DAILY_TOTAL_DISCOUNT_SALE_PAYMENT1
             dp = getDataFromPirit(0x01, 9L);
             res = (long) (dp.getDoubleValue(1) * 100.0D);
             break;
        case 187:// REGISTER_DAILY_TOTAL_DISCOUNT_REFOUND_PAYMENT3
             dp = getDataFromPirit(0x01, 9L);
             res = (long) (dp.getDoubleValue(3) * 100.0D);
             break;
        case 193:// REGISTER_DAILY_TOTAL_SALE_PAYMENT1
            dp = getDataFromPirit(0x01, 3L);
            res = (long) (dp.getDoubleValue(1) * 100.0D);
            break;
        case 197:// REGISTER_DAILY_TOTAL_SALE_PAYMENT2
            dp = getDataFromPirit(0x01, 3L);
            res = (long) (dp.getDoubleValue(2) * 100.0D);
            break;
        case 201:// REGISTER_DAILY_TOTAL_SALE_PAYMENT3
            dp = getDataFromPirit(0x01, 3L);
            res = (long) (dp.getDoubleValue(3) * 100.0D);
            break;
        case 205:// REGISTER_DAILY_TOTAL_SALE_PAYMENT4
            dp = getDataFromPirit(0x01, 3L);
            res = (long) (dp.getDoubleValue(4) * 100.0D);
            break;
        case 195:// REGISTER_DAILY_TOTAL_REFUND_PAYMENT1
            dp = getDataFromPirit(0x01, 5L);
            res = (long) (dp.getDoubleValue(1) * 100.0D);
            break;
        case 199:// REGISTER_DAILY_TOTAL_REFUND_PAYMENT2
            dp = getDataFromPirit(0x01, 5L);
            res = (long) (dp.getDoubleValue(2) * 100.0D);
            break;
        case 203:// REGISTER_DAILY_TOTAL_REFUND_PAYMENT3
            dp = getDataFromPirit(0x01, 5L);
            res = (long) (dp.getDoubleValue(3) * 100.0D);
            break;
        case 207:// REGISTER_DAILY_TOTAL_REFUND_PAYMENT4
            dp = getDataFromPirit(0x01, 5L);
            res = (long) (dp.getDoubleValue(4) * 100.0D);
            break;
        case 242:// REGISTER_DAILY_TOTAL_CASH_IN
            dp = getDataFromPirit(0x01, 8L);
            res = (long) (dp.getDoubleValue(3) * 100.0D);
            break;
        case 243:// REGISTER_DAILY_TOTAL_CASH_OUT
            dp = getDataFromPirit(0x01, 8L);
            res = (long) (dp.getDoubleValue(4) * 100.0D);
            break;
        default:
            dp = getDataFromPirit(0x02, 7L);
            res = (long) (dp.getDoubleValue(1) * 100.0D);
            break;
        }

        return res;
    }

    public synchronized long getLastDocNumber() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x03, 2L);
        return dp.getLongValue(4);
    }

    public synchronized long getDocStatus() throws Exception {
        StringBuilder result = new StringBuilder();
        pc.SendPacket(0, "");
        DataPacket dp = new DataPacket(pc.ReadPacket(0));
        long docStatus = dp.getLongValue(2);
        Log.debug("DocStatus is(" + docStatus + ")");
        return docStatus;
    }

    public synchronized long getVatValue(int vatId) throws Exception {
        return (long) (Float.parseFloat(getStrValueTableSettings(41L, vatId)) * 100.0F);
    }

    public synchronized void setVatValue(int vatId, String vatValue) throws Exception {
        setStrValueTableSettings(41L, vatId, vatValue);
    }

    public synchronized String getVatName(int vatId) throws Exception {
        return getStrValueTableSettings(40L, vatId);
    }

    public synchronized void setVatName(int vatId, String vatName) throws Exception {
        setStrValueTableSettings(40L, vatId, vatName);
    }

    public synchronized void printMoney(long total, String description) throws Exception {
        Log.debug("printMoney:" + total);
        DataPacket dp = new DataPacket();
        dp.putStringValue(description);
        dp.putDoubleValue(total / 100.0D);
        pc.SendPacket(72, dp.getDataBuffer());
        if(!bEnablePacketPrint) {
            pc.ReadPacket(72);
        }
    }

    public synchronized void addGoods(String item, long price, int quantity, int vatId, long unitPrice, String unitName) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putStringValue(item); // поменены местами для Х5
        dp.putStringValue(unitName);// поменены местами для Х5
        dp.putDoubleValue(quantity / 1000.0D);
        dp.putDoubleValue(unitPrice / 100.0D);
        dp.putLongValue(Long.valueOf(vatId));
        dp.putStringValue(null);// Номер товарной позиции
        dp.putLongValue(0L);// Номер секции
        dp.putLongValue(0L);// Тип скидки/наценки
        dp.putStringValue(null);// Название скидки/наценки,
        dp.putDoubleValue(0D);// Процент или сумма скидки/наценки.
        pc.SendPacket(0x42, dp.getDataBuffer());
        if(!bEnablePacketPrint) {
            pc.ReadPacket(0x42);
        }
    }

    public synchronized void addDiscount(int adjustmentType, String name, long amount) throws Exception {
        DataPacket dp = new DataPacket();
        if (adjustmentType == 1)
            dp.putLongValue(1L);
        else
            dp.putLongValue(0L);

        dp.putStringValue(name);
        dp.putDoubleValue(amount / 100.0D);
        pc.SendPacket(0x45, dp.getDataBuffer());
        if(!bEnablePacketPrint) {
            pc.ReadPacket(0x45);
        }
    }

    public synchronized void addMargin(int adjustmentType, String name, long amount) throws Exception {
        DataPacket dp = new DataPacket();
        if (adjustmentType == 2)
            dp.putLongValue(1L);
        else
            dp.putLongValue(0L);

        dp.putStringValue(name);
        dp.putDoubleValue(amount / 100.0D);
        pc.SendPacket(0x46, dp.getDataBuffer());
        if(!bEnablePacketPrint) {
            pc.ReadPacket(0x46);
        }
    }

    public synchronized void subtotal() throws Exception {
        pc.SendPacket(0x44, "");
        if(!bEnablePacketPrint) {
            pc.ReadPacket(0x44);
        }
    }

    public synchronized void setTotalReceipt(int sum) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putDoubleValue(sum / 100.0D);
        pc.SendPacket(0x64, dp.getDataBuffer());
//        pc.ReadPacket(0x64);
    }

    public synchronized void putPayment(long sum, long paymentId) throws Exception {
        DataPacket dp = new DataPacket();

        dp.putLongValue(Long.valueOf(paymentId));
        dp.putDoubleValue(sum / 100.0D);
        dp.putStringValue("");

        pc.SendPacket(0x47, dp.getDataBuffer());
        if(!bEnablePacketPrint) {
            pc.ReadPacket(0x47);
        }
    }

    public synchronized void putText(long textAttribute, String text) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putStringValue(text);
        dp.putLongValue(Long.valueOf(textAttribute));

        pc.SendPacket(0x40, dp.getDataBuffer());
        if(!bEnablePacketPrint) {
            pc.ReadPacket(0x40);
        }
    }

    public synchronized void putBarCode(long textPosition, long widthBarCode, long heightBarCode, long typeBarCode, String text) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putLongValue(Long.valueOf(textPosition));
        dp.putLongValue(Long.valueOf(widthBarCode));
        dp.putLongValue(Long.valueOf(heightBarCode));
        dp.putLongValue(Long.valueOf(typeBarCode));
        dp.putStringValue(text);

        pc.SendPacket(0x41, dp.getDataBuffer());
        if(!bEnablePacketPrint) {
            pc.ReadPacket(0x41);
        }
    }

    public synchronized void closeDoc() throws Exception {
        Log.info("Close Doc : BAYER_ADDRESS: " + bayerAddress);
        if (getDocStatus() != 0) {
            if(!bayerAddress.equals("")){
                DataPacket dpBayerAdress = new DataPacket();
                dpBayerAdress.putStringValue(bayerAddress);
                pc.SendPacket(0x31, dpBayerAdress.getDataBuffer());
                pc.ReadPacket(0x31);
            } else {
                pc.SendPacket(0x31, "");
                pc.ReadPacket(0x31);
            }
        }
    }

    public synchronized void addRequesit(long textAttrib, String dataReq) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putLongValue(0L);
        dp.putLongValue(textAttrib);
        dp.putStringValue(dataReq);
        dp.putLongValue(null);
        dp.putLongValue(null);
        dp.putLongValue(null);

        pc.SendPacket(0x49, dp.getDataBuffer());
        if(!bEnablePacketPrint) {
            pc.ReadPacket(0x49);
        }
    }

    public synchronized void addReqForOFD(long reqCode, long textAttrib, String nameReq, String dataReq) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putLongValue(reqCode);
        dp.putLongValue(textAttrib);
        dp.putStringValue(nameReq);
        dp.putStringValue(dataReq);

        pc.SendPacket(0x57, dp.getDataBuffer());
        pc.ReadPacket(0x57);
    }

    public synchronized void printFiscalReportByDate(String date1, String date2) throws Exception {
        DateFormat df = new SimpleDateFormat("ddMMyyyyhhmm");
        DataPacket dp = new DataPacket();
        dp.putLongValue(1L);
        dp.putDateValue(df.parse(date1));
        dp.putDateValue(df.parse(date2));
        dp.putStringValue(config.getPassword());

        pc.SendPacket(0x62, dp.getDataBuffer());
        pc.ReadPacket(0x62);
    }

    public synchronized void printFiscalReportByShiftId(long startShiftId, long endShiftId) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putLongValue(1L);
        dp.putLongValue(startShiftId);
        dp.putLongValue(endShiftId);
        dp.putStringValue(config.getPassword());

        pc.SendPacket(0x61, dp.getDataBuffer());
        pc.ReadPacket(0x61);
    }

    public synchronized void printCopyZReport() throws Exception {
        pc.SendPacket(0xA1, "");
        pc.ReadPacket(0xA1);
    }

    public synchronized void printXReport(String cashier) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putStringValue(cashier);

        pc.SendPacket(0x20, dp.getDataBuffer());
        pc.ReadPacket(0x20);
    }

    public synchronized void printZReport(String cashier) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putStringValue(cashier);

        pc.SendPacket(0x21, dp.getDataBuffer());
        pc.ReadPacket(0x21);
    }

    public synchronized void openShift(String cashier) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putStringValue(cashier);

        pc.SendPacket(0x23, dp.getDataBuffer());
        pc.ReadPacket(0x23);
    }

    public synchronized void openMoneyDrawer() throws Exception {
        pc.SendPacket(0x80, "");
        pc.ReadPacket(0x80);
    }

    public synchronized Boolean isMoneyDrawerOpen() throws Exception {
        pc.SendPacket(0x81, "");
        DataPacket dp = new DataPacket(pc.ReadPacket(0x81));
        if (dp.getLongValue(0) == 1L)
            return true;
        return false;
    }

    public synchronized String getFiscalOfdReceiptFromRepo(long receipNumbder) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putLongValue(12L);
        dp.putLongValue(receipNumbder);
        System.out.println("dp "+dp.toString());
        pc.SendPacket(0x78, dp.getDataBuffer());
        String result = pc.ReadPacket(0x78);
        return result;
    }

    public synchronized String getRegFiscalNumber() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x78, 1L);
        String lRes = dp.getStringValue(1);
        return lRes;
    }

    public synchronized long getOFDStatusAmountUnsendedDocs() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x78, 7L);
        long lRes = dp.getLongValue(2);
        return lRes;
    }

    public synchronized Date getOFDStatusDateFirstUnsendedDocs() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x78, 7L);
        Date lRes = dp.getDateValue(4);
        return lRes;
    }

    public synchronized Date getDateTimeRegFiscalNumber() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x78, 4L);
        Date lRes = dp.getTimeValue(1);
        return lRes;
    }

    public synchronized long getNumberDocOfLastReregistration() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x78, 5L);
        long lRes = dp.getLongValue(1);
        return lRes;
    }

    public synchronized long getLastFiscalNumberDoc() throws Exception {
        DataPacket dp = new DataPacket();
        dp = getDataFromPirit(0x78, 3L);
        long lRes = dp.getLongValue(1);
        return lRes;
    }

    public synchronized Vector<PiritFNTicket> getSignedFiscalDocsFromMemory(int[] arg1) throws Exception {
        Vector<PiritFNTicket> tickets = getFiscalDocsFromMemory(arg1);
        Vector<PiritFNTicket> resultTickets = new Vector<PiritFNTicket>();
        for(PiritFNTicket ticket :  tickets) {
            if(ticket.isSigned()){
                resultTickets.add(ticket);
             }
          }
        return resultTickets;
    }

    public synchronized Vector<PiritFNTicket> getRangeFiscalDocsFromMemory(int[] arg1) throws Exception {
        int lastNumber = (int) getLastFiscalNumberDoc();
        int[] range;
        if(arg1[0]>=lastNumber){
            range = new int[1];
            range[0] = lastNumber;
        } else {
            range = new int[lastNumber-arg1[0]+1];
            for (int i = 0; i<range.length; i++){
                range[i]=arg1[0]+i;
            }
        }
        Vector<PiritFNTicket> tickets = getFiscalDocsFromMemory(range);
        return tickets;
    }

    public synchronized Vector<PiritFNTicket> getSortCountFiscalDocsFromMemory(int[] arg1) throws Exception {
        Vector<PiritFNTicket> tickets = new Vector<PiritFNTicket>();
        int[] range;
        int requestAmount = arg1[1];
        int startRecepieNumber = arg1[0];
        int lastNumber = (int) getLastFiscalNumberDoc();

        if(startRecepieNumber>=lastNumber){
            range = new int[1];
            range[0] = lastNumber;
        } else {
            int avialableAmount = lastNumber - startRecepieNumber;
              if(avialableAmount<requestAmount)
               {
                requestAmount = avialableAmount+1;
               }

                range = new int[requestAmount];
                for (int i = 0; i<range.length; i++){
                  range[i]=startRecepieNumber+i;
                }

        }
        tickets = getFiscalDocsFromMemory(range);
        return tickets;
    }

    public synchronized Vector<PiritFNTicket> getFiscalDocsFromMemory(int[] arg1) throws Exception {
        Vector<PiritFNTicket> tickets = new Vector<PiritFNTicket>();
        for(int docNumber :  arg1) {
            DataPacket dp = new DataPacket();
            String docHEX = null;
            dp.putLongValue(11L);
            dp.putLongValue((long) docNumber);
            dp.putLongValue(null);
            pc.SendPacket(0x78, dp.getDataBuffer());
            try {
                docHEX = pc.ReadPacket(0x78);
            } catch (JposException e){
                if(e.getErrorCodeExtended()==72){
                    docHEX = null;
                }
            }
            if(docHEX!=null){
                try {
                    tickets.add(new PiritFNTicket(docHEX, docNumber));
                } catch(Exception e){
                        tickets.add(new PiritFNTicket(docNumber));
                }
            }
        }
        return tickets;
    }

    private String getStrValueTableSettings(long number, long index) throws Exception {
        String result = "";

        DataPacket dp = new DataPacket();
        dp.putLongValue(Long.valueOf(number));
        dp.putLongValue(Long.valueOf(index));

        pc.SendPacket(17, dp.getDataBuffer());
        dp = new DataPacket(pc.ReadPacket(17));
        if (dp.getCountValue() > 0) {
            result = dp.getStringValue(0);
        }

        return result;
    }

    private void setStrValueTableSettings(long number, long index, String value) throws Exception {
        DataPacket dp = new DataPacket();
        dp.putLongValue(Long.valueOf(number));
        dp.putLongValue(Long.valueOf(index));
        dp.putStringValue(value);

        pc.SendPacket(18, dp.getDataBuffer());
        pc.ReadPacket(18);
    }

    public static boolean getBit(long number, int bitNum) {
        return ((number & (1 << bitNum)) > 0L);
    }

    public synchronized String getPhysicalDescription() {
        return physicalDescription;
    }

    public synchronized String getPhysicalName() {
        return physicalName;
    }

    public synchronized String getServiceDescription() {
        return getServiceDescription;
    }
}