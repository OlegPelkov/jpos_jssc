package ru.crystals.pos.fiscalprinter.jpos.pirit.events;


import jpos.events.StatusUpdateEvent;
import jpos.services.EventCallbacks;
import org.apache.log4j.Logger;

public final class StatusUpdateEventRequest implements Runnable {
    private final EventCallbacks cb;
    private final StatusUpdateEvent event;
    private static final Logger Log = Logger.getLogger(StatusUpdateEventRequest.class);

    public StatusUpdateEventRequest(EventCallbacks cb, StatusUpdateEvent event) {
        this.cb = cb;
        this.event = event;
    }

    public void run() {
        cb.fireStatusUpdateEvent(event);
    }
}
