package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.domain.EventLog;

import java.time.LocalDateTime;

public class EventLogFactory {

    EventLog build(String type, String userEmail) throws Exception {
        switch (type) {
            case "CREATED": return EventLog.builder()
                    .logTime(LocalDateTime.now())
                    .logMessage(userEmail + " created a new event")
                    .build();

            case "JOINED": return EventLog.builder()
                    .logTime(LocalDateTime.now())
                    .logMessage(userEmail + " registered to the event")
                    .build();

            case "LEFT": return EventLog.builder()
                    .logTime(LocalDateTime.now())
                    .logMessage(userEmail + " unregistered from the event")
                    .build();

            default: throw new Exception("event log type not supported");
        }
    }
}
