package ru.crystals.pos.cashdrawer.jpos.pirit;

import jpos.JposException;
import jpos.config.JposEntry;
import jpos.loader.JposServiceInstance;
import jpos.loader.JposServiceInstanceFactory;

public class PiritDrawerSvc112Factory
  implements JposServiceInstanceFactory
{
  public JposServiceInstance createInstance(String s, JposEntry jposentry)
    throws JposException
  {
    JposServiceInstance jposserviceinstance = null;
    try
    {
      Class class1 = Class.forName("ru.crystals.pos.cashdrawer.jpos.pirit.PiritDrawerSvc112");
      jposserviceinstance = (JposServiceInstance)class1.newInstance();
    }
    catch (ClassNotFoundException classnotfoundexception)
    {
      throw new JposException(104, "PiritDrawerSvc112 does not exist!", classnotfoundexception);
    }
    catch (InstantiationException instantiationexception)
    {
      throw new JposException(104, "PiritDrawerSvc112 could not be instantiated!", instantiationexception);
    }
    catch (IllegalAccessException illegalaccessexception)
    {
      throw new JposException(104, "PiritDrawerSvc112 creation failed!", illegalaccessexception);
    }
    return jposserviceinstance;
  }
}