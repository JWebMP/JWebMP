package com.jwebmp.vertx;

import com.guicedee.guicedservlets.websockets.options.WebSocketMessageReceiver;
import com.guicedee.guicedservlets.websockets.services.IWebSocketMessageReceiver;

import java.util.Set;

public class JWebMPWebSocket implements IWebSocketMessageReceiver
{

    @Override
    public Set<String> messageNames()
    {
        return Set.of();
    }

    @Override
    public void receiveMessage(WebSocketMessageReceiver<?> message) throws SecurityException
    {

    }
}
