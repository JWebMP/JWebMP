package com.jwebmp.vertx.implementations;

import com.guicedee.guicedservlets.servlets.services.scopes.CallScope;
import com.guicedee.guicedservlets.websockets.options.CallScopeProperties;
import com.guicedee.guicedservlets.websockets.options.IGuicedWebSocket;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;

@CallScope
public class VertXEventBusBridgeIWebSocket implements IGuicedWebSocket
{
    @Inject
    private Vertx vertx;

    @Inject
    private CallScopeProperties callScopeProperties;

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
        //send to event bus address
        vertx.eventBus().publish(groupName, message);
    }

    @Override
    public void broadcastMessage(String message)
    {
        //broadcast to myself?
    }

    @Override
    public void broadcastMessageSync(String groupName, String message) throws Exception
    {
        vertx.eventBus().publish(groupName, message);
    }
}
