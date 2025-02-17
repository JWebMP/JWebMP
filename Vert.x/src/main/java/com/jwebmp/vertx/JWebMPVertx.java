package com.jwebmp.vertx;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.ProvisionException;
import com.google.inject.name.Names;
import com.guicedee.client.CallScoper;
import com.guicedee.client.Environment;
import com.guicedee.client.IGuiceContext;
import com.guicedee.guicedinjection.interfaces.IGuiceModule;
import com.guicedee.guicedservlets.servlets.services.scopes.CallScope;
import com.guicedee.guicedservlets.websockets.options.CallScopeProperties;
import com.guicedee.guicedservlets.websockets.options.CallScopeSource;
import com.guicedee.services.jsonrepresentation.IJsonRepresentation;
import com.guicedee.vertx.spi.VertxHttpServerConfigurator;
import com.jwebmp.core.annotations.PageConfiguration;
import com.jwebmp.core.base.ajax.*;
import com.jwebmp.core.base.interfaces.IComponentStyleBase;
import com.jwebmp.core.base.servlets.interfaces.IDataComponent;
import com.jwebmp.core.exceptions.InvalidRequestException;
import com.jwebmp.core.generics.FileTemplates;
import com.jwebmp.core.htmlbuilder.javascript.events.interfaces.IEvent;
import com.jwebmp.core.services.IPage;
import com.jwebmp.interception.services.AjaxCallIntercepter;
import com.jwebmp.interception.services.DataCallIntercepter;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.java.Log;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static com.guicedee.client.IGuiceContext.get;
import static com.guicedee.services.jsonrepresentation.json.StaticStrings.CHAR_DOT;
import static com.guicedee.services.jsonrepresentation.json.StaticStrings.CHAR_UNDERSCORE;
import static com.jwebmp.interception.services.JWebMPInterceptionBinder.AjaxCallInterceptorKey;
import static com.jwebmp.interception.services.JWebMPInterceptionBinder.DataCallInterceptorKey;
import static com.jwebmp.interception.services.StaticStrings.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@Log
@Singleton
public class JWebMPVertx extends AbstractModule implements IGuiceModule<JWebMPVertx>, VertxHttpServerConfigurator
{
    @Inject
    private Vertx vertx;


    @Override
    public HttpServer builder(HttpServer builder)
    {
        Router router = Router.router(vertx);
        configureDataServlet(router);
        configureCSSServlet(router);
        configureAjaxReceiveServlet(router);
        configureInternalDataServlet(router);

        configurePageServlet(router);

        return builder;
    }

    @Override
    protected void configure()
    {
        super.configure();
        if (Boolean.parseBoolean(Environment.getProperty("BIND_JW_PAGES", "true")))
        {
            ScanResult scanResult = IGuiceContext.instance()
                    .getScanResult();

            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(PageConfiguration.class))
            {
                if (classInfo.isAbstract() || classInfo.isInterface() || classInfo.isStatic())
                {
                    continue;
                }

                @SuppressWarnings("unchecked")
                Class<IPage<?>> pageClass = (Class<IPage<?>>) classInfo.loadClass();
                PageConfiguration pc = pageClass.getAnnotation(PageConfiguration.class);
                String url = pc.url();
                if (Strings.isNullOrEmpty(url))
                {
                    url = "/";
                }
                bind(Key.get(IPage.class, Names.named(url))).to(pageClass)
                        .in(CallScope.class);
            }
        }
    }

    private void configureDataServlet(Router router)
    {
        router.route(DATA_LOCATION)
                .handler(routingContext -> {
                    CompletableFuture.runAsync(() -> {
                        CallScoper scoper = IGuiceContext.get(CallScoper.class);
                        scoper.enter();
                        try
                        {
                            configureScopeProperties(routingContext);

                            HttpServerRequest request = routingContext.request();
                            HttpServerResponse response = routingContext.response();

                            String componentID = request.params()
                                    .get("component");
                            StringBuilder responseString = new StringBuilder();
                            try
                            {
                                @SuppressWarnings("unchecked")
                                Class<? extends IDataComponent> clazz = (Class<? extends IDataComponent>)
                                        Class.forName(componentID.replace(CHAR_UNDERSCORE, CHAR_DOT));
                                IDataComponent component = get(clazz);
                                StringBuilder renderData = component.renderData();
                                responseString.append(renderData);
                            } catch (Exception e)
                            {
                                log.log(Level.SEVERE, MessageFormat.format("Cannot render data for component {0}", componentID), e);
                                return;
                            }
                            for (DataCallIntercepter<?> dataCallIntercepter : get(DataCallInterceptorKey))
                            {
                                dataCallIntercepter.intercept(get(AjaxCall.class), get(AjaxResponse.class));
                            }
                            response.putHeader(CONTENT_TYPE, HTML_HEADER_JSON);
                            response.write(responseString.toString(), StandardCharsets.UTF_8.toString());
                        } finally
                        {
                            scoper.exit();
                        }
                    });
                });
    }

    private void configureCSSServlet(Router router)
    {
        router.route(CSS_LOCATION)
                .handler(routingContext -> {
                    CompletableFuture.runAsync(() -> {
                        CallScoper scoper = IGuiceContext.get(CallScoper.class);
                        scoper.enter();
                        try
                        {
                            configureScopeProperties(routingContext);
                            HttpServerResponse response = routingContext.response();
                            IPage<?> page = IGuiceContext.get(IPage.class);
                            @SuppressWarnings("rawtypes")
                            StringBuilder css = ((IComponentStyleBase) page.getBody()).renderCss(0);
                            response.putHeader(CONTENT_TYPE, HTML_HEADER_CSS);
                            response.write(css.toString(), StandardCharsets.UTF_8.toString());
                        } finally
                        {
                            scoper.exit();
                        }
                    });
                });
    }

    private void configureAjaxReceiveServlet(Router router)
    {
        router.route(AJAX_SCRIPT_LOCATION)
                .handler(routingContext -> {
                    CompletableFuture.runAsync(() -> {

                        configureScopeProperties(routingContext);
                        HttpServerRequest request = routingContext.request();

                        request.bodyHandler((handler) -> {
                            CallScoper scoper = IGuiceContext.get(CallScoper.class);
                            scoper.enter();
                            try
                            {
                                try
                                {
                                    String bodyString = handler.toString();
                                    AjaxCall<?> ajaxCallIncoming = IJsonRepresentation.From(bodyString, AjaxCall.class);
                                    AjaxCall<?> ajaxCall = get(AjaxCall.class);
                                    ajaxCall.fromCall(ajaxCallIncoming);
                                    ajaxCall.setPageCall(true);
                                    AjaxResponse<?> ajaxResponse = get(AjaxResponse.class);
                                    IEvent<?, ?> triggerEvent = processEvent();
                                    for (AjaxCallIntercepter<?> ajaxCallIntercepter : get(AjaxCallInterceptorKey))
                                    {
                                        ajaxCallIntercepter.intercept(ajaxCall, ajaxResponse);
                                    }
                                    triggerEvent.fireEvent(ajaxCall, ajaxResponse);
                                    routingContext.response()
                                            .putHeader(CONTENT_TYPE, HTML_HEADER_JSON);
                                    routingContext.response()
                                            .write(ajaxResponse.toJson());
                                } catch (InvalidRequestException ie)
                                {
                                    AjaxResponse<?> ajaxResponse = new AjaxResponse<>();
                                    ajaxResponse.setSuccess(false);
                                    AjaxResponseReaction<?> arr = new AjaxResponseReaction<>("Invalid Request Value", "A value in the request was found to be incorrect.<br>" + ie.getMessage(), ReactionType.DialogDisplay);
                                    arr.setResponseType(AjaxResponseType.Danger);
                                    ajaxResponse.addReaction(arr);
                                    log.log(Level.SEVERE, "[SessionID]-[" + request.streamId() + "];" + "[Exception]-[Invalid Request]", ie);
                                    routingContext.response()
                                            .putHeader(CONTENT_TYPE, HTML_HEADER_JSON);
                                    routingContext.response()
                                            .write(ajaxResponse.toJson());
                                } catch (Exception T)
                                {
                                    AjaxResponse<?> ajaxResponse = new AjaxResponse<>();
                                    ajaxResponse.setSuccess(false);
                                    AjaxResponseReaction<?> arr = new AjaxResponseReaction<>("Unknown Error", "An AJAX call resulted in an unknown server error<br>" + T.getMessage() + "<br>" + ExceptionUtils.getStackTrace(T), ReactionType.DialogDisplay);
                                    arr.setResponseType(AjaxResponseType.Danger);
                                    ajaxResponse.addReaction(arr);
                                    log.log(Level.SEVERE, "Unknown in ajax reply\n", T);
                                    routingContext.response()
                                            .putHeader(CONTENT_TYPE, HTML_HEADER_JSON);
                                    routingContext.response()
                                            .write(ajaxResponse.toJson());
                                }
                            } finally
                            {
                                scoper.exit();
                            }
                        });
                    });
                });
    }

    private void configurePageServlet(Router router)
    {
        if (Boolean.parseBoolean(Environment.getProperty("BIND_JW_PAGES", "true")))
        {
            ScanResult scanResult = IGuiceContext.instance()
                    .getScanResult();

            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(PageConfiguration.class))
            {
                if (classInfo.isAbstract() || classInfo.isInterface() || classInfo.isStatic())
                {
                    continue;
                }

                @SuppressWarnings("unchecked")
                Class<IPage<?>> pageClass = (Class<IPage<?>>) classInfo.loadClass();
                PageConfiguration pc = pageClass.getAnnotation(PageConfiguration.class);
                String url = pc.url();
                if (Strings.isNullOrEmpty(url))
                {
                    url = "/";
                }
                String finalUrl = url;
                router.getWithRegex(url + "*")
                        .handler(routingContext -> {
                            CompletableFuture.runAsync(() -> {
                                CallScoper scoper = IGuiceContext.get(CallScoper.class);
                                scoper.enter();
                                try
                                {
                                    configureScopeProperties(routingContext);
                                    IPage<?> page = IGuiceContext.get(Key.get(IPage.class, Names.named(finalUrl)));
                                    String pageHtml = page.toString(true);
                                    routingContext.response()
                                            .putHeader(CONTENT_TYPE, HTML_HEADER_DEFAULT_CONTENT_TYPE);
                                    routingContext.response()
                                            .write(pageHtml, StandardCharsets.UTF_8.toString());
                                } finally
                                {
                                    scoper.exit();
                                }
                            });
                        });
            }
        }
    }

    private void configureScopeProperties(RoutingContext routingContext)
    {
        CallScopeProperties callScopeProperties = IGuiceContext.get(CallScopeProperties.class);

        callScopeProperties.setSource(CallScopeSource.Http);

        callScopeProperties.getProperties()
                .put("RoutingContext", routingContext.request());
        callScopeProperties.getProperties()
                .put("HttpServerRequest", routingContext.request());
        callScopeProperties.getProperties()
                .put("HttpServerResponse", routingContext.request());
        callScopeProperties.getProperties()
                .put("StreamId", routingContext.request()
                        .streamId());

    }

    private void configureInternalDataServlet(Router router)
    {
        router.get(JW_SCRIPT_LOCATION)
                .handler(routingContext -> {
                    CompletableFuture.runAsync(() -> {
                        CallScoper scoper = IGuiceContext.get(CallScoper.class);
                        scoper.enter();
                        try
                        {
                            configureScopeProperties(routingContext);
                            FileTemplates.getFileTemplate(JWebMPVertx.class, "jwscript", "siteloader");
                            FileTemplates.getTemplateVariables()
                                    .put("SITEADDRESSINSERT", new StringBuilder(routingContext.request()
                                            .authority()
                                            .host()));
                            FileTemplates.getTemplateVariables()
                                    .put("ROOTADDRESSINSERT", new StringBuilder(routingContext.request()
                                            .authority()
                                            .host()));
                            try
                            {
                                FileTemplates.getTemplateVariables()
                                        .put("PAGECLASS", new StringBuilder(IGuiceContext.get(IPage.class)
                                                .getClass()
                                                .getCanonicalName()));

                            } catch (ProvisionException | OutOfScopeException e)
                            {
                                FileTemplates.getTemplateVariables()
                                        .put("PAGECLASS", new StringBuilder());
                            }

                            try
                            {
                                HttpServerRequest hsr = get(HttpServerRequest.class);
                                FileTemplates.getTemplateVariables()
                                        .put("%USERAGENT%", new StringBuilder(hsr.getHeader("user-agent")));
                                FileTemplates.getTemplateVariables()
                                        .put("%MYIP%", new StringBuilder(hsr.remoteAddress()
                                                .host()));
                                FileTemplates.getTemplateVariables()
                                        .put("%REFERER%", new StringBuilder(hsr.getHeader("referer")));
                            } catch (Throwable T)
                            {

                            }

                            StringBuilder output = FileTemplates.renderTemplateScripts("jwscript");
                            routingContext.response()
                                    .putHeader(CONTENT_TYPE, HTML_HEADER_JAVASCRIPT);
                            routingContext.response()
                                    .write(output.toString(), StandardCharsets.UTF_8.toString());
                        } finally
                        {
                            scoper.exit();
                        }
                    });
                });
    }


    protected IEvent<?, ?> processEvent() throws InvalidRequestException
    {
        IEvent<?, ?> triggerEvent = null;
        try
        {
            AjaxCall<?> call = get(AjaxCall.class);
            Class<?> eventClass = Class.forName(call.getClassName()
                    .replace(CHAR_UNDERSCORE, CHAR_DOT));
            triggerEvent = (IEvent<?, ?>) get(eventClass);
        } catch (ClassNotFoundException cnfe)
        {
            log.log(Level.FINEST, "Unable to find the event class specified", cnfe);
            throw new InvalidRequestException("The Event To Be Triggered Could Not Be Found");
        }
        return triggerEvent;
    }


}
