package com.jwebmp.vertx;

import com.guicedee.guicedservlets.websockets.options.WebSocketMessageReceiver;
import com.guicedee.guicedservlets.websockets.services.IWebSocketMessageReceiver;
import io.smallrye.mutiny.Uni;

import java.util.Set;

public class JWebMPWebSocket implements IWebSocketMessageReceiver<Void, JWebMPWebSocket>
{

    @Override
    public Set<String> messageNames()
    {
        return Set.of();
    }

    @Override
    public Uni<Void> receiveMessage(WebSocketMessageReceiver<?> message) throws SecurityException
    {
        return Uni.createFrom()
                  .voidItem();
    }
}
