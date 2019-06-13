package com.bsep_sbz.SIEMCenter.websockets;

import com.bsep_sbz.SIEMCenter.model.sbz.log.Alarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer
{
    @Autowired
    private SimpMessagingTemplate template;

    public void sendMessage(Alarm alarm)
    {
        this.template.convertAndSend("/topic", alarm);
    }
}
