package com.everyparking.server.event;

import com.everyparking.server.data.dto.EntryLogDto;
import com.everyparking.server.data.dto.ParkingDto;
import com.everyparking.server.data.dto.ReportItemDto;
import com.everyparking.server.data.dto.UserViolationStatusDto;
import com.everyparking.server.data.entity.EntryLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EntryLogEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public EntryLogEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleEntityChangeEvent(EntryLogChangeEvent event) {
        if (event.getSource() instanceof EntryLogDto) {
            EntryLogDto changedEntity = (EntryLogDto) event.getSource();
            messagingTemplate.convertAndSend("/topic/entry-log", changedEntity);
        } else if (event.getSource() instanceof ParkingDto.ParkingInfoDto.Info) {
            ParkingDto.ParkingInfoDto.Info changedEntity = (ParkingDto.ParkingInfoDto.Info) event.getSource();
            messagingTemplate.convertAndSend("/topic/parking-status", changedEntity);
        } else if (event.getSource() instanceof ReportItemDto) {
            ReportItemDto changedEntity = (ReportItemDto) event.getSource();
            messagingTemplate.convertAndSend("/topic/report-log", changedEntity);
        } else if (event.getSource() instanceof UserViolationStatusDto) {
            UserViolationStatusDto changedEntity = (UserViolationStatusDto) event.getSource();
            messagingTemplate.convertAndSend("/topic/user-violation", changedEntity);
        }
    }

}
