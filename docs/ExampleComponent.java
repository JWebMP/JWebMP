package com.jwebmp.plugins.aggrid;

import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.NgAfterContentChecked;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnInit;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.annotations.structures.NgInject;
import com.jwebmp.core.base.angular.client.annotations.structures.NgMethod;
import com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils;
import com.jwebmp.core.base.angular.client.services.interfaces.INgComponent;
import com.jwebmp.core.base.angular.modules.directives.OnClickListenerDirective;
import com.jwebmp.core.base.html.DivSimple;

import java.util.List;

@NgField("staticTypescriptField : string = '';")
@NgMethod("""
        void staticMethod($event:any)
        {
           return 'result';
        }""")
//constructor parameters usually have a component reference
@NgConstructorParameter("private service : Service")
//reference other INgxxxx using NgComponentReference
@NgComponentReference(OnClickListenerDirective.class)

//to create injected variables
//will usually also have an associate NgComponentReference
@NgInject(value = "injected", referenceName = "injectedService")

@NgConstructorBody("""
        console.log('rendered in the constructor body')""")
@NgOnInit("""
        console.log('rendered inside ngOnInit()');""")
//all the life cycle methods are done like this
@NgAfterContentChecked("""
        console.log('rendered inside ngAfterContentChecked()');""")


public class ExampleComponent<J extends ExampleComponent<J>>
        extends DivSimple<J>
        implements INgComponent<J>
{
    String name;

    @Override
    public List<String> fields()
    {
        var out = INgComponent.super.fields();
        out.add("dynamicFieldValue : " + name);
        return out;
    }

    //to build methods dynamically to the configuration of the componentt
    @Override
    public List<String> methods()
    {
        var out = INgComponent.super.methods();
        out.add("""
                void dynamicMethod($event:any)
                {
                    console.log('dynamicMethod %s called');
                }
                """.formatted(name));
        return out;
    }

    @Override
    protected void init()
    {
        if (!isInitialized())
        {
            //to add custom annotations during tag render phase, supports all the Ng annotations and setups
            //can also be used to dynamically configure modules, directives, imports etc
            addConfiguration(AnnotationUtils.getNgComponentReference(OnClickListenerDirective.class));

            addAttribute("[value]", "staticTypescriptField");
        }
        super.init();
    }

    public static void main(String[] args)
    {
        var component = new ExampleComponent<>().setTag("ex-ample-component");
        //<ex-ample-component [value]="staticTypescriptField"></ex-ample-component>
        System.out.println(component.toString(true));
    }
}
