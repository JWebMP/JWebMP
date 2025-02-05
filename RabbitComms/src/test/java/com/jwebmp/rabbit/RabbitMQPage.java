package com.jwebmp.rabbit;

import com.jwebmp.core.base.angular.client.annotations.angular.NgComponent;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.services.SocketClientService;
import com.jwebmp.core.base.angular.client.services.interfaces.INgComponent;
import com.jwebmp.core.base.html.DivSimple;

@NgComponent(value = "RabbitMQPage")
@NgComponentReference(RabbitMQProvider.class)
@NgComponentReference(SocketClientService.class)
@NgConstructorParameter(value = "private rabbitMqProvider : RabbitMQProvider")
public class RabbitMQPage extends DivSimple<RabbitMQPage> implements INgComponent<RabbitMQPage> {

}
