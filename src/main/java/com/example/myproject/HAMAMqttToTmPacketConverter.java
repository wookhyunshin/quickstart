package com.example.myproject;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.yamcs.TmPacket;
import org.yamcs.YConfiguration;
import org.yamcs.YamcsServer;
import org.yamcs.logging.Log;
import org.yamcs.time.Instant;
import org.yamcs.time.TimeService;
import org.yamcs.utils.TimeEncoding;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * MQTT packet converter that works with HAMA ground stations wrapper.
 * <p>
 * Messages are binary objects with payload data, no extra information.
 * </p>
 */

public class HAMAMqttToTmPacketConverter implements org.yamcs.mqtt.MqttToTmPacketConverter  {
    TimeService timeService;
    Log log;

    @Override
    public void init(String yamcsInstance, String linkName, YConfiguration config) {
        this.timeService = YamcsServer.getTimeService(yamcsInstance);
        this.log = new Log(this.getClass(), yamcsInstance);
    }

    @Override
    public List<TmPacket> convert(MqttMessage message) {
        try {
            var pkt = new TmPacket(timeService.getMissionTime(), message.getPayload());
	    pkt.setGenerationTime(TimeEncoding.getWallclockTime());
    	    pkt.setSequenceCount(0);
            return Collections.singletonList(pkt);
        } catch (Exception e) {
            log.warn("Cannot parse message {}: {}", message, e.toString());
            return Collections.emptyList();
        }
    }
}
