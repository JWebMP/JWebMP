package com.jwebmp.vertx;

import com.guicedee.client.IGuiceContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JWebMPVertxTest
{

    @Test
    void builder()
    {
        IGuiceContext.instance()
                     .inject();
        System.out.println("Started");
    }
}