package ru.crystals.pos.fiscalprinter.jpos.pirit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import jpos.BaseControl;
import jpos.JposConst;
import jpos.JposException;
import jpos.config.JposEntry;
import jpos.events.*;
import jpos.loader.JposServiceLoader;
import jpos.FiscalPrinterConst;
import jpos.services.EventCallbacks;
import jpos.services.FiscalPrinterService112;
import org.apache.log4j.Logger;

import ru.crystals.pos.fiscalprinter.jpos.pirit.connector.DocumentType;
import ru.crystals.pos.fiscalprinter.jpos.pirit.connector.PiritErrorMsg;
import ru.crystals.pos.fiscalprinter.jpos.pirit.connector.PiritFNTicket;
import ru.crystals.pos.fiscalprinter.jpos.pirit.connector.PiritService;
import ru.crystals.pos.fiscalprinter.jpos.pirit.events.StatusUpdateEventRequest;

import static jpos.FiscalPrinterConst.*;
import static jpos.JposConst.*;

public class PiritSvc112 implements FiscalPrinterService112 {
    private static final Logger Log = Logger.getLogger(PiritSvc112.class);
    protected int iActualCurrency = FiscalPrinterConst.FPTR_AC_RUR;
    protected String strAdditionalHeader = "";
    protected boolean bCapAdditionalHeader = false;
    protected String strAdditionalTrailer = "";
    protected boolean bCapAdditionalTrailer = false;
    protected int iAmountDecimalPlaces = 2;
    protected boolean bAsyncMode = false;
    protected boolean bCapAdditionalLines = false;
    protected boolean bCapAmountAdjustment = true;
    protected boolean bCapAmountNotPaid = false;
    protected boolean bCapChangeDue = false;
    protected boolean bCapCheckTotal = false;
    protected boolean bCapCoverSensor = true;
    protected boolean bCapDoubleWidth = false;
    protected boolean bCapDuplicateReceipt = false;
    protected boolean bCapEmptyReceiptIsVoidable = true;
    protected boolean bCapFiscalReceiptStation = false;
    protected boolean bCapFiscalReceiptType = true;
    protected boolean bCapFixedOutput = false;
    protected boolean bCapHasVatTable = true;
    protected boolean bCapIndependentHeader = true;
    protected boolean bCapItemList = false;
    protected boolean bCapJrnEmptySensor = true;
    protected boolean bCapJrnNearEndSensor = false;
    protected boolean bCapJrnPresent = false;
    protected boolean bCapMultiContractor = false;
    protected boolean bCapNonFiscalMode = true;
    protected boolean bCapOnlyVoidLastItem = false;
    protected boolean bCapOrderAdjustmentFirst = false;
    protected boolean bCapPackageAdjustment = false;
    protected boolean bCapPercentAdjustment = false;
    protected boolean bCapPositiveAdjustment = true;
    protected boolean bCapPositiveSubtotalAdjustment = true;
    protected boolean bCapPostPreLine = false;
    protected boolean bCapPowerLossReport = false;
    protected boolean bCapPredefinedPaymentLines = false;
    protected boolean bCapReceiptNotPaid = false;
    protected boolean bCapRecEmptySensor = true;
    protected boolean bCapRecNearEndSensor = false;
    protected boolean bCapRecPresent = true;
    protected boolean bCapRemainingFiscalMemory = false;
    protected boolean bCapReservedWord = false;
    protected String strReservedWord = "";
    protected boolean bCapSetCurrency = false;
    protected boolean bCapSetHeader = true;
    protected boolean bCapSetPOSID = true;
    protected boolean bCapSetStoreFiscalID = true;
    protected boolean bCapSetTrailer = true;
    protected boolean bCapSetVatTable = true;
    protected boolean bCapSlpEmptySensor = false;
    protected boolean bCapSlpFiscalDocument = false;
    protected boolean bCapSlpFullSlip = false;
    protected boolean bCapSlpNearEndSensor = false;
    protected boolean bCapSlpPresent = false;
    protected boolean bCapSlpValidation = false;
    protected boolean bCapSubAmountAdjustment = true;
    protected boolean bCapSubPercentAdjustment = true;
    protected boolean bCapSubtotal = true;
    protected boolean bCapTotalizerType = true;
    protected boolean bCapTrainingMode = false;
    protected boolean bCapValidateJournal = false;
    protected boolean bCapXReport = true;
    protected String strChangeDue = "";
    protected boolean bCheckTotal = false;
    protected int iContractorId = FiscalPrinterConst.FPTR_CID_SINGLE;
    protected int iCountryCode = FiscalPrinterConst.FPTR_CC_RUSSIA;

    protected int iDateType = FiscalPrinterConst.FPTR_DT_RTC;
    protected boolean bDayOpened = false;
    protected int iDescriptionLength = 100;
    protected boolean bDuplicateReceipt = false;
    protected int iErrorLevel = FiscalPrinterConst.FPTR_EL_NONE;
    protected int iErrorOutID = 0;
    protected int iErrorState = 0;
    protected int iErrorStation = 0;
    protected String strErrorString = "";
    protected int iFiscalReceiptStation = 1;
    protected int iFiscalReceiptType = FiscalPrinterConst.FPTR_RT_SALES;
    protected boolean bFlagWhenIdle = false;
    protected int iMessageLength = 56;
    protected int iMessageType = FiscalPrinterConst.FPTR_MT_FREE_TEXT;
    protected int iNumHeaderLines = 4;
    protected int iNumTrailerLines = 5;
    protected int iNumVatRates = 6;
    protected String strPostLine = "";
    protected String strPredefinedPaymentLines = "";
    protected String strPreLine = "";
    protected int iPrinterState = 1;
    protected int iQuantityDecimalPlaces = 3;
    protected int iQuantityLength = 8;
    protected int iRemainingFiscalMemory = 0;
    protected boolean bSlpEmpty = false;
    protected boolean bSlpNearEnd = false;
    protected int iSlipSelection = 1;
    protected int iTotalizerType = FiscalPrinterConst.FPTR_TT_DAY;
    protected boolean bTrainingModeActive = false;
    protected boolean bCapServiceAllowManagement = false;
    protected int iState = 1;
    protected boolean bClaimed = false;
    protected boolean bDeviceEnabled = false;
    protected boolean bCoverOpen = false;
    protected volatile boolean bPaperEnd = false;
    protected boolean bPrinterOnline = false;
    protected boolean bCapCompareFirmwareVersion = false;
    protected int iCapPowerReporting = 1;
    protected boolean bCapStatisticsReporting = false;
    protected boolean bCapUpdateFirmware = false;
    protected boolean bCapUpdateStatistics = false;
    protected String strHealthText = "";
    protected static final int iServiceVersion = 1013016;
    protected int iPowerNotify = 0;
    protected int iOutputID = -1;
    protected Vector directIOListeners;
    protected Vector errorListeners;
    protected Vector outputCompleteListeners;
    protected Vector statusUpdateListeners;
    private final Vector events = new Vector();
    private boolean bOpened = false;
    private boolean freezeEvents = true;
    private String strCashierId = "";
    private int iCheckNumber = 0;
    private int iResultCode = 0;
    private Map<Integer, String> vatTable = new HashMap();
    private boolean bPrintBarCode = false;
    private int iTextAttribute = 0;
    private int iTextPosition = 0;
    private int iWidthBarCode = 3;
    private int iHeightBarCode = 100;
    private int iTypeBarCode = 4;
    private EventCallbacks cb = null;
    private Thread eventThread = null;
    private Thread deviceThread = null;
    private boolean eventThreadEnabled = false;
    private boolean deviceThreadEnabled = false;
    private boolean stopEvents;
    private int powerState = 0;
    private int goodsCounter = 0;
    protected boolean bEnableGoodsCounter = true;
    protected boolean bEnablePacketPrint = false;
    private PropertyChangeListener exeptionListener = null;


    public PiritSvc112() {
        Log.info("****************************************************************");
        Log.info("*           JavaPos For Fiscal Printer Pirit 2F/2SF   v0.72    *");
        Log.info("*  Version: " + iServiceVersion / 1000000 + "." + (iServiceVersion % 1000000) / 1000 + "." + (iServiceVersion % 1000)
                + "                     JSSC               *");
        Log.info("****************************************************************");
        directIOListeners = new Vector();
        errorListeners = new Vector();
        outputCompleteListeners = new Vector();
        statusUpdateListeners = new Vector();
        exeptionListener = new ExeptionListener();
    }

    @Override
    public void printRecItemRefund(String arg0, long arg1, int arg2, int arg3, long arg4, String arg5) throws JposException {
        printRecItem(arg0, arg1, arg2, arg3, arg4, arg5);
        // throw new JposException(JposConst.JPOS_E_ILLEGAL,
        // "Method is not supported by this service");
    }

    @Override
    public void printRecItemRefundVoid(String arg0, long arg1, int arg2, int arg3, long arg4, String arg5) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public boolean getCapPositiveSubtotalAdjustment() throws JposException {
        Log.info("getCapPositiveSubtotalAdjustment:" + this.bCapPositiveSubtotalAdjustment);
        return this.bCapPositiveSubtotalAdjustment;
    }

    @Override
    public void printRecItemAdjustmentVoid(int arg0, String arg1, long arg2, int arg3) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void printRecItemVoid(String arg0, long arg1, int arg2, int arg3, long arg4, String arg5) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void compareFirmwareVersion(String arg0, int[] arg1) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public boolean getCapCompareFirmwareVersion() throws JposException {
        Log.info("getCapCompareFirmwareVersion:" + this.bCapCompareFirmwareVersion);
        return this.bCapCompareFirmwareVersion;
    }

    @Override
    public boolean getCapUpdateFirmware() throws JposException {
        Log.info("getCapUpdateFirmware:" + this.bCapUpdateFirmware);
        return this.bCapUpdateFirmware;
    }

