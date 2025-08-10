package com.jwebmp.vertx.implementations;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.AbstractModule;
import com.guicedee.guicedinjection.interfaces.IGuiceModule;
import com.guicedee.guicedservlets.servlets.services.scopes.CallScope;
import com.guicedee.guicedservlets.websockets.options.IGuicedWebSocket;
import com.guicedee.services.jsonrepresentation.IJsonRepresentation;
import io.vertx.core.json.jackson.DatabindCodec;
import net.sf.uadetector.ReadableUserAgent;


public class JWebMPVertxBinder extends AbstractModule implements IGuiceModule<JWebMPVertxBinder>
{

    @Override
    protected void configure()
    {
        bind(ReadableUserAgent.class).toProvider(ReadableUserAgentProvider.class)
                                     .in(CallScope.class);
        //bind(IGuicedWebSocket.class).to(VertXEventBusBridgeIWebSocket.class);
        bind(IGuicedWebSocket.class).to(VertXStompEventBusBridgeIWebSocket.class);

        ObjectMapper mapper = DatabindCodec.mapper();

        IJsonRepresentation.configureObjectMapper(DatabindCodec.mapper());
        //ObjectMapper prettyMapper = DatabindCodec.prettyMapper();

        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        // Enable strict JSON rendering (force field names to have quotes)
        mapper.configure(JsonWriteFeature.QUOTE_FIELD_NAMES.mappedFeature(), true);
        mapper.configure(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true);
        //mapper.enable(SerializationFeature.QUOTE_FIELD_NAMES);
        //prettyMapper.enable(SerializationFeature.QUOTE_FIELD_NAMES);

    }
}
