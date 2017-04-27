package ru.crystals.pos.fiscalprinter.jpos.pirit;

import jpos.JposException;
import jpos.config.JposEntry;
import jpos.loader.JposServiceInstance;
import jpos.loader.JposServiceInstanceFactory;

public class PiritSvc112Factory implements JposServiceInstanceFactory {
    @Override
    public JposServiceInstance createInstance(String logicalName, JposEntry jposentry) throws JposException {
        JposServiceInstance jposserviceinstance = null;
        try {
            Class class1 = Class.forName("ru.crystals.pos.fiscalprinter.jpos.pirit.PiritSvc112");
            jposserviceinstance = (JposServiceInstance) class1.newInstance();
        } catch (ClassNotFoundException classnotfoundexception) {
            throw new JposException(104, "PiritSvc112 does not exist!", classnotfoundexception);
        } catch (InstantiationException instantiationexception) {
            throw new JposException(104, "PiritSvc112 could not be instantiated!", instantiationexception);
        } catch (IllegalAccessException illegalaccessexception) {
            throw new JposException(104, "PiritSvc112 creation failed!", illegalaccessexception);
        }
        return jposserviceinstance;
    }
}
