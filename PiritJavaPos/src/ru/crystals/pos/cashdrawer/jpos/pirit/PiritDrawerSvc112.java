package ru.crystals.pos.cashdrawer.jpos.pirit;

import jpos.JposException;
import jpos.config.JposEntry;
import jpos.config.JposEntryRegistry;
import jpos.loader.JposServiceLoader;
import jpos.loader.JposServiceManager;
import jpos.services.CashDrawerService112;
import jpos.services.EventCallbacks;
import org.apache.log4j.Logger;
import ru.crystals.pos.fiscalprinter.jpos.pirit.connector.PiritService;

public class PiritDrawerSvc112
  implements CashDrawerService112
{
  private static final Logger Log = Logger.getLogger(PiritDrawerSvc112.class);
  protected boolean bCapStatusMultiDrawerDetect = false;
  protected boolean bCapStatus = true;
  protected boolean bCapServiceAllowManagement = false;
  protected int iState = 1;
  protected boolean bClaimed = false;
  protected boolean bDeviceEnabled = false;
  protected boolean bCapCompareFirmwareVersion = false;
  protected int iCapPowerReporting = 1;
  protected boolean bCapStatisticsReporting = false;
  protected boolean bCapUpdateFirmware = false;
  protected boolean bCapUpdateStatistics = false;
  protected String strHealthText = "";
  protected static final int iServiceVersion = 1012000;
  protected int iPowerNotify = 0;
  protected int iOutputID = -1;
  private boolean bOpened = false;

  public PiritDrawerSvc112()
  {
    Log.info("*************************************");
  }

  public void compareFirmwareVersion(String arg0, int[] arg1) throws JposException
  {
    throw new JposException(106, "Method is not supported by this service");
  }

  public boolean getCapCompareFirmwareVersion() throws JposException
  {
    Log.info("getCapCompareFirmwareVersion:" + this.bCapCompareFirmwareVersion);
    return this.bCapCompareFirmwareVersion;
  }

  public boolean getCapUpdateFirmware() throws JposException
  {
    Log.info("getCapUpdateFirmware:" + this.bCapUpdateFirmware);
    return this.bCapUpdateFirmware;
  }

  public void updateFirmware(String arg0) throws JposException
  {
    throw new JposException(106, "Method is not supported by this service");
  }

  public boolean getCapStatisticsReporting() throws JposException
  {
    Log.info("getCapStatisticsReporting:" + this.bCapStatisticsReporting);
    return this.bCapStatisticsReporting;
  }

  public boolean getCapUpdateStatistics() throws JposException
  {
    Log.info("getCapUpdateStatistics:" + this.bCapUpdateStatistics);
    return this.bCapUpdateStatistics;
  }

  public void resetStatistics(String arg0) throws JposException
  {
    throw new JposException(106, "Method is not supported by this service");
  }

  public void retrieveStatistics(String[] arg0) throws JposException
  {
    throw new JposException(106, "Method is not supported by this service");
  }

  public void updateStatistics(String arg0) throws JposException
  {
    throw new JposException(106, "Method is not supported by this service");
  }

  public boolean getCapStatusMultiDrawerDetect() throws JposException
  {
    Log.info("getCapStatusMultiDrawerDetect:" + this.bCapStatusMultiDrawerDetect);
    return this.bCapStatusMultiDrawerDetect;
  }

  public int getCapPowerReporting() throws JposException
  {
    Log.info("getCapPowerReporting:" + this.iCapPowerReporting);
    return this.iCapPowerReporting;
  }

  public int getPowerNotify() throws JposException
  {
    Log.info("getPowerNotify:" + this.iPowerNotify);
    return this.iPowerNotify;
  }

  public int getPowerState() throws JposException
  {
    throw new JposException(106, "Method is not supported by this service");
  }

  public void setPowerNotify(int arg0) throws JposException
  {
   // throw new JposException(106, "Method is not supported by this service");
  }

  public boolean getCapStatus() throws JposException
  {
    Log.info("bCapStatus:" + this.bCapStatus);
    return this.bCapStatus;
  }

  public boolean getDrawerOpened() throws JposException
  {
    boolean reult = false;
    try {
      reult = PiritService.getInstance().isMoneyDrawerOpen().booleanValue();
    } catch (Exception e) {
      throwJposException(e);
    }
    Log.info("getDrawerOpened:" + reult);
    return reult;
  }

  public void openDrawer() throws JposException
  {
    try {
      PiritService.getInstance().openMoneyDrawer();
      Log.info("openDrawer");
    } catch (Exception e) {
      throwJposException(e);
    }
  }

  public void waitForDrawerClose(int arg0, int arg1, int arg2, int arg3)
    throws JposException
  {
    throw new JposException(106, "Method is not supported by this service");
  }

  public void checkHealth(int arg0) throws JposException
  {
    try {
      Log.debug("begin checkHealth");
      if (!(this.bOpened))
        throw new JposException(101, "Service is not open");
      if (!(this.bClaimed))
        throw new JposException(103, "Device is not claimed");
      if (!(this.bDeviceEnabled))
        throw new JposException(105, "Device is not enabled");
      if (arg0 != 1)
        throw new JposException(106, "Device only supports internal health checks");

      this.strHealthText = "Successful";
      Log.info("checkHealth:" + this.strHealthText);
    } catch (Exception e) {
      throwJposException(e);

      Log.debug("end checkHealth"); } finally { Log.debug("end checkHealth");
    }
  }

  public void claim(int timeOut) throws JposException
  {
    try {
      Log.debug("begin claim");
      if (timeOut < -1)
        throw new JposException(106, "Invalid timeout value");
      if (!(this.bOpened))
        throw new JposException(101, "Service is not open");
      if (this.bClaimed)
        throw new JposException(102, "Service is already claimed");

      PiritService.getInstance().start();
      this.bClaimed = true;
      Log.info("claim");
    } catch (Exception e) {
      throwJposException(e);

      Log.debug("end claim"); } finally { Log.debug("end claim");
    }
  }

  public void close() throws JposException
  {
    try {
      if (!(this.bOpened))
        throw new JposException(101, "Service is not open");

      PiritService.getInstance().stop();
      this.bOpened = false;
      this.iState = 1;
      Log.info("close");
    } catch (Exception e) {
      throwJposException(e);
    }
  }

  public void directIO(int arg0, int[] arg1, Object arg2)
    throws JposException
  {
    throw new JposException(106, "Method is not supported by this service");
  }

  public boolean getCapServiceAllowManagement() throws JposException
  {
    Log.info("getCapServiceAllowManagement:" + this.bCapServiceAllowManagement);
    return this.bCapServiceAllowManagement;
  }

  public String getCheckHealthText() throws JposException
  {
    Log.info("getCheckHealthText:" + this.strHealthText);
    return this.strHealthText;
  }

  public boolean getClaimed() throws JposException
  {
    Log.info("getClaimed:" + this.bClaimed);
    return this.bClaimed;
  }

  public boolean getDeviceEnabled() throws JposException
  {
    if (!(this.bOpened))
      throw new JposException(101, "Service is not open");
    if (!(this.bClaimed))
      throw new JposException(103, "Device is not claimed");

    Log.info("getDeviceEnabled:" + this.bDeviceEnabled);
    return this.bDeviceEnabled;
  }

  public String getDeviceServiceDescription() throws JposException
  {
    if (!(this.bOpened))
      throw new JposException(101, "Service is not open");

    Log.info("getDeviceServiceDescription:" + PiritService.getInstance().getServiceDescription());
    return PiritService.getInstance().getServiceDescription();
  }

  public int getDeviceServiceVersion()
    throws JposException
  {
    if (!(this.bOpened))
      throw new JposException(101, "Service is not open");

    Log.info("getDeviceServiceVersion:1012000");
    return 1012000;
  }

  public boolean getFreezeEvents()
    throws JposException
  {
    throw new JposException(106, "Method is not supported by this service");
  }

  public String getPhysicalDeviceDescription() throws JposException
  {
    if (!(this.bOpened))
      throw new JposException(101, "Service is not open");

    Log.info("getPhysicalDeviceDescription:" + PiritService.getInstance().getPhysicalDescription());
    return PiritService.getInstance().getPhysicalDescription();
  }

  public String getPhysicalDeviceName()
    throws JposException
  {
    if (!(this.bOpened))
      throw new JposException(101, "Service is not open");

    Log.info("getPhysicalDeviceName:" + PiritService.getInstance().getPhysicalName());
    return PiritService.getInstance().getPhysicalName();
  }

  public int getState()
    throws JposException
  {
    return this.iState;
  }

  public void open(String logicalName, EventCallbacks arg1) throws JposException
  {
    try {
      if (this.bOpened) {
        throw new JposException(106, "Service is already open");
      }

      JposEntry jposEntry = JposServiceLoader.getManager().getEntryRegistry().getJposEntry(logicalName);
      if (jposEntry == null) {
        throw new JposException(109, "Logical device could not be found");
      }

      PiritService.getInstance().readConfig(jposEntry);
      this.bOpened = true;
      this.iState = 2;
      Log.info("open: logicalName=" + logicalName);
    } catch (Exception e) {
      throwJposException(e);
    }
  }

  public void release() throws JposException
  {
    Log.info("release");
    this.bClaimed = false;
  }

  public void setDeviceEnabled(boolean flag) throws JposException
  {
    try {
      if (!(this.bOpened))
        throw new JposException(101, "Service is not open");
      if (!(this.bClaimed))
        throw new JposException(103, "Device is not claimed");

      Log.info("setDeviceEnabled:" + flag);
      this.bDeviceEnabled = flag;
    } catch (Exception e) {
      throwJposException(e);
    }
  }

  public void setFreezeEvents(boolean arg0) throws JposException
  {
    throw new JposException(111, "Method is not supported by this service");
  }

  public void deleteInstance() throws JposException
  {
  }

  private void throwJposException(Exception e) throws JposException
  {
    Log.error("", e);
    if (e instanceof JposException)
      throw ((JposException)e);

    throw new JposException(111, "Unknown error", e);
  }
}