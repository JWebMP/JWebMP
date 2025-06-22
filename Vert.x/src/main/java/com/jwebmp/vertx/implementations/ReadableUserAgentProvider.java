package com.jwebmp.vertx.implementations;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.guicedee.client.CallScopeProperties;
import com.guicedee.client.IGuiceContext;
import com.guicedee.guicedservlets.servlets.services.scopes.CallScope;
//import com.guicedee.client.CallScopeProperties;
import com.jwebmp.core.base.ajax.AjaxCall;
import com.jwebmp.core.base.ajax.HeadersDTO;
import io.vertx.core.http.HttpServerRequest;
import net.sf.uadetector.*;

import static com.guicedee.services.jsonrepresentation.json.StaticStrings.STRING_EMPTY;

@CallScope
public class ReadableUserAgentProvider implements Provider<ReadableUserAgent>
{
    @Inject
    private UserAgentStringParser userAgentStringParser;

    @Inject
    private CallScopeProperties callScopeProperties;

    @Override
    public ReadableUserAgent get()
    {
        try
        {
            HttpServerRequest request = (HttpServerRequest) callScopeProperties.getProperties()
                                                                               .get("HttpServerRequest");
            String headerInformation = request.getHeader("User-Agent");
            if (!Strings.isNullOrEmpty(headerInformation))
            {
                ReadableUserAgent agent = userAgentStringParser.parse(headerInformation);
                return agent;
            }
            return defaultAgent();
        }
        catch (ProvisionException | OutOfScopeException e)
        {
            try
            {
                AjaxCall<?> call = IGuiceContext.get(AjaxCall.class);
                HeadersDTO headers = call.getHeaders();
                if (!Strings.isNullOrEmpty(headers.useragent))
                {
                    ReadableUserAgent agent = userAgentStringParser.parse(headers.useragent);
                    return agent;
                }
            }
            catch (Throwable T)
            {
                return defaultAgent();
            }
            return defaultAgent();
        }
    }

    public static ReadableUserAgent defaultAgent()
    {
        return new UserAgent(DeviceCategory.EMPTY, UserAgentFamily.FIREFOX, STRING_EMPTY, STRING_EMPTY, OperatingSystem.EMPTY,
                STRING_EMPTY, STRING_EMPTY,
                UserAgentType.BROWSER, STRING_EMPTY, STRING_EMPTY, VersionNumber.UNKNOWN);
    }
}
