package com.everyparking.server.event;

import org.springframework.context.ApplicationEvent;

public class EntryLogChangeEvent extends ApplicationEvent {
    public EntryLogChangeEvent(Object source) {
        super(source);
    }
}
