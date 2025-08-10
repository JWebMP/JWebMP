package com.jwebmp.vertx.implementations;

import com.google.inject.Inject;
import com.guicedee.client.CallScopeProperties;
import com.guicedee.guicedservlets.servlets.services.scopes.CallScope;
import com.guicedee.guicedservlets.websockets.options.IGuicedWebSocket;
import io.vertx.core.Vertx;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class VertXStompEventBusBridgeIWebSocket implements IGuicedWebSocket
{
    @Inject
    private Vertx vertx;

    @Override
    public void addToGroup(String groupName) throws Exception
    {
        //do nothing, it is an event bus address
    }

    @Override
    public void removeFromGroup(String groupName) throws Exception
    {
        //do nothing, it is an event bus address
    }

    @Override
    public void broadcastMessage(String groupName, String message)
    {
        // Send to event bus address with STOMP destination prefix
        String stompDestination = "/toStomp/" + groupName;
        log.debug("Broadcasting message to STOMP destination: {}", stompDestination);
        vertx.eventBus()
             .publish(stompDestination, message);
    }

    @Override
    public void broadcastMessage(String message)
    {
        // Broadcast to myself?
        // Not implemented as it's not clear what the destination should be
        log.warn("broadcastMessage(String message) called but not implemented in VertXStompEventBusBridgeIWebSocket");
    }

    @Override
    public void broadcastMessageSync(String groupName, String message) throws Exception
    {
        // Send to event bus address with STOMP destination prefix
        String stompDestination = "/toStomp/" + groupName;
        log.debug("Broadcasting message synchronously to STOMP destination: {}", stompDestination);
        vertx.eventBus()
             .publish(stompDestination, message);
    }
}