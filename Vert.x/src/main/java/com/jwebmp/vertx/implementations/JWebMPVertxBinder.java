package com.jwebmp.vertx.implementations;

import com.google.inject.AbstractModule;
import com.guicedee.guicedinjection.interfaces.IGuiceModule;
import com.guicedee.guicedservlets.servlets.services.scopes.CallScope;
import net.sf.uadetector.ReadableUserAgent;


public class JWebMPVertxBinder extends AbstractModule implements IGuiceModule<JWebMPVertxBinder>
{

    @Override
    protected void configure()
    {
        bind(ReadableUserAgent.class).toProvider(ReadableUserAgentProvider.class).in(CallScope.class);
    }
}
