package com.fcherchi.demo.clientnotifier;

import com.fcherchi.demo.drivers.rfidreader.ReaderListener;
import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;


@Component
public class EventNotifier implements ReaderListener {

    @Autowired
    private SimpMessagingTemplate template;

    @Override
    public void onUnknownResponseReceived(String readerId, byte[] data) {
    }

    @Override
    public boolean onTagRead(String readerId, TagReport tagReport) {

        sendMessage(readerId, tagReport.toString());
        return true;
    }

    private void sendMessage(String readerid, String message) {
        String msg = String.format("[%s] : %s", readerid, message);
        this.template.convertAndSend("/topic", msg);
    }
}