    @Override
    public void updateFirmware(String arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public boolean getCapStatisticsReporting() throws JposException {
        Log.info("getCapStatisticsReporting:" + this.bCapStatisticsReporting);
        return this.bCapStatisticsReporting;
    }

    @Override
    public boolean getCapUpdateStatistics() throws JposException {
        Log.info("getCapUpdateStatistics:" + this.bCapUpdateStatistics);
        return this.bCapUpdateStatistics;
    }

    @Override
    public void resetStatistics(String arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void retrieveStatistics(String[] arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void updateStatistics(String arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public int getAmountDecimalPlaces() throws JposException {
        Log.info("getAmountDecimalPlaces:" + this.iAmountDecimalPlaces);
        return this.iAmountDecimalPlaces;
    }

    @Override
    public int getActualCurrency() throws JposException {
        Log.info("getActualCurrency:" + this.iActualCurrency);
        return this.iActualCurrency;
    }

    @Override
    public String getAdditionalHeader() throws JposException {
        Log.info("getAdditionalHeader:" + this.strAdditionalHeader);
        return this.strAdditionalHeader;
    }

    @Override
    public String getAdditionalTrailer() throws JposException {
        Log.info("getAdditionalTrailer:" + this.strAdditionalTrailer);
        return this.strAdditionalTrailer;
    }

    @Override
    public boolean getCapAdditionalHeader() throws JposException {
        Log.info("getCapAdditionalHeader:" + this.bCapAdditionalHeader);
        return this.bCapAdditionalHeader;
    }

    @Override
    public boolean getCapAdditionalTrailer() throws JposException {
        Log.info("getCapAdditionalTrailer:" + this.bCapAdditionalTrailer);
        return this.bCapAdditionalTrailer;
    }

    @Override
    public boolean getCapChangeDue() throws JposException {
        Log.info("getCapChangeDue:" + this.bCapChangeDue);
        return this.bCapChangeDue;
    }

    @Override
    public boolean getCapEmptyReceiptIsVoidable() throws JposException {
        Log.info("getCapEmptyReceiptIsVoidable:" + this.bCapEmptyReceiptIsVoidable);
        return this.bCapEmptyReceiptIsVoidable;
    }

    @Override
    public boolean getCapFiscalReceiptStation() throws JposException {
        Log.info("getCapFiscalReceiptStation:" + this.bCapFiscalReceiptStation);
        return this.bCapFiscalReceiptStation;
    }

    @Override
    public boolean getCapFiscalReceiptType() throws JposException {
        Log.info("getCapFiscalReceiptType:" + this.bCapFiscalReceiptType);
        return this.bCapFiscalReceiptType;
    }

    @Override
    public boolean getCapMultiContractor() throws JposException {
        Log.info("getCapMultiContractor:" + this.bCapMultiContractor);
        return this.bCapMultiContractor;
    }

    @Override
    public boolean getCapOnlyVoidLastItem() throws JposException {
        Log.info("getCapOnlyVoidLastItem:" + this.bCapOnlyVoidLastItem);
        return this.bCapOnlyVoidLastItem;
    }

    @Override
    public boolean getCapPackageAdjustment() throws JposException {
        Log.info("getCapPackageAdjustment:" + this.bCapPackageAdjustment);
        return this.bCapPackageAdjustment;
    }

    @Override
    public boolean getCapPostPreLine() throws JposException {
        Log.info("getCapPostPreLine:" + this.bCapPostPreLine);
        return this.bCapPostPreLine;
    }

    @Override
    public boolean getCapSetCurrency() throws JposException {
        Log.info("getCapSetCurrency:" + this.bCapSetCurrency);
        return this.bCapSetCurrency;
    }

    @Override
    public boolean getCapTotalizerType() throws JposException {
        Log.info("getCapTotalizerType:" + this.bCapTotalizerType);
        return this.bCapTotalizerType;
    }

    @Override
    public String getChangeDue() throws JposException {
        Log.info("getChangeDue:" + this.strChangeDue);
        return this.strChangeDue;
    }

    @Override
    public int getContractorId() throws JposException {
        Log.info("getContractorId:" + this.iContractorId);
        return this.iContractorId;
    }

    @Override
    public int getDateType() throws JposException {
        Log.info("getDateType:" + this.iDateType);
        return this.iDateType;
    }

    @Override
    public int getFiscalReceiptStation() throws JposException {
        Log.info("getFiscalReceiptStation:" + this.iFiscalReceiptStation);
        return this.iFiscalReceiptStation;
    }

    @Override
    public int getFiscalReceiptType() throws JposException {
        Log.info("getFiscalReceiptType:" + this.iFiscalReceiptType);
        return this.iFiscalReceiptType;
    }

    @Override
    public int getMessageType() throws JposException {
        Log.info("getMessageType:" + this.iMessageType);
        return this.iMessageType;
    }

    @Override
    public String getPostLine() throws JposException {
        if (this.bCapPostPreLine) {
            Log.info("getPostLine:" + this.strPostLine);
            return this.strPostLine;
        }
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public String getPreLine() throws JposException {
        if (this.bCapPostPreLine) {
            Log.info("getPreLine:" + this.strPreLine);
            return this.strPreLine;
        }
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public int getTotalizerType() throws JposException {
        if (this.bCapTotalizerType) {
            Log.info("getTotalizerType:" + this.iTotalizerType);
            return this.iTotalizerType;
        }
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void printRecCash(long amount) throws JposException {
        try {
            Log.debug("begin printRecCash: amount=" + amount);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            if ((this.iFiscalReceiptType != FiscalPrinterConst.FPTR_RT_CASH_IN) && (this.iFiscalReceiptType != FiscalPrinterConst.FPTR_RT_CASH_OUT)) {
                throw new JposException(JposConst.JPOS_E_ILLEGAL, "Error value FiscalReceiptType");
            }

            PiritService.getInstance().printMoney(amount, "");
            this.iState = 2;
            Log.info("printRecCash: amount=" + amount);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end printRecCash: amount=" + amount);
        } finally {
            Log.debug("end printRecCash: amount=" + amount);
        }
    }

    @Override
    public void printRecItemFuel(String arg0, long arg1, int arg2, int arg3, long arg4, String arg5, long arg6, String arg7) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void printRecItemFuelVoid(String arg0, long arg1, int arg2, long arg3) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void printRecPackageAdjustVoid(int arg0, String arg1) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void printRecPackageAdjustment(int arg0, String arg1, String arg2) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void printRecRefundVoid(String arg0, long arg1, int arg2) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void printRecSubtotalAdjustVoid(int arg0, long arg1) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void printRecTaxID(String arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void setAdditionalHeader(String arg0) throws JposException {
        if (this.bCapAdditionalHeader) {
            this.strAdditionalHeader = arg0;
            Log.info("setAdditionalHeader:" + this.strAdditionalHeader);
        } else {
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
        }
    }

    @Override
    public void setAdditionalTrailer(String arg0) throws JposException {
        if (this.bCapAdditionalTrailer) {
            this.strAdditionalTrailer = arg0;
            Log.info("setAdditionalTrailer:" + this.strAdditionalTrailer);
        } else {
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
        }
    }

    @Override
    public void setChangeDue(String arg0) throws JposException {
        if (this.bCapChangeDue) {
            this.strChangeDue = arg0;
            Log.info("setChangeDue:" + this.strChangeDue);
        } else {
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
        }
    }

    @Override
    public void setContractorId(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void setCurrency(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void setDateType(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void setFiscalReceiptStation(int arg0) throws JposException {
        if (this.bCapFiscalReceiptStation) {
            this.iFiscalReceiptStation = arg0;
            Log.info("setFiscalReceiptStation:" + this.iFiscalReceiptStation);
        } else {
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
        }
    }

    // Переписан
    @Override
    public void setFiscalReceiptType(int fiscalReceiptType) throws JposException {
        if (this.bCapFiscalReceiptType) {
            this.iFiscalReceiptType = fiscalReceiptType;
            Log.info("setFiscalReceiptType:" + this.iFiscalReceiptType);
        } else {
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
        }
    }

    @Override
    public void setMessageType(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void setPostLine(String arg0) throws JposException {
        if (this.bCapPostPreLine) {
            Log.info("setPostLine:" + arg0);
            this.strPostLine = arg0;
        } else {
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
        }
    }

    @Override
    public void setPreLine(String arg0) throws JposException {
        if (this.bCapPostPreLine) {
            Log.info("setPreLine:" + arg0);
            this.strPreLine = arg0;
        } else {
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
        }
    }

    @Override
    public void setTotalizerType(int arg0) throws JposException {
        if (this.bCapTotalizerType) {
            Log.info("setTotalizerType:" + arg0);
            this.iTotalizerType = arg0;
        } else {
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
        }
    }

    @Override
    public void beginFiscalDocument(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void beginFiscalReceipt(boolean arg0) throws JposException {
        try {
            Log.debug("begin beginFiscalReceipt");
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");
            int documentType = 0;
            switch (this.iFiscalReceiptType) {
            case FiscalPrinterConst.FPTR_RT_SALES:
                documentType = DocumentType.CHECK_SALE;
                if(bEnablePacketPrint){
                    documentType = DocumentType.PACKET_CHECK_SALE;
                }
                PiritService.getInstance().beginSales(documentType, this.strCashierId, this.iCheckNumber);
                Log.info("beginFiscalReceipt: CHECK_SALE type="+documentType);
                break;
            case FiscalPrinterConst.FPTR_RT_REFUND:
                 documentType = DocumentType.CHECK_REFUND;
                if(bEnablePacketPrint){
                    documentType = DocumentType.PACKET_CHECK_REFUND;
                }
                PiritService.getInstance().beginSales(documentType, this.strCashierId, this.iCheckNumber);
                Log.info("beginFiscalReceipt: CHECK_REFUND type="+documentType);
                break;
            case FiscalPrinterConst.FPTR_RT_CASH_IN:
                documentType = DocumentType.CASH_IN;
                if(bEnablePacketPrint){
                    documentType = DocumentType.PACKET_CASH_IN;
                }
                PiritService.getInstance().beginSales(DocumentType.CASH_IN, this.strCashierId, this.iCheckNumber);
                Log.info("beginFiscalReceipt: CASH_IN type="+documentType);
                break;
            case FiscalPrinterConst.FPTR_RT_CASH_OUT:
                documentType = DocumentType.CASH_OUT;
                if(bEnablePacketPrint){
                    documentType = DocumentType.PACKET_CASH_OUT;
                }
                PiritService.getInstance().beginSales(documentType, this.strCashierId, this.iCheckNumber);
                Log.info("beginFiscalReceipt: CASH_OUT type="+documentType);
                break;
            case FiscalPrinterConst.FPTR_RT_GENERIC:
            case FiscalPrinterConst.FPTR_RT_SERVICE:
            case FiscalPrinterConst.FPTR_RT_SIMPLE_INVOICE:
            default:
                throw new JposException(JposConst.JPOS_E_ILLEGAL, "error fiscalReceiptType");
            }

            this.iPrinterState = 2;
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end beginFiscalReceipt");
        } finally {
            Log.debug("end beginFiscalReceipt");
        }
    }

    @Override
    public void beginFixedOutput(int arg0, int arg1) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void beginInsertion(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void beginItemList(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void beginNonFiscal() throws JposException {
        try {
            Log.debug("begin beginNonFiscal");
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");
            int documentType = DocumentType.SERVICE;
            if(bEnablePacketPrint){
                documentType = DocumentType.PACKET_PRINT_SERVICE;
            }
            PiritService.getInstance().beginSales(documentType, this.strCashierId, this.iCheckNumber);
            Log.info("beginFiscalReceipt: FPTR_RT_SERVICE type="+documentType);
            this.iPrinterState = 9;
            Log.info("beginNonFiscal: FPTR_RT_SERVICE");
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end beginNonFiscal");
        } finally {
            Log.debug("end beginNonFiscal");
        }
    }

    @Override
    public void beginRemoval(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void beginTraining() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void clearError() throws JposException {
        this.strHealthText = "";
        clearResultCode();
    }

    @Override
    public void clearOutput() throws JposException {
    }

    @Override
    public void endFiscalDocument() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void endFiscalReceipt(boolean arg0) throws JposException {
        try {
            Log.debug("begin endFiscalReceipt");
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");
            this.bEnablePacketPrint = false;
            PiritService.getInstance().setbEnablePacketPrint(false);
            PiritService.getInstance().closeDoc();
            this.iPrinterState = 1;
            this.goodsCounter = 0;
            Log.info("endFiscalReceipt");
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end endFiscalReceipt");
        } finally {
            Log.debug("end endFiscalReceipt");
        }
    }

    @Override
    public void endFixedOutput() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void endInsertion() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void endItemList() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void endNonFiscal() throws JposException {
        try {
            Log.debug("begin endNonFiscal");
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");
            this.bEnablePacketPrint = false;
            PiritService.getInstance().setbEnablePacketPrint(false);
            PiritService.getInstance().closeDoc();
            this.iPrinterState = 1;
            Log.info("endNonFiscal");
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end endNonFiscal");
        } finally {
            Log.debug("end endNonFiscal");
        }
    }

    @Override
    public void endRemoval() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void endTraining() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public int getAmountDecimalPlace() throws JposException {
        Log.info("getAmountDecimalPlace:" + this.iAmountDecimalPlaces);
        return this.iAmountDecimalPlaces;
    }

    @Override
    public boolean getAsyncMode() throws JposException {
        Log.info("getAsyncMode:" + this.bAsyncMode);
        return this.bAsyncMode;
    }

    @Override
    public boolean getCapAdditionalLines() throws JposException {
        Log.info("getCapAdditionalLines:" + this.bCapAdditionalLines);
        return this.bCapAdditionalLines;
    }

    @Override
    public boolean getCapAmountAdjustment() throws JposException {
        Log.info("getCapAmountAdjustment:" + this.bCapAmountAdjustment);
        return this.bCapAmountAdjustment;
    }

    @Override
    public boolean getCapAmountNotPaid() throws JposException {
        Log.info("getCapAmountNotPaid:" + this.bCapAmountNotPaid);
        return this.bCapAmountNotPaid;
    }

    @Override
    public boolean getCapCheckTotal() throws JposException {
        Log.info("getCapCheckTotal:" + this.bCapCheckTotal);
        return this.bCapCheckTotal;
    }

    @Override
    public boolean getCapCoverSensor() throws JposException {
        Log.info("getCapCoverSensor:" + this.bCapCoverSensor);
        return this.bCapCoverSensor;
    }

    @Override
    public boolean getCapDoubleWidth() throws JposException {
        Log.info("getCapDoubleWidth:" + this.bCapDoubleWidth);
        return this.bCapDoubleWidth;
    }

    @Override
    public boolean getCapDuplicateReceipt() throws JposException {
        Log.info("getCapDuplicateReceipt:" + this.bCapDuplicateReceipt);
        return this.bCapDuplicateReceipt;
    }

    @Override
    public boolean getCapFixedOutput() throws JposException {
        Log.info("getCapFixedOutput:" + this.bCapFixedOutput);
        return this.bCapFixedOutput;
    }

    @Override
    public boolean getCapHasVatTable() throws JposException {
        Log.info("getCapHasVatTable:" + this.bCapHasVatTable);
        return this.bCapHasVatTable;
    }

    @Override
    public boolean getCapIndependentHeader() throws JposException {
        Log.info("getCapIndependentHeader:" + this.bCapIndependentHeader);
        return this.bCapIndependentHeader;
    }

    @Override
    public boolean getCapItemList() throws JposException {
        Log.info("getCapItemList:" + this.bCapItemList);
        return this.bCapItemList;
    }

    @Override
    public boolean getCapJrnEmptySensor() throws JposException {
        Log.info("getCapJrnEmptySensor:" + this.bCapJrnEmptySensor);
        return this.bCapJrnEmptySensor;
    }

    @Override
    public boolean getCapJrnNearEndSensor() throws JposException {
        Log.info("getCapJrnNearEndSensor:" + this.bCapJrnNearEndSensor);
        return this.bCapJrnNearEndSensor;
    }

    @Override
    public boolean getCapJrnPresent() throws JposException {
        Log.info("getCapJrnPresent:" + this.bCapJrnPresent);
        return this.bCapJrnPresent;
    }

    @Override
    public boolean getCapNonFiscalMode() throws JposException {
        Log.info("getCapNonFiscalMode:" + this.bCapNonFiscalMode);
        return this.bCapNonFiscalMode;
    }

    @Override
    public boolean getCapOrderAdjustmentFirst() throws JposException {
        Log.info("getCapOrderAdjustmentFirst:" + this.bCapOrderAdjustmentFirst);
        return this.bCapOrderAdjustmentFirst;
    }

    @Override
    public boolean getCapPercentAdjustment() throws JposException {
        Log.info("getCapPercentAdjustment:" + this.bCapPercentAdjustment);
        return this.bCapPercentAdjustment;
    }

    @Override
    public boolean getCapPositiveAdjustment() throws JposException {
        Log.info("getCapPositiveAdjustment:" + this.bCapPositiveAdjustment);
        return this.bCapPositiveAdjustment;
    }

    @Override
    public boolean getCapPowerLossReport() throws JposException {
        Log.info("getCapPowerLossReport:" + this.bCapPowerLossReport);
        return this.bCapPowerLossReport;
    }

    @Override
    public int getCapPowerReporting() throws JposException {
        Log.info("getCapPowerReporting:" + this.iCapPowerReporting);
        return this.iCapPowerReporting;
    }

    @Override
    public boolean getCapPredefinedPaymentLines() throws JposException {
        Log.info("getCapPredefinedPaymentLines:" + this.bCapPredefinedPaymentLines);
        return this.bCapPredefinedPaymentLines;
    }

    @Override
    public boolean getCapRecEmptySensor() throws JposException {
        Log.info("getCapRecEmptySensor:" + this.bCapRecEmptySensor);
        return this.bCapRecEmptySensor;
    }

    @Override
    public boolean getCapRecNearEndSensor() throws JposException {
        Log.info("getCapRecNearEndSensor:" + this.bCapRecNearEndSensor);
        return this.bCapRecNearEndSensor;
    }

    @Override
    public boolean getCapRecPresent() throws JposException {
        Log.info("getCapRecPresent:" + this.bCapRecPresent);
        return this.bCapRecPresent;
    }

    @Override
    public boolean getCapReceiptNotPaid() throws JposException {
        Log.info("getCapReceiptNotPaid:" + this.bCapReceiptNotPaid);
        return this.bCapReceiptNotPaid;
    }

    @Override
    public boolean getCapRemainingFiscalMemory() throws JposException {
        Log.info("getCapRemainingFiscalMemory:" + this.bCapRemainingFiscalMemory);
        return this.bCapRemainingFiscalMemory;
    }

    @Override
    public boolean getCapReservedWord() throws JposException {
        Log.info("getCapReservedWord:" + this.bCapReservedWord);
        return this.bCapReservedWord;
    }

    @Override
    public boolean getCapSetHeader() throws JposException {
        Log.info("getCapSetHeader:" + this.bCapSetHeader);
        return this.bCapSetHeader;
    }

    @Override
    public boolean getCapSetPOSID() throws JposException {
        Log.info("getCapSetPOSID:" + this.bCapSetPOSID);
        return this.bCapSetPOSID;
    }

    @Override
    public boolean getCapSetStoreFiscalID() throws JposException {
        Log.info("getCapSetStoreFiscalID:" + this.bCapSetStoreFiscalID);
        return this.bCapSetStoreFiscalID;
    }

    @Override
    public boolean getCapSetTrailer() throws JposException {
        Log.info("getCapSetTrailer:" + this.bCapSetTrailer);
        return this.bCapSetTrailer;
    }

    @Override
    public boolean getCapSetVatTable() throws JposException {
        Log.info("getCapSetVatTable:" + this.bCapSetVatTable);
        return this.bCapSetVatTable;
    }

    @Override
    public boolean getCapSlpEmptySensor() throws JposException {
        Log.info("getCapSlpEmptySensor:" + this.bCapSlpEmptySensor);
        return this.bCapSlpEmptySensor;
    }

    @Override
    public boolean getCapSlpFiscalDocument() throws JposException {
        Log.info("getCapSlpFiscalDocument:" + this.bCapSlpFiscalDocument);
        return this.bCapSlpFiscalDocument;
    }

    @Override
    public boolean getCapSlpFullSlip() throws JposException {
        Log.info("getCapSlpFullSlip:" + this.bCapSlpFullSlip);
        return this.bCapSlpFullSlip;
    }

    @Override
    public boolean getCapSlpNearEndSensor() throws JposException {
        Log.info("getCapSlpNearEndSensor:" + this.bCapSlpNearEndSensor);
        return this.bCapSlpNearEndSensor;
    }

    @Override
    public boolean getCapSlpPresent() throws JposException {
        Log.info("getCapSlpPresent:" + this.bCapSlpPresent);
        return this.bCapSlpPresent;
    }

    @Override
    public boolean getCapSlpValidation() throws JposException {
        Log.info("getCapSlpValidation:" + this.bCapSlpValidation);
        return this.bCapSlpValidation;
    }

    @Override
    public boolean getCapSubAmountAdjustment() throws JposException {
        Log.info("getCapSubAmountAdjustment:" + this.bCapSubAmountAdjustment);
        return this.bCapSubAmountAdjustment;
    }

    @Override
    public boolean getCapSubPercentAdjustment() throws JposException {
        Log.info("getCapSubPercentAdjustment:" + this.bCapSubPercentAdjustment);
        return this.bCapSubPercentAdjustment;
    }

    @Override
    public boolean getCapSubtotal() throws JposException {
        Log.info("getCapSubtotal:" + this.bCapSubtotal);
        return this.bCapSubtotal;
    }

    @Override
    public boolean getCapTrainingMode() throws JposException {
        Log.info("getCapTrainingMode:" + this.bCapTrainingMode);
        return this.bCapTrainingMode;
    }

    @Override
    public boolean getCapValidateJournal() throws JposException {
        Log.info("getCapValidateJournal:" + this.bCapValidateJournal);
        return this.bCapValidateJournal;
    }

    @Override
    public boolean getCapXReport() throws JposException {
        Log.info("getCapXReport:" + this.bCapXReport);
        return this.bCapXReport;
    }

    @Override
    public boolean getCheckTotal() throws JposException {
        Log.info("getCheckTotal:" + this.bCheckTotal);
        return this.bCheckTotal;
    }

    @Override
    public int getCountryCode() throws JposException {
        Log.info("getCountryCode:" + this.iCountryCode);
        return this.iCountryCode;
    }

    @Override
    public boolean getCoverOpen() throws JposException {
        Log.info("getCoverOpen:" + this.bCoverOpen);
        return this.bCoverOpen;
    }

    @Override
    public void getData(int dataItem, int[] optArgs, String[] data) throws JposException {
        try {
            long lRes;
            Log.debug("begin getData: dataItem=" + dataItem);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            switch (dataItem) {
            case FiscalPrinterConst.FPTR_GD_FIRMWARE:
                lRes = PiritService.getInstance().getFirmWare();
                data[0] = Long.toString(lRes);
                Log.info("getData(FPTR_GD_FIRMWARE): getFirmWare=" + optArgs[0]);

                break;
            case FiscalPrinterConst.FPTR_GD_PRINTER_ID:
                data[0] = PiritService.getInstance().getPrinterId();
                Log.info("getData(FPTR_GD_PRINTER_ID): getPrinterId=" + data[0]);

                break;
            case FiscalPrinterConst.FPTR_GD_CURRENT_TOTAL:
                lRes = PiritService.getInstance().getReceiptTotal();
                optArgs[0] = (int) lRes;
                data[0] = Long.toString(lRes);
                Log.info("getData(FPTR_GD_CURRENT_TOTAL): getCurrentReceiptTotal=" + optArgs[0]);

                break;
            case FiscalPrinterConst.FPTR_GD_DAILY_TOTAL:
                lRes = PiritService.getInstance().getDailyTotal(optArgs[0]);
                optArgs[0] = (int) lRes;
                data[0] = Long.toString(lRes);
                Log.info("getData(FPTR_GD_DAILY_TOTAL): getDailyTotal=" + optArgs[0]);

                break;
            case FiscalPrinterConst.FPTR_GD_MID_VOID:
                lRes = PiritService.getInstance().getCountVoidReceipts();
                optArgs[0] = (int) lRes;
                data[0] = Long.toString(lRes);
                Log.info("getData(FPTR_GD_MID_VOID): getCountVoidedReceipts=" + optArgs[0]);
                break;

            case FiscalPrinterConst.FPTR_GD_RECEIPT_NUMBER:
                lRes = PiritService.getInstance().getCountFiscalReceipts(optArgs[0]);
                optArgs[0] = (int) lRes;
                data[0] = Long.toString(lRes);
                Log.info("getData(FPTR_GD_RECEIPT_NUMBER): getCountFiscalReceipts=" + optArgs[0]);
                break;

            case FiscalPrinterConst.FPTR_GD_REFUND:
                lRes = PiritService.getInstance().getDailyRefundTotal();
                optArgs[0] = (int) lRes;
                data[0] = Long.toString(lRes);
                Log.info("getData(FPTR_GD_REFUND): getRefundTotal=" + optArgs[0]);
                break;

            case FiscalPrinterConst.FPTR_GD_FISCAL_DOC_VOID:
                lRes = PiritService.getInstance().getCountVoidReceipts();
                optArgs[0] = (int) lRes;
                data[0] = Long.toString(lRes);
                Log.info("getData(FPTR_GD_FISCAL_DOC_VOID): getCountVoidReceipts=" + optArgs[0]);
                break;

            case FiscalPrinterConst.FPTR_GD_FISCAL_REC:
                lRes = PiritService.getInstance().getCountFiscalSaleReceipts();
                optArgs[0] = (int) lRes;
                data[0] = Long.toString(lRes);
                Log.info("getData(FPTR_GD_FISCAL_REC): getCountFiscalSaleReceipts=" + optArgs[0]);
                break;

            case FiscalPrinterConst.FPTR_GD_Z_REPORT:
                lRes = PiritService.getInstance().getShiftNumber();
                optArgs[0] = (int) lRes;
                data[0] = Long.toString(lRes);
                Log.info("getData(FPTR_GD_Z_REPORT): getShiftNumber=" + optArgs[0]);
                break;

            case FiscalPrinterConst.FPTR_GD_FISCAL_DOC:
                lRes = PiritService.getInstance().getLastDocNumber();
                optArgs[0] = (int) lRes;
                data[0] = Long.toString(lRes);
                Log.info("getData(FPTR_GD_FISCAL_DOC): getLastDocNumber=" + optArgs[0]);
                break;

            case FiscalPrinterConst.FPTR_GD_DESCRIPTION_LENGTH:
                optArgs[0] = 44;
                data[0] = Long.toString(44L);
                Log.info("getData(FPTR_GD_DESCRIPTION_LENGTH): " + optArgs[0] + "(" + data[0] + ")");
                break;
            case FiscalPrinterConst.FPTR_GD_GRAND_TOTAL:
                lRes = PiritService.getInstance().getDailyTotal(0);
                switch (optArgs[0]) {
                case 0:
                    lRes = PiritService.getInstance().getGrandTotalSumms();
                    break;
                case 1:
                    lRes = PiritService.getInstance().getGrandTotalSumms();
                    break;
                default:
                    lRes = PiritService.getInstance().getGrandTotalSumms();
                    break;
                }
                optArgs[0] = (int) lRes;
                data[0] = Long.toString(lRes);
                Log.info("getData(FPTR_GD_GRAND_TOTAL): " + optArgs[0] + "(" + data[0] + ")");
                break;
            case FiscalPrinterConst.FPTR_GD_NOT_PAID:
            case FiscalPrinterConst.FPTR_GD_RESTART:
            case FiscalPrinterConst.FPTR_GD_REFUND_VOID:
            case FiscalPrinterConst.FPTR_GD_NUMB_CONFIG_BLOCK:
            case FiscalPrinterConst.FPTR_GD_NUMB_CURRENCY_BLOCK:
            case FiscalPrinterConst.FPTR_GD_NUMB_HDR_BLOCK:
            case FiscalPrinterConst.FPTR_GD_NUMB_RESET_BLOCK:
            case FiscalPrinterConst.FPTR_GD_NUMB_VAT_BLOCK:
                Log.info("getData(XXX): Not supported");

            }

        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end getData: dataItem=" + dataItem);
        } finally {
            Log.debug("end getData: dataItem=" + dataItem);
        }
    }

    @Override
    public void getDate(String[] arg0) throws JposException {
        try {
            Log.debug("begin getDate");
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");
            arg0[0] = PiritService.getInstance().getDate();
            Log.info("getDate:" + arg0[0]);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end getDate");
        } finally {
            Log.debug("end getDate");
        }
    }

    @Override
    public boolean getDayOpened() throws JposException {
        try {
            Log.debug("begin getDayOpened");
            this.bDayOpened = PiritService.getInstance().isShiftOpen();
            Log.info("getDayOpened:" + this.bDayOpened);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end getDayOpened");
        } finally {
            Log.debug("end getDayOpened");
        }
        return this.bDayOpened;
    }

    @Override
    public int getDescriptionLength() throws JposException {
        Log.info("getDescriptionLength:" + this.iDescriptionLength);
        return this.iDescriptionLength;
    }

    @Override
    public boolean getDuplicateReceipt() throws JposException {
        Log.info("getDuplicateReceipt:" + this.bDuplicateReceipt);
        return this.bDuplicateReceipt;
    }

    @Override
    public int getErrorLevel() throws JposException {
        Log.info("getErrorLevel:" + this.iErrorLevel);
        return this.iErrorLevel;
    }

    @Override
    public int getErrorOutID() throws JposException {
        Log.info("getErrorOutID:" + this.iErrorOutID);
        return this.iErrorOutID;
    }

    @Override
    public int getErrorState() throws JposException {
        Log.info("iErrorState:" + this.iErrorState);
        return this.iErrorState;
    }

    @Override
    public int getErrorStation() throws JposException {
        Log.info("getErrorStation:" + this.iErrorStation);
        return this.iErrorStation;
    }

    @Override
    public String getErrorString() throws JposException {
        Log.info("getErrorString:" + this.strErrorString);
        return this.strErrorString;
    }

    @Override
    public boolean getFlagWhenIdle() throws JposException {
        Log.info("getFlagWhenIdle:" + this.bFlagWhenIdle);
        return this.bFlagWhenIdle;
    }

    @Override
    public boolean getJrnEmpty() throws JposException {
        Log.debug("begin getJrnEmpty");
        boolean bJrnEmpty = false;
        try {
            if (this.bCapJrnEmptySensor)
                bJrnEmpty = PiritService.getInstance().isPaperOut();
            else
                throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");

            Log.info("getJrnEmpty:" + bJrnEmpty);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end getJrnEmpty");
        } finally {
            Log.debug("end getJrnEmpty");
        }
        return bJrnEmpty;
    }

    @Override
    public boolean getJrnNearEnd() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public int getMessageLength() throws JposException {
        Log.info("getMessageLength:" + this.iMessageLength);
        return this.iMessageLength;
    }

    @Override
    public int getNumHeaderLines() throws JposException {
        Log.info("getNumHeaderLines:" + this.iNumHeaderLines);
        return this.iNumHeaderLines;
    }

    @Override
    public int getNumTrailerLines() throws JposException {
        Log.info("getNumTrailerLines:" + this.iNumTrailerLines);
        return this.iNumTrailerLines;
    }

    @Override
    public int getNumVatRates() throws JposException {
        Log.info("getNumVatRates:" + this.iNumVatRates);
        return this.iNumVatRates;
    }

    @Override
    public int getOutputID() throws JposException {
        Log.info("getOutputID:" + this.iOutputID);
        return this.iOutputID;
    }

    @Override
    public int getPowerNotify() throws JposException {
        Log.info("getPowerNotify:" + this.iPowerNotify);
        return this.iPowerNotify;
    }

    @Override
    public int getPowerState() throws JposException {
        if (!(this.bOpened))
            throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
        if (!(this.bClaimed))
            throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
        if (!(this.bDeviceEnabled))
            throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

        if (PiritService.getInstance().isConnect())
            return 2001;

        return 2004;
    }

    @Override
    public String getPredefinedPaymentLines() throws JposException {
        if (this.bCapPredefinedPaymentLines) {
            Log.info("getPredefinedPaymentLines:" + this.strPredefinedPaymentLines);
            return this.strPredefinedPaymentLines;
        }
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public int getPrinterState() throws JposException {
        Log.info("getPrinterState:" + this.iPrinterState);
        return this.iPrinterState;
    }

    @Override
    public int getQuantityDecimalPlaces() throws JposException {
        Log.info("getQuantityDecimalPlaces:" + this.iQuantityDecimalPlaces);
        return this.iQuantityDecimalPlaces;
    }

    @Override
    public int getQuantityLength() throws JposException {
        Log.info("getQuantityLength:" + this.iQuantityLength);
        return this.iQuantityLength;
    }

    @Override
    public boolean getRecEmpty() throws JposException {
        Log.debug("begin getRecEmpty");
        boolean bRecEmpty = false;
        try {
            if (this.bCapRecEmptySensor)
                bRecEmpty = PiritService.getInstance().isPaperOut();
            else
                throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");

            Log.info("getJrnEmpty:" + bRecEmpty);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end getJrnEmpty");
        } finally {
            Log.debug("end getJrnEmpty");
        }
        return bRecEmpty;
    }

    @Override
    public boolean getRecNearEnd() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public int getRemainingFiscalMemory() throws JposException {
        if (this.bCapRemainingFiscalMemory) {
            Log.info("getRemainingFiscalMemory:" + this.iRemainingFiscalMemory);
            return this.iRemainingFiscalMemory;
        }
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public String getReservedWord() throws JposException {
        if (this.bCapReservedWord) {
            Log.info("getReservedWord:" + this.strReservedWord);
            return this.strReservedWord;
        }
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public int getSlipSelection() throws JposException {
        Log.info("getSlipSelection:" + this.iSlipSelection);
        return this.iSlipSelection;
    }

    @Override
    public boolean getSlpEmpty() throws JposException {
        Log.info("getSlpEmpty:" + this.bSlpEmpty);
        return this.bSlpEmpty;
    }

    @Override
    public boolean getSlpNearEnd() throws JposException {
        Log.info("getSlpNearEnd:" + this.bSlpNearEnd);
        return this.bSlpNearEnd;
    }

    // ERROR //
    @Override
    public void getTotalizer(int vatId, int optArg, String[] data) throws JposException {
        Log.info("getTotalizer vatId:" + vatId + "   optArg:" + optArg);
        // GetShiftRegister = 0x01
        // GetReceiptData = 0x03
        try {

            switch (optArg) {
            // FPTR_GT_NET Net totalizer specified by the TotalizerType and
            // ContractorId properties.
            case FiscalPrinterConst.FPTR_GT_NET:
                if (iTotalizerType == FiscalPrinterConst.FPTR_TT_DAY) {
                    data[0] = Long.toString(PiritService.getInstance().getDailyTotal(0));// SMFPTR_DAILY_TOTAL_ALL
                } else if (iTotalizerType == FiscalPrinterConst.FPTR_TT_RECEIPT) {
                    data[0] = Long.toString(PiritService.getInstance().getReceiptTotal());
                }
                break;

            // FPTR_GT_DISCOUNT Discount totalizer specified by the
            // TotalizerType and ContractorId properties.
            case FiscalPrinterConst.FPTR_GT_DISCOUNT:
                if (iTotalizerType == FiscalPrinterConst.FPTR_TT_DAY) {
                    // Вернуть суммы по скидкам/наценкам (скидок по продажам)
                    data[0] = Long.toString(PiritService.getInstance().getDiscountDailyTotal());
                } else if (iTotalizerType == FiscalPrinterConst.FPTR_TT_RECEIPT) {
                    // Вернуть счетчики текущего документа (сумма скидки по
                    // чеку)
                    data[0] = Long.toString(PiritService.getInstance().getDiscountReceiptTotal());
                }
                break;

            // FPTR_GT_SURCHARGE Surcharge totalizer specified by the
            // TotalizerType and ContractorId properties.
            case FiscalPrinterConst.FPTR_GT_SURCHARGE:
                if (iTotalizerType == FiscalPrinterConst.FPTR_TT_DAY) {
                    // Вернуть суммы по скидкам/наценкам (наценок по продажам)
                    data[0] = Long.toString(PiritService.getInstance().getSurchargeDailyTotal());
                } else if (iTotalizerType == FiscalPrinterConst.FPTR_TT_RECEIPT) {
                    // Вернуть счетчики текущего документа (сумма наценок по
                    // чеку)
                    data[0] = Long.toString(PiritService.getInstance().getSurchargeReceiptTotal());
                }
                break;

            // FPTR_GT_REFUND Refund totalizer specified by the TotalizerType
            // and ContractorId properties.
            case FiscalPrinterConst.FPTR_GT_REFUND:
                if (iTotalizerType == FiscalPrinterConst.FPTR_TT_DAY) {
                    // Вернуть суммы возвратов по типам платежа (за смену)
                    data[0] = Long.toString(PiritService.getInstance().getDailyRefundTotal());
                } else if (iTotalizerType == FiscalPrinterConst.FPTR_TT_RECEIPT) {
                    // Вернуть счетчики текущего документа (сумма чека + сумма
                    // скидки по чеку - сумма наценки по чеку)
                    data[0] = Long.toString(PiritService.getInstance().getReceiptTotal() + PiritService.getInstance().getDiscountReceiptTotal()
                            - PiritService.getInstance().getSurchargeReceiptTotal());
                }
                break;

            // FPTR_GT_ITEM Item totalizer specified by the TotalizerType and
            // ContractorId properties.
            case FiscalPrinterConst.FPTR_GT_ITEM:
                if (iTotalizerType == FiscalPrinterConst.FPTR_TT_DAY) {
                    // Вернуть суммы продаж по типам платежа (за смену)
                    data[0] = Long.toString(PiritService.getInstance().getDailyTotal(0));// SMFPTR_DAILY_TOTAL_ALL
                } else if (iTotalizerType == FiscalPrinterConst.FPTR_TT_RECEIPT) {
                    // Вернуть счетчики текущего документа (сумма чека + сумма
                    // скидки по чеку - сумма наценки по чеку)
                    data[0] = Long.toString(PiritService.getInstance().getReceiptTotal() + PiritService.getInstance().getDiscountReceiptTotal()
                            - PiritService.getInstance().getSurchargeReceiptTotal());
                }
                break;

            // FPTR_GT_GROSS Gross totalizer specified by the TotalizerType and
            // ContractorId properties.
            case FiscalPrinterConst.FPTR_GT_GROSS:
                if (iTotalizerType == FiscalPrinterConst.FPTR_TT_DAY) {
                    data[0] = Long.toString(PiritService.getInstance().getDailyTotal(0) - PiritService.getInstance().getDailyRefundTotal());// //SMFPTR_DAILY_TOTAL_ALL
                } else if (iTotalizerType == FiscalPrinterConst.FPTR_TT_RECEIPT) {
                    throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
                }
                break;

            // FPTR_GT_ITEM_VOID Voided item totalizer specified by the
            // TotalizerType and ContractorId properties.
            case FiscalPrinterConst.FPTR_GT_ITEM_VOID:
                data[0] = Long.toString(PiritService.getInstance().getCashAmount(0));
                break;

            default:
                throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
                // break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
        }

        // FPTR_GT_DISCOUNT_VOID Voided discount totalizer specified by the
        // TotalizerType and ContractorId properties.
        // FPTR_GT_ITEM Item totalizer specified by the TotalizerType and
        // ContractorId properties.
        // FPTR_GT_NOT_PAID Not paid totalizer specified by the TotalizerType
        // and ContractorId properties.
        // FPTR_GT_REFUND_VOID Voided refund totalizer specified by the
        // TotalizerType and ContractorId properties.
        // FPTR_GT_SUBTOTAL_DISCOUNT Subtotal discount totalizer specified by
        // the TotalizerType and ContractorId properties.
        // FPTR_GT_SUBTOTAL_DISCOUNT_VOID Voided discount totalizer specified by
        // the TotalizerType and ContractorId properties.
        // FPTR_GT_SUBTOTAL_SURCHARGES Subtotal surcharges totalizer specified
        // by the TotalizerType and ContractorId properties
        // FPTR_GT_SUBTOTAL_SURCHARGES_VOID Voided surcharges totalizer
        // specified by the TotalizerType and ContractorId properties.
        // FPTR_GT_SURCHARGE_VOID Voided surcharge totalizer specified by the
        // TotalizerType and ContractorId properties.
        // FPTR_GT_VATVAT totalizer specified by the TotalizerType and
        // ContractorId properties.
        // FPTR_GT_VAT_CATEGORYVAT totalizer per VAT category specified by the
        // TotalizerType and ContractorId properties associated to the given
        // vatID.

    }

    @Override
    public boolean getTrainingModeActive() throws JposException {
        Log.info("getTrainingModeActive:" + this.bTrainingModeActive);
        return this.bTrainingModeActive;
    }

    @Override
    public void getVatEntry(int vatId, int optArgs, int[] vatRate) throws JposException {
        try {
            Log.debug("begin getVatEntry: vatId=" + vatId);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            vatRate[0] = (int) PiritService.getInstance().getVatValue(vatId);
            Log.info("getVatEntry: vatId=" + vatId + " value=" + vatRate[0]);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end getVatEntry: vatId=" + vatId);
        } finally {
            Log.debug("end getVatEntry: vatId=" + vatId);
        }
    }

    @Override
    public void printDuplicateReceipt() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Error value of station");
    }

    @Override
    public void printFiscalDocumentLine(String arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Error value of station");
    }

    @Override
    public void printFixedOutput(int arg0, int arg1, String arg2) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Error value of station");
    }

    @Override
    public void printNormal(int station, String data) throws JposException {
        try {
            Log.debug("begin printNormal: station=" + station + " data=" + data);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");
            if (station != 2)
                throw new JposException(JposConst.JPOS_E_ILLEGAL, "Error value of station");

            if (this.bPrintBarCode) {
                PiritService.getInstance().putBarCode(this.iTextPosition, this.iWidthBarCode, this.iHeightBarCode, this.iTypeBarCode, data);
                Log.info("printNormal: putBarCode: iTypeBarCode=" + this.iTypeBarCode + " data=" + data);
            } else {
                PiritService.getInstance().putText(this.iTextAttribute, data);
                Log.info("printNormal: putBarCode: data=" + data);
            }

            // this.iPrinterState = 9;
            this.iState = 2;
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end printNormal: station=" + station + " data=" + data);
        } finally {
            Log.debug("end printNormal: station=" + station + " data=" + data);
        }
    }

    @Override
    public void printPeriodicTotalsReport(String date1, String date2) throws JposException {
        try {
            Log.debug("begin printPeriodicTotalsReport: date1=" + date1 + " date2=" + date2);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            PiritService.getInstance().printFiscalReportByDate(date1, date2);
            Log.info("printPeriodicTotalsReport: date1=" + date1 + " date2=" + date2);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end printPeriodicTotalsReport: date1=" + date1 + " date2=" + date2);
        } finally {
            Log.debug("end printPeriodicTotalsReport: date1=" + date1 + " date2=" + date2);
        }
    }

    @Override
    public void printPowerLossReport() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Error value of station");
    }

    @Override
    public void printRecItem(String description, long price, int quantity, int vatInfo, long unitPrice, String unitName) throws JposException {
        try {
            if(bEnableGoodsCounter) {
                this.goodsCounter++;
                description = this.goodsCounter+" "+description;
            }

            Log.debug("begin printRecItem: description="+ description +" unitName=" + unitName + " quantity=" + quantity + " unitPrice=" + unitPrice);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            if ((this.iFiscalReceiptType != FiscalPrinterConst.FPTR_RT_SALES) && (this.iFiscalReceiptType != FiscalPrinterConst.FPTR_RT_REFUND)) {
                throw new JposException(JposConst.JPOS_E_ILLEGAL, "Error value FiscalReceiptType");
            }

            PiritService.getInstance().addGoods(description, price, quantity, vatInfo, unitPrice, unitName);
            this.iState = 2;
            Log.info("printRecItem: unitName=" + unitName + " quantity=" + quantity + " unitPrice=" + unitPrice);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end printRecItem: unitName=" + unitName + " quantity=" + quantity + " unitPrice=" + unitPrice);
        } finally {
            Log.debug("end printRecItem: unitName=" + unitName + " quantity=" + quantity + " unitPrice=" + unitPrice);
        }
    }

    @Override
    public void printRecItemAdjustment(int adjustmentType, String description, long amount, int vatInfo) throws JposException {
        try {
            Log.debug("begin printRecItemAdjustment: adjustmentType=" + adjustmentType + " description=" + description + " amount=" + amount);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");
            if(adjustmentType == 1){
                PiritService.getInstance().addRequesit(1, description);
            } else {
                String st = "Скидка " + description + "=" + Long.toString(amount / 100) + "." + String.format("%02d", amount % 100);
                PiritService.getInstance().addRequesit(1, st);
            }
            // rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr

            /*
             * switch (adjustmentType) { case 1:
             * PiritService.getInstance().addDiscount(adjustmentType,
             * description, amount);
             *
             * break; case 3:
             * PiritService.getInstance().addDiscount(adjustmentType,
             * description, amount);
             *
             * break; case 2:
             * PiritService.getInstance().addMargin(adjustmentType, description,
             * amount);
             *
             * break; case 4:
             * PiritService.getInstance().addMargin(adjustmentType, description,
             * amount);
             *
             * break; default: throw new JposException(JposConst.JPOS_E_ILLEGAL,
             * "Error value adjustmentType"); }
             */

            this.iState = 2;
            Log.info("printRecItemAdjustment: adjustmentType=" + adjustmentType + " description=" + description + " amount=" + amount);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end printRecItemAdjustment: adjustmentType=" + adjustmentType + " description=" + description + " amount=" + amount);
        } finally {
            Log.debug("end printRecItemAdjustment: adjustmentType=" + adjustmentType + " description=" + description + " amount=" + amount);
        }
    }

    @Override
    public void printRecMessage(String arg0) throws JposException {
        Log.debug("begin printRecMessage: message=" + arg0);
        try {
            long docSt = PiritService.getInstance().getDocStatus();
            if ((docSt & 15) > 0) {
                if ((docSt & 15) == 1) {
                    PiritService.getInstance().putText(0, arg0);
                } else {
                    PiritService.getInstance().addRequesit(1, arg0);
                }
            }
        } catch (Exception e) {
            // TODO: Не забыть прокинуть Exception выше
            e.printStackTrace();
        }
        // throw new JposException(JposConst.JPOS_E_ILLEGAL,
        // "Error value of station");
        Log.debug("end printRecMessage");
    }

    @Override
    public void printRecNotPaid(String arg0, long arg1) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Error value of station");
    }

    @Override
    public void printRecRefund(String arg0, long arg1, int arg2) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Error value of station");
    }

    @Override
    public void printRecSubtotal(long amount) throws JposException {
        try {
            Log.debug("begin printRecSubtotal: amount=" + amount);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            PiritService.getInstance().subtotal();
            this.iState = 2;
            Log.info("printRecSubtotal: amount=" + amount);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end printRecSubtotal: amount=" + amount);
        } finally {
            Log.debug("end printRecSubtotal: amount=" + amount);
        }
    }

    @Override
    public void printRecSubtotalAdjustment(int adjustmentType, String description, long amount) throws JposException {
        // printRecItemAdjustment(adjustmentType, description, amount, 0);

        try {
            Log.debug("begin printRecSubtotalAdjustment: adjustmentType=" + adjustmentType + " description=" + description + " amount=" + amount);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            switch (adjustmentType) {
            case 1:
                PiritService.getInstance().addDiscount(adjustmentType, description, amount);

                break;
            case 3:
                PiritService.getInstance().addDiscount(adjustmentType, description, amount);

                break;
            case 2:
                PiritService.getInstance().addMargin(adjustmentType, description, amount);

                break;
            case 4:
                PiritService.getInstance().addMargin(adjustmentType, description, amount);

                break;
            default:
                throw new JposException(JposConst.JPOS_E_ILLEGAL, "Error value adjustmentType");
            }

            this.iState = 2;
            Log.info("printRecSubtotalAdjustment: adjustmentType=" + adjustmentType + " description=" + description + " amount=" + amount);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end printRecSubtotalAdjustment: adjustmentType=" + adjustmentType + " description=" + description + " amount=" + amount);
        } finally {
            Log.debug("end printRecSubtotalAdjustment: adjustmentType=" + adjustmentType + " description=" + description + " amount=" + amount);
        }
    }

    @Override
    public void printRecTotal(long total, long payment, String description) throws JposException {
        try {
            Log.debug("begin printRecTotal: payment=" + payment + " description=" + description);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            if ((iFiscalReceiptType != FiscalPrinterConst.FPTR_RT_CASH_IN) && (iFiscalReceiptType != FiscalPrinterConst.FPTR_RT_CASH_OUT)) {
                PiritService.getInstance().putPayment(payment, Long.parseLong(description));
                this.iState = 2;// Для X5
            } else
                this.iState = 2;// Для X5
            Log.info("printRecTotal: payment=" + payment + " description=" + description);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end printRecTotal: payment=" + payment + " description=" + description);
        } finally {
            Log.debug("end printRecTotal: payment=" + payment + " description=" + description);
        }
    }

    @Override
    public void printRecVoid(String description) throws JposException {
        try {
            Log.debug("begin printRecVoid");
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");
            this.bEnablePacketPrint = false;
            PiritService.getInstance().setbEnablePacketPrint(false);
            PiritService.getInstance().resetDoc();
            this.goodsCounter = 0;
            this.iPrinterState = 1;
            Log.info("printRecVoid");
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end printRecVoid");
        } finally {
            Log.debug("end printRecVoid");
        }
    }

    @Override
    public void printRecVoidItem(String arg0, long arg1, int arg2, int arg3, long arg4, int arg5) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Error value of station");
    }

    @Override
    public void printReport(int reportType, String startNum, String endNum) throws JposException {
        try {
            Log.debug("begin printReport: startNum=" + startNum + " endNum=" + endNum);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            if ((startNum.equals("0")) && (endNum.equals("0")))
                PiritService.getInstance().printCopyZReport();
            else
                PiritService.getInstance().printFiscalReportByShiftId(Long.parseLong(startNum), Long.parseLong(endNum));

            Log.info("printReport: startNum=" + startNum + " endNum=" + endNum);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end printReport: startNum=" + startNum + " endNum=" + endNum);
        } finally {
            Log.debug("end printReport: startNum=" + startNum + " endNum=" + endNum);
        }
    }

    @Override
    public void printXReport() throws JposException {
        try {
            Log.debug("begin printXReport");
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            PiritService.getInstance().printXReport(this.strCashierId);
            Log.info("printXReport");
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end printXReport");
        } finally {
            Log.debug("end printXReport");
        }
    }

    @Override
    public void printZReport() throws JposException {
        try {
            Log.debug("begin printZReport");
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            if(PiritService.getInstance().isShiftOpen() == false)
               PiritService.getInstance().openShift(this.strCashierId);
            PiritService.getInstance().printZReport(this.strCashierId);
            Log.info("printZReport");
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end printZReport");
        } finally {
            Log.debug("end printZReport");
        }
    }

    @Override
    public void resetPrinter() throws JposException {
        try {
            Log.debug("begin resetPrinter");
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");
            this.bEnablePacketPrint = false;
            PiritService.getInstance().setbEnablePacketPrint(false);
            PiritService.getInstance().resetDoc();
            this.goodsCounter = 0;
            this.iPrinterState = 1;
            Log.info("resetPrinter");
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end resetPrinter");
        } finally {
            Log.debug("end resetPrinter");
        }
    }

    @Override
    public void setAsyncMode(boolean arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void setCheckTotal(boolean arg0) throws JposException {
        if (this.bCapCheckTotal) {
            this.bCheckTotal = arg0;
            Log.info("setCheckTotal:" + this.bCheckTotal);
        } else {
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
        }
    }

    @Override
    public void setDate(String date) throws JposException {
        try {
            Log.debug("begin setDate");
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            PiritService.getInstance().setDate(date);
            Log.info("setDate:" + date);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end setDate");
        } finally {
            Log.debug("end setDate");
        }
    }

    @Override
    public void setDuplicateReceipt(boolean arg0) throws JposException {
        if (this.bCapDuplicateReceipt) {
            this.bDuplicateReceipt = arg0;
            Log.info("setDuplicateReceipt:" + this.bDuplicateReceipt);
        } else {
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
        }
    }

    @Override
    public void setFlagWhenIdle(boolean arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void setHeaderLine(int lineNumber, String text, boolean doubleWidth) throws JposException {
        try {
            Log.debug("begin setHeaderLine: lineNumber=" + lineNumber + " text=" + text);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            PiritService.getInstance().setHeaderLine(lineNumber - 1, text);
            Log.info("setHeaderLine: lineNumber=" + lineNumber + " text=" + text);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("begin setHeaderLine: lineNumber=" + lineNumber + " text=" + text);
        } finally {
            Log.debug("begin setHeaderLine: lineNumber=" + lineNumber + " text=" + text);
        }
    }

    @Override
    public void setPOSID(String posId, String cashierId) throws JposException {
        try {
            Log.debug("begin setPOSID: posId=" + posId + " cashierId=" + cashierId);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");
            if (!(this.bCapSetPOSID))
                throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");

            this.strCashierId = cashierId;
            Log.info("setPOSID: posId=" + posId + " cashierId=" + cashierId);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("begin setPOSID: posId=" + posId + " cashierId=" + cashierId);
        } finally {
            Log.debug("begin setPOSID: posId=" + posId + " cashierId=" + cashierId);
        }
    }

    @Override
    public void setPowerNotify(int arg0) throws JposException {
        // throw new JposException(JposConst.JPOS_E_ILLEGAL,
        // "Method is not supported by this service");
    }

    @Override
    public void setSlipSelection(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void setStoreFiscalID(String arg0) throws JposException {
        this.iCheckNumber=Integer.valueOf(arg0);
     //   throw new JposException(JposConst.JPOS_E_ILLEGAL, "Error value of station");
    }

    @Override
    public void setTrailerLine(int lineNumber, String text, boolean doubleWidth) throws JposException {
        try {
            Log.debug("begin setHeaderLine: lineNumber=" + lineNumber + " text=" + text);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            Log.info("setTrailerLine: lineNumber=" + lineNumber + " text=" + text);
            PiritService.getInstance().setTrailerLine(lineNumber - 1, text);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("begin setTrailerLine: lineNumber=" + lineNumber + " text=" + text);
        } finally {
            Log.debug("begin setTrailerLine: lineNumber=" + lineNumber + " text=" + text);
        }
    }

    @Override
    public void setVatTable() throws JposException {
        try {
            Log.debug("begin setVatTable");
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            Iterator it = this.vatTable.keySet().iterator();
            while (it.hasNext()) {
                Integer vatId = (Integer) it.next();
                String vatValue = this.vatTable.get(vatId);
                PiritService.getInstance().setVatValue(vatId.intValue(), vatValue);
                Log.info("setVatTable: vatId=" + vatId + " vatValue=" + vatValue);
            }
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end setVatTable");
        } finally {
            Log.debug("end setVatTable");
        }
    }

    @Override
    public void setVatValue(int vatId, String vatValue) throws JposException {
        try {
            Log.debug("begin setVatValue: vatId=" + vatId + " vatValue=" + vatValue);
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");

            this.vatTable.put(Integer.valueOf(vatId), vatValue);
            Log.info("setVatValue: vatId=" + vatId + " vatValue=" + vatValue);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end setVatValue: vatId=" + vatId + " vatValue=" + vatValue);
        } finally {
            Log.debug("end setVatValue: vatId=" + vatId + " vatValue=" + vatValue);
        }
    }

    @Override
    public void verifyItem(String arg0, int arg1) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Error value of station");
    }

    @Override
    public void checkHealth(int arg0) throws JposException {
        try {
            Log.debug("begin checkHealth");
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");
            if (!(this.bDeviceEnabled))
                throw new JposException(JposConst.JPOS_E_DISABLED, "Device is not enabled");
            if (arg0 != 1)
                throw new JposException(JposConst.JPOS_E_ILLEGAL, "Device only supports internal health checks");

            this.strHealthText = PiritService.getInstance().getPrinterState();
            Log.info("checkHealth:" + this.strHealthText);
        } catch (Exception e) {
            throwJposException(e);

            Log.debug("end checkHealth");
        } finally {
            Log.debug("end checkHealth");
        }
    }

    @Override
    public void claim(int timeOut) throws JposException {
        try {
            Log.debug("begin claim");
            if (timeOut < -1)
                throw new JposException(JposConst.JPOS_E_ILLEGAL, "Invalid timeout value");
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (this.bClaimed)
                throw new JposException(102, "Service is already claimed");
            PiritService.getInstance().setReadTimeOut(timeOut);
            PiritService.getInstance().setExeptinListener(exeptionListener);
            PiritService.getInstance().start();
            this.bDayOpened = PiritService.getInstance().isShiftOpen();
            this.bClaimed = true;
            Log.info("claim");
        } catch (Exception e) {
            throwJposException(e);
            Log.debug("end claim");
        } finally {
            Log.debug("end claim");
        }
    }

    @Override
    public void close() throws JposException {
        try {
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            setEventCallbacks(null);
            setStopEvents(true);
            stopPoll();
            PiritService.getInstance().stop();
            setPowerState(JPOS_SUE_POWER_OFFLINE);
            this.bOpened = false;
            this.iState = 1;
            Log.info("close");
        } catch (Exception e) {
            throwJposException(e);
        }
    }

    @Override
    public void directIO(int arg0, int[] arg1, Object arg2) throws JposException {
        try {
            switch (arg0) {
            case 1:
                this.iCheckNumber = arg1[0];
                Log.info("directIO_1.0 : SET_CHECK_NUMBER:" + this.iCheckNumber);
                break;

            case 2:
                if (arg1[0] != 0)
                    this.bPrintBarCode = true;
                else
                    this.bPrintBarCode = false;

                Log.info("directIO_1.0 : SET_PRINT_BAR_CODE:" + this.bPrintBarCode);
                break;

            case 3:
                this.iTextAttribute = arg1[0];
                Log.info("directIO_1.0 : SET_TEXT_ATTRIBUTE:" + this.iTextAttribute);
                break;

            case 4:
                this.iTextPosition = arg1[0];
                Log.info("directIO_1.0 : SET_TEXT_POSITION:" + this.iTextPosition);
                break;

            case 5:
                this.iWidthBarCode = arg1[0];
                Log.info("directIO_1.0 : SET_WIDTH_BAR_CODE:" + this.iWidthBarCode);
                break;

            case 6:
                this.iHeightBarCode = arg1[0];
                Log.info("directIO_1.0 : SET_HEIGHT_BAR_CODE:" + this.iHeightBarCode);
                break;

            case 7:
                this.iTypeBarCode = arg1[0];
                Log.info("directIO_1.0 : SET_TYPE_BAR_CODE:" + this.iTypeBarCode);
                break;

            case 8:
                PiritService.getInstance().openMoneyDrawer();
                Log.info("directIO_1.0 : OPEN_CASH_DRAWER");
                break;

            case 9:
                arg2 = PiritService.getInstance().isMoneyDrawerOpen();
                Log.info("directIO_1.0 : OPEN_CASH_DRAWER:" + (arg2));
                break;

            case 10:
                PiritService.getInstance().setVatName(arg1[0], (String) arg2);
                Log.info("directIO_1.0 : SET_VAT_NAME: vatId= " + arg1[0] + " vatName= " + ((String) arg2));
                break;

            case 11:
                arg2 = PiritService.getInstance().getVatName(arg1[0]);
                Log.info("directIO_1.0 : GET_VAT_NAME: vatId= " + arg1[0] + " vatName= " + ((String) arg2));
                break;
            case 12:
                if (arg1[0] != 0)
                  this.bEnableGoodsCounter = true;
                else
                  this.bEnableGoodsCounter = false;
                  Log.info("directIO_1.0 : SET_ENABLE_GOODS_COUNTER:" + this.bEnableGoodsCounter);
                break;
           case 13:
                if (arg1[0] != 0) {
                    this.bEnablePacketPrint = true;
                    PiritService.getInstance().setbEnablePacketPrint(true);
                }
                else {
                    this.bEnablePacketPrint = false;
                    PiritService.getInstance().setbEnablePacketPrint(false);
                }
                  Log.info("directIO_1.0 : SET_ENABLE_PACKET_PRINT:" + this.bEnablePacketPrint);
                break;
            case 52:
                if( arg1[0]==1117)
                {
                String mamereq = "Адрес отправителя: ";
                PiritService.getInstance().addReqForOFD((long) arg1[0], 0L, mamereq, (String) arg2);
                Log.info("directIO_1.0 : ReqForOFD: reqNum= " + arg1[0] + " reqData= " + ((String) arg2));
                break;
                }
                if( arg1[0]==1008)
                {
                String mamereq = "Адрес покупателя: ";
                PiritService.getInstance().addReqForOFD((long) arg1[0], 0L, mamereq, (String) arg2);
                Log.info("directIO_1.0 : ReqForOFD: reqNum= " + arg1[0] + " reqData= " + ((String) arg2));
                break;
                }
                else{
                PiritService.getInstance().addReqForOFD((long) arg1[0], 0L, (String) null, (String) arg2);
                Log.info("directIO_1.0 : ReqForOFD: reqNum= " + arg1[0] + " reqData= " + ((String) arg2));
                }
                break;
            case 53:
                int receiptNumber = arg1[0];
                String[] result_reg = (String[]) arg2;
                long test = Long.valueOf(receiptNumber);
                result_reg[0] = PiritService.getInstance().getFiscalOfdReceiptFromRepo(test).toString();
                Log.info("directIO_1.0 : GET_FISCAL_DOC_FROM_FR_REPO: " + " reqData= " +result_reg[0]);
                break;
            case 54:
                int sum = arg1[0];
                PiritService.getInstance().setTotalReceipt(sum);
                System.err.println("directIO_1.0 : SET_TOTAL_RECEPIE: " +sum/100.0D);
                Log.info("directIO_1.0 : SET_TOTAL_RECEPIE: " +sum/100.0D);
                break;

             case 26: // READ OFD DOC FROM MEMORY:
                Object[] items = (Object[]) arg2;
                Vector<PiritFNTicket> resultVector1 = PiritService.getInstance().getSignedFiscalDocsFromMemory(arg1);
                items[0] = resultVector1;
                for(PiritFNTicket s : resultVector1) {
                     Log.info("directIO_1.0 : READ_OFD_DOC_FROM_MEMORY: Data = " + s.getDocNumber());
                }
                break;
            case 27:// FPTR_DIO_READ_CASH_REG:
                String[] lines = (String[]) arg2;
                lines[0] = Long.toString(PiritService.getInstance().getCashAmount(arg1[0]));
                Log.info("directIO_1.0 : FPTR_DIO_READ_CASH_REG: Sum= " + lines[0] + " for param " + arg1[0]);
                break;
            case 28:// Read date end time PRINTER
                String[] EndDateTimeResouarcePrinter = (String[]) arg2;
                EndDateTimeResouarcePrinter[0] = PiritService.getInstance().getEndDateTimeResouarcePrinter().toString();
                Log.info("directIO_1.0 : GET_END_TIME_RESOURCE_PRINTER: " + " reqData= " + EndDateTimeResouarcePrinter[0]);
                break;
            case 29:// Read registration fiscal number
                String[] RegFiscalNumber = (String[]) arg2;
                RegFiscalNumber[0] = PiritService.getInstance().getRegFiscalNumber();
                Log.info("directIO_1.0 : GET_REGISTRATION_FISCAL_NUMBER: " + " reqData= " + RegFiscalNumber[0]);
                break;
            case 30:// Read date and time of registration fiscal number
                String[] DateTimeRegFiscalNumber = (String[]) arg2;
                DateTimeRegFiscalNumber[0] = PiritService.getInstance().getDateTimeRegFiscalNumber().toString();
                Log.info("directIO_1.0 : GET_DATE_AND_TIME_REGISTRATION_FISCAL_NUMBER: " + " reqData= " +DateTimeRegFiscalNumber[0]);
                break;
            case 31:// Read amount of unsended doc.
                String[] OFDStatusAmountUnsendedDocs = (String[]) arg2;
                OFDStatusAmountUnsendedDocs[0] = Long.toString(PiritService.getInstance().getOFDStatusAmountUnsendedDocs());
                Log.info("directIO_1.0 : GET_DATE_AND_TIME_OF_UNSENDED_DOC: " + " reqData= " + OFDStatusAmountUnsendedDocs[0]);
                break;
            case 32:// Read date and time of first unsended doc.
                String[] OFDStatusDateFirstUnsendedDocs = (String[]) arg2;
                OFDStatusDateFirstUnsendedDocs[0] = PiritService.getInstance().getOFDStatusDateFirstUnsendedDocs().toString();
                Log.info("directIO_1.0 : GET_AMOUNT_OF_UNSENDED_DOC: " + " reqData= " + OFDStatusDateFirstUnsendedDocs[0]);
                break;
            case 33:
                String bayerAddress = (String) arg2;
                PiritService.getInstance().setBayerAddress(bayerAddress);
                Log.info("directIO_1.0 : SET_BAYER_ADDRESS: " + bayerAddress);
                break;
            case 34:
                String[] piritStatus = (String[]) arg2;
                long status = PiritService.getInstance().getPrinterStatus();
                piritStatus[0] = String.valueOf(PiritService.getInstance().getBit(status, arg1[0]));
                Log.info("directIO_1.0 : PRINTER_STATUS: " +piritStatus[0]+ " for param " + arg1[0]);
                break;
            case 35:
                String[] regNumber = (String[]) arg2;
                regNumber[0] = PiritService.getInstance().getRegNumberFR();
                Log.info("directIO_1.0 : PRINTER_REG_NUMBER: " +regNumber[0]);
                break;
            case 36:
                String[] lastNumberFD = (String[]) arg2;
                lastNumberFD[0] = String.valueOf(PiritService.getInstance().getLastFiscalNumberDoc());
                Log.info("directIO_1.0 : NUMBER_OF_LAST_FISCAL_DOC: " +lastNumberFD[0]);
                break;
            case 37:
                String[] dateLastFD = (String[]) arg2;
                long numberLastFD = PiritService.getInstance().getLastFiscalNumberDoc();
                int [] numbers = new int[1];
                numbers[0] = (int)numberLastFD;
                Vector<PiritFNTicket> resultVector2 = PiritService.getInstance().getFiscalDocsFromMemory(numbers);
                dateLastFD[0] = resultVector2.get(0).getDateOfDoc();
                Log.info("directIO_1.0 : DATE_OF_LAST_FD: " +dateLastFD[0]);
                break;
            case 38:
                Object[] result = (Object[]) arg2;
                Vector<PiritFNTicket> resultVector3 = PiritService.getInstance().getRangeFiscalDocsFromMemory(arg1);
                result[0] = resultVector3;
                for(PiritFNTicket s : resultVector3) {
                    Log.info("directIO_1.0 : READ_RANGE_OFD_DOC_FROM_MEMORY: Data = " + s.getDocNumber());
                }
                break;
            case 39:
                String[] shiftNumber = (String[]) arg2;
                shiftNumber[0] = String.valueOf(PiritService.getInstance().getNumberOfCurretnShift());
                Log.info("directIO_1.0 : NUMBER_OF_CURRENT_SHIFT: " +shiftNumber[0]);
                break;
            case 40:
                arg1[0] = (int)PiritService.getInstance().getAmountReceiptsOfCurretnShift();
                Log.info("directIO_1.0 : AMOUNT_RECEIPTS_OF_CURRENT_SHIFT: " +arg1[0]);
                break;
            case 41:
                arg1[0] = (int)PiritService.getInstance().getNumberDocOfLastReregistration();
                Log.info("directIO_1.0 : DOC_NUMBER_OF_LAST_REREGISTRATION: " +arg1[0]);
                break;
            case 42:
                Object[] result1 = (Object[]) arg2;
                Vector<PiritFNTicket> resultVector4 = PiritService.getInstance().getSortCountFiscalDocsFromMemory(arg1);
                result1[0] = resultVector4;
                for(PiritFNTicket s : resultVector4) {
                    Log.info("directIO_1.0 : READ_RANGE_OFD_DOC_FROM_MEMORY: Data = " + s.getDocNumber() + " "+s.getDateOfDoc());
                }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throwJposException(e);
        }
    }

    @Override
    public String getCheckHealthText() throws JposException {
        Log.info("getCheckHealthText:" + this.strHealthText);
        return this.strHealthText;
    }

    @Override
    public boolean getClaimed() throws JposException {
        Log.info("getClaimed:" + this.bClaimed);
        return this.bClaimed;
    }

    @Override
    public boolean getDeviceEnabled() throws JposException {
        if (!(this.bOpened))
            throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
        if (!(this.bClaimed))
            throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");

        Log.info("getDeviceEnabled:" + this.bDeviceEnabled);
        return this.bDeviceEnabled;
    }

    @Override
    public String getDeviceServiceDescription() throws JposException {
        if (!(this.bOpened))
            throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");

        Log.info("getDeviceServiceDescription:" + PiritService.getInstance().getServiceDescription());
        return PiritService.getInstance().getServiceDescription();
    }

    @Override
    public int getDeviceServiceVersion() throws JposException {
        if (!(this.bOpened))
            throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");

        Log.info("getDeviceServiceVersion:" + iServiceVersion);
        return iServiceVersion;
    }

    @Override
    public boolean getFreezeEvents() throws JposException {
        checkOpened();
        return freezeEvents;
    }

    @Override
    public String getPhysicalDeviceDescription() throws JposException {
        if (!(this.bOpened))
            throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");

        Log.info("getPhysicalDeviceDescription:" + PiritService.getInstance().getPhysicalDescription());
        return PiritService.getInstance().getPhysicalDescription();
    }

    @Override
    public String getPhysicalDeviceName() throws JposException {
        if (!(this.bOpened))
            throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");

        Log.info("getPhysicalDeviceName:" + PiritService.getInstance().getPhysicalName());
        return PiritService.getInstance().getPhysicalName();
    }

    @Override
    public int getState() throws JposException {
        return this.iState;
    }

    @Override
    public void open(String logicalName, EventCallbacks arg1) throws JposException {
        try {
            if (this.bOpened) {
                throw new JposException(JposConst.JPOS_E_ILLEGAL, "Service is already open");
            }

            JposEntry jposEntry = JposServiceLoader.getManager().getEntryRegistry().getJposEntry(logicalName);
            if (jposEntry == null) {
                throw new JposException(JposConst.JPOS_E_NOEXIST, "Logical device could not be found");
            }
            setEventCallbacks(arg1);
            PiritService.getInstance().readConfig(jposEntry);
            this.bOpened = true;
            this.iState = 2;
//            startPoll();
//            setStopEvents(false);
            Log.info("open: logicalName=" + logicalName);
        } catch (Exception e) {
            throwJposException(e);
        }
    }

    @Override
    public void release() throws JposException {
        Log.info("release");
        this.bClaimed = false;
    }

    @Override
    public void setDeviceEnabled(boolean flag) throws JposException {
        try {
            if (!(this.bOpened))
                throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open");
            if (!(this.bClaimed))
                throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed");

            Log.info("setDeviceEnabled:" + flag);
            this.bDeviceEnabled = flag;
        } catch (Exception e) {
            throwJposException(e);
        }
    }

    @Override
    public void setFreezeEvents(boolean freezeEvents) throws JposException {
        checkOpened();
        if (freezeEvents != getFreezeEvents()) {
            this.freezeEvents = freezeEvents;
            if (freezeEvents) {
                try {
                    stopEventThread();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    startEventThread();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void deleteInstance() throws JposException {
    }

    public boolean getCapServiceAllowManagement() throws JposException {
        Log.info("getCapServiceAllowManagement:" + this.bCapServiceAllowManagement);
        return this.bCapServiceAllowManagement;
    }

    private void getExtendedErrorStatus() throws Exception {
        System.err.println(PiritService.getInstance().getAddErrorInfo());
    }

    private void clearResultCode() {
        this.iResultCode = 0;

        this.strErrorString = "No errors";
    }

    private synchronized void throwJposException(Exception e) throws JposException {
        Log.error("", e);
        if (e instanceof JposException) {
            if(((JposException) e).getErrorCode()==8){
                System.err.println("propertyChange");
                try {
                    updateStatus();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                return;
            }
            JposException jpe = (JposException) e;
            this.iResultCode = jpe.getErrorCode();

            this.strErrorString = jpe.getMessage();
            throw jpe;
        }
        this.iResultCode = 111;

        this.strErrorString = "Unknown error";
        throw new JposException(this.iResultCode, this.strErrorString, e);
    }



   /*******************EventCallbacks************************/

   //--------------------------------------------------------------------------
   // Event Listener Methods
   //--------------------------------------------------------------------------

   public void addDirectIOListener(DirectIOListener l)
   {
       synchronized(directIOListeners)
       {
           directIOListeners.addElement(l);
       }
   }

    public void removeDirectIOListener(DirectIOListener l)
    {
        synchronized(directIOListeners)
        {
            directIOListeners.removeElement(l);
        }
    }

    public void addErrorListener(ErrorListener l)
    {
        synchronized(errorListeners)
        {
            errorListeners.addElement(l);
        }
    }

    public void removeErrorListener(ErrorListener l)
    {
        synchronized(errorListeners)
        {
            errorListeners.removeElement(l);
        }
    }

    public void addOutputCompleteListener(OutputCompleteListener l)
    {
        synchronized(outputCompleteListeners)
        {
            outputCompleteListeners.addElement(l);
        }
    }

    public void removeOutputCompleteListener(OutputCompleteListener l)
    {
        synchronized(outputCompleteListeners)
        {
            outputCompleteListeners.removeElement(l);
        }
    }

    public void addStatusUpdateListener(StatusUpdateListener l)
    {
        synchronized(statusUpdateListeners)
        {
            statusUpdateListeners.addElement(l);
        }
    }

    public void removeStatusUpdateListener(StatusUpdateListener l)
    {
        synchronized(statusUpdateListeners)
        {
            statusUpdateListeners.removeElement(l);
        }
    }

    //--------------------------------------------------------------------------
    // Event Methods & Classes
    //--------------------------------------------------------------------------

    class EventTarget implements Runnable {

        private final PiritSvc112 fiscalPrinter;

        public EventTarget(PiritSvc112 fiscalPrinter) {
            this.fiscalPrinter = fiscalPrinter;
        }

        public void run() {
            fiscalPrinter.eventProc();
        }
    }

    class DeviceTarget implements Runnable {

        private final PiritSvc112 fiscalPrinter;

        public DeviceTarget(PiritSvc112 fiscalPrinter) {
            this.fiscalPrinter = fiscalPrinter;
        }

        public void run() {
            fiscalPrinter.deviceProc();
        }
    }

    private void eventProc() {
        try {
            while (eventThreadEnabled) {
                synchronized (events) {
                    while (!events.isEmpty()) {
                        ((Runnable) events.remove(0)).run();
                    }
                    events.wait();
                }
            }
        } catch (InterruptedException e) {
            Log.error("InterruptedException", e);
            Thread.currentThread().interrupt();
        }
    }

    private void deviceProc() {
        Log.debug("deviceProc started");
        try {
            while (deviceThreadEnabled) {
                checkDeviceStatus();
                Thread.sleep(300);
                updateStatus();
            }
        } catch (InterruptedException e) {
            Log.error("InterruptedException", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.debug("deviceProc stoped");
    }

    private void startPoll() throws Exception {
        deviceThreadEnabled = true;
        deviceThread = new Thread(new DeviceTarget(this));
        deviceThread.start();
    }

    private void stopPoll() throws Exception {
        if (deviceThreadEnabled) {
            deviceThreadEnabled = false;
            deviceThread.join();
            deviceThread = null;
        }
    }


    private void addEvent(Runnable event) {
        synchronized (events) {
            events.add(event);
            events.notifyAll();
            System.out.println(events.toString());
        }
    }

    private void statusUpdateEvent(int value) {
        Log.debug("statusUpdateEvent( "
               + value + " )");
        addEvent(new StatusUpdateEventRequest(cb, new StatusUpdateEvent(this,
                value)));
    }


    private void stopEventThread() throws Exception {
        if (eventThreadEnabled) {
            eventThreadEnabled = false;
            synchronized (events) {
                events.notifyAll();
            }
            if (eventThread != null) {
                eventThread.join();
            }
            eventThread = null;
        }
    }

    private void startEventThread() throws Exception {
        if (!eventThreadEnabled) {
            eventThreadEnabled = true;
            if (eventThread == null) {
                eventThread = new Thread(new EventTarget(this));
                eventThread.start();
            }
        }
    }


    private void setStopEvents(boolean freezeEvents) throws Exception {
        checkOpened();
        if (freezeEvents != getFreezeEvents()) {
            this.stopEvents = freezeEvents;
            if (freezeEvents) {
                stopEventThread();
            } else {
                startEventThread();
            }
        }
    }

    private void checkOpened() throws JposException {
        if (!this.bOpened) {
            throw new JposException(JposConst.JPOS_S_CLOSED);
        }
    }

    private void setCoverState(boolean isCoverOpened) throws Exception {
        if (getCapCoverSensor()) {
            if (isCoverOpened != getCoverOpen()) {
                if (isCoverOpened) {
                    statusUpdateEvent(FPTR_SUE_COVER_OPEN);
                } else {
                    statusUpdateEvent(FPTR_SUE_COVER_OK);
                }
                this.bCoverOpen = isCoverOpened;
            }
        }
    }

    private synchronized void setPaperState(boolean isPaperEnded) throws Exception {
            if (isPaperEnded != this.bPaperEnd) {
                if (isPaperEnded) {
                    statusUpdateEvent(FPTR_SUE_REC_EMPTY);
                    resetPrinter();
                    this.bPaperEnd = isPaperEnded;
                   // throwJposException(new JposException(111, 8, PiritErrorMsg.getErrorMessage(8)));
                } else {
                    resetPrinter();
                    statusUpdateEvent(FPTR_SUE_JRN_PAPEROK);
                }
                this.bPaperEnd = isPaperEnded;
            }
    }

    private synchronized void updateStatus() throws Exception {
        if(PrinterStatus.isbPrinterOnline()){
            setPowerState(JPOS_PS_ONLINE);
        } else {setPowerState(JPOS_PS_OFFLINE);}
        setCoverState(PrinterStatus.isbCoverOpen());
        setPaperState(PrinterStatus.isbPaperEnd());
    }

    private void checkDeviceStatus() {
        try {
            int printerState = (int)PiritService.getInstance().getPrinterStatus();
            PrinterStatus.setbPaperEnd(PiritService.getBit(printerState, 1));
            PrinterStatus.setbCoverOpen(PiritService.getBit(printerState, 2));
            PrinterStatus.setbPrinterOnline(!PiritService.getBit(printerState, 7));
        } catch (Exception e) {
            Log.error(e);
            PrinterStatus.setbPrinterOnline(false);
        }
    }

    public void setEventCallbacks(EventCallbacks cb) {
        this.cb = cb;
    }

    private void setPowerState(int powerState) {
        if (powerState != this.powerState) {
            switch (powerState) {
                case JPOS_PS_ONLINE:
                    statusUpdateEvent(JPOS_SUE_POWER_ONLINE);
                    break;

                case JPOS_PS_OFF:
                    statusUpdateEvent(JPOS_SUE_POWER_OFF);
                    break;

                case JPOS_PS_OFFLINE:
                    statusUpdateEvent(JPOS_SUE_POWER_OFFLINE);
                    break;

                case JPOS_PS_OFF_OFFLINE:
                    statusUpdateEvent(JPOS_SUE_POWER_OFF_OFFLINE);
                    break;
            }
        }
        this.powerState = powerState;
    }

    class ExeptionListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            PrinterStatus.setbPaperEnd(true);
            try {
                updateStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public EventCallbacks createPiritCallBacks()
    {
        return new PiritCallBacks();
    }

    /**************************************************************/
    /******************** INNER CLASS EventCallbacks **************/
    /**************************************************************/

    public class PiritCallBacks implements EventCallbacks {

        public BaseControl getEventSource()
        {
            return (BaseControl)PiritSvc112.this;
        }

        public void fireDataEvent(DataEvent e)
        {
        }

        public void fireDirectIOEvent(DirectIOEvent e)
        {
            synchronized(directIOListeners)
            {
                // deliver the event to all registered listeners
                for(int x = 0; x < directIOListeners.size(); x++)
                {
                    ((DirectIOListener)directIOListeners.elementAt(x)).directIOOccurred(e);
                }
            }
        }

        public void fireErrorEvent(ErrorEvent e)
        {
            synchronized(errorListeners)
            {
                // deliver the event to all registered listeners
                for(int x = 0; x < errorListeners.size(); x++)
                {
                    ((ErrorListener)errorListeners.elementAt(x)).errorOccurred(e);
                }
            }
        }

        public void fireOutputCompleteEvent(OutputCompleteEvent e)
        {
            synchronized(outputCompleteListeners)
            {
                // deliver the event to all registered listeners
                for(int x = 0; x < outputCompleteListeners.size(); x++)
                {
                    ((OutputCompleteListener)outputCompleteListeners.elementAt(x)).outputCompleteOccurred(e);
                }
            }
        }

        public void fireStatusUpdateEvent(StatusUpdateEvent e)
        {
            synchronized(statusUpdateListeners)
            {
                // deliver the event to all registered listeners
                for(int x = 0; x < statusUpdateListeners.size(); x++)
                {
                    ((StatusUpdateListener)statusUpdateListeners.elementAt(x)).statusUpdateOccurred(e);
                }
            }
        }
    }


}
