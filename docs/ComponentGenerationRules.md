# Component Generation Rules for JWebMP Components

## Introduction

The JWebMP framework provides a powerful way to generate HTML components and Angular components from Java code. This document serves as a comprehensive guide for creating well-structured, maintainable components within the JWebMP ecosystem, with special attention to Angular integration when needed.

### Purpose of This Document

This rules file was created to:
1. Document the correct usage of Angular annotations in JWebMP
2. Provide guidance on component structure and organization
3. Highlight lessons learned from the AgGrid implementation
4. Help developers avoid common pitfalls and mistakes
5. Establish best practices for component generation

### Why These Rules Matter

Following these rules ensures:
- Consistent component structure across the codebase
- Proper rendering of HTML, TypeScript, and CSS
- Efficient integration with Angular's component system
- Maintainable and extensible components
- Reduced bugs and issues related to component generation

## Table of Contents
1. [Component Structure](#component-structure)
2. [Code Documentation and Authorship](#code-documentation-and-authorship)
3. [Page Configurator Setup](#page-configurator-setup)
4. [Plugin Information](#plugin-information)
5. [Angular Annotations](#angular-annotations)
6. [TypeScript Integration](#typescript-integration)
7. [HTML Rendering](#html-rendering)
8. [WebSocket Integration](#websocket-integration)
9. [Component Design Patterns](#component-design-patterns)
10. [Common Mistakes to Avoid](#common-mistakes-to-avoid)

## Component Structure

### Basic Component Structure
A JWebMP component should:
- Extend an appropriate HTML base class (e.g., `DivSimple`)
- Have clear getter/setter methods for all properties
- Use the Curiously Recursive Template pattern (CRT) for proper type inheritance

For Angular components specifically, you should also:
- Implement the `INgComponent` interface
- Use appropriate annotations for Angular integration

Example of an Angular component:
```
public class MyAngularComponent<J extends MyAngularComponent<J>> extends DivSimple<J> implements INgComponent<J> {
    // Angular component implementation
}
```

Example of a non-Angular component:
```
public class MyRegularComponent<J extends MyRegularComponent<J>> extends DivSimple<J> {
    // Regular component implementation (no INgComponent)
}
```

### Curiously Recursive Template Pattern (CRT)
JWebMP components should always use the Curiously Recursive Template pattern:
- Define the class as `ClassNameE<J extends ClassName<J>>`
- Builder/chain/fluent methods should always return the `J` type, not `ClassName`
- This pattern enables proper type inheritance in the component hierarchy

Example:
```
public class MyComponent<J extends MyComponent<J>> extends DivSimple<J> {
    // Correct: Returns J type for fluent API
    public J setMyProperty(String value) {
        // Implementation
        return (J) this;
    }

    // Incorrect: Returns MyComponent type
    public MyComponent<J> setAnotherProperty(String value) {
        // Implementation
        return this;
    }
}
```

### When to Use INgComponent
The `INgComponent` interface and associated Angular annotations are only required for components that are actual Angular directives or components. Regular UI components that don't need Angular-specific functionality should only use the CRT pattern without implementing `INgComponent`.

Use `INgComponent` when:
- Your component needs to be rendered as an Angular component or directive
- You need Angular-specific features like lifecycle hooks, dependency injection, etc.
- Your component needs to interact with the Angular framework directly

Don't use `INgComponent` when:
- Your component is just a regular UI element without Angular-specific behavior
- Your component is used as a building block for other components
- You don't need TypeScript code generation for your component

### Options Pattern
For complex components, use a separate options class that:
- Extends `JavaScriptPart`
- Uses Jackson annotations for JSON serialization
- Has well-documented properties with getter/setter methods
- Uses the Curiously Recursive Template pattern (CRT) just like components
- Ensures JSON property names match exactly the names expected in TypeScript as {} objects

## Code Documentation and Authorship

All JWebMP components should follow consistent documentation and authorship practices:

### Author and Organization

- Always use `@author GedMarc` in the Javadoc for all classes
- The organization is JWebMP, which should be reflected in package names, GitHub URLs, and other references
- GitHub repository URLs should use the JWebMP organization: `https://github.com/JWebMP/...`

### Plugin Information and Last Updated Date

When using the `@PluginInformation` annotation, always update the `pluginLastUpdatedDate` field to the current date whenever you make changes to a plugin or project. This helps track when the plugin was last modified and ensures that users know they're using the most recent version.

Example:
```java
@PluginInformation(pluginName = "AG Grid",
        // other plugin information...
        pluginLastUpdatedDate = "2023-05-15", // Always update this to today's date when making changes
        pluginStatus = PluginStatus.Released
)
```

## Page Configurator Setup

Page configurators are a crucial part of JWebMP plugins as they handle the registration and configuration of resources required by the plugin. Every plugin should have a page configurator that follows these guidelines:

### Basic Structure

A page configurator should:
- Implement the `IPageConfigurator<T>` interface
- Be annotated with `@PluginInformation` to provide metadata about the plugin
- Have a static `enabled` field and methods to control whether the configurator is enabled
- Override the `configure(IPage<?> page)` method to configure the page
- Override the `enabled()` method to check if the configurator is enabled

Example:
```java
public class MyPluginPageConfigurator
        implements IPageConfigurator<MyPluginPageConfigurator>
{
    /**
     * If this configurator is enabled
     */
    private static boolean enabled = true;

    /**
     * Default constructor
     */
    public MyPluginPageConfigurator()
    {
        // Default constructor
    }

    /**
     * Method isEnabled returns the enabled status of this configurator
     *
     * @return the enabled status
     */
    public static boolean isEnabled()
    {
        return MyPluginPageConfigurator.enabled;
    }

    /**
     * Method setEnabled sets the enabled status of this configurator
     *
     * @param mustEnable the enabled status
     */
    public static void setEnabled(boolean mustEnable)
    {
        MyPluginPageConfigurator.enabled = mustEnable;
    }

    @NotNull
    @Override
    public IPage<?> configure(IPage<?> page)
    {
        // Configure the page here
        return page;
    }

    @Override
    public boolean enabled()
    {
        return MyPluginPageConfigurator.enabled;
    }
}
```

### Resource Registration

Page configurators can register various resources:

#### CSS Stylesheets
Use `@NgStyleSheet` to include CSS files:

```java
@NgStyleSheet(name = "My Plugin", value = "node_modules/my-plugin/dist/styles/my-plugin.css")
```

#### JavaScript Files
For Angular components, prefer proper imports over direct script inclusion:

```java
// Preferred: Using proper Angular/TypeScript imports in the component
@NgImportReference(value = "{ MyModule }", reference = "my-plugin")
@NgImportModule("MyModule")

// Avoid when possible: Direct script inclusion in the page configurator
@NgScript(name = "My Plugin", value = "node_modules/my-plugin/dist/my-plugin.min.js")
```

### Configuration Method

The `configure(IPage<?> page)` method is where you can add any page-level configurations needed by your plugin. For Angular components, this is often minimal since Angular handles resource loading:

```java
@NotNull
@Override
public IPage<?> configure(IPage<?> page)
{
    // For Angular components, this is often minimal
    return page;
}
```

## Plugin Information

The `@PluginInformation` annotation provides metadata about a plugin and should be used on the page configurator class. This information is used for documentation, dependency management, and plugin discovery.

### Required Fields

The following fields should always be provided:

- `pluginName`: The display name of the plugin
- `pluginDescription`: A brief description of what the plugin does
- `pluginUniqueName`: A unique identifier for the plugin (typically lowercase with hyphens)
- `pluginVersion`: The current version of the plugin
- `pluginCategories`: Comma-separated categories that describe the plugin
- `pluginSubtitle`: A short subtitle or tagline for the plugin
- `pluginLastUpdatedDate`: The date when the plugin was last updated (YYYY-MM-DD format)
- `pluginStatus`: The current status of the plugin (e.g., `PluginStatus.Released`)

### Optional Fields

These fields provide additional information and should be included when applicable:

- `pluginSourceUrl`: URL to the original library or framework
- `pluginWikiUrl`: URL to the plugin's wiki or documentation
- `pluginGitUrl`: URL to the plugin's Git repository
- `pluginIconUrl`: URL to an icon representing the plugin
- `pluginIconImageUrl`: URL to an image representing the plugin
- `pluginOriginalHomepage`: URL to the original library's homepage
- `pluginDownloadUrl`: URL where the plugin can be downloaded
- `pluginGroupId`: Maven group ID for the plugin
- `pluginArtifactId`: Maven artifact ID for the plugin
- `pluginModuleName`: Java module name for the plugin

### Example

```java
@PluginInformation(
        pluginName = "My Plugin",
        pluginDescription = "A plugin that provides awesome functionality",
        pluginUniqueName = "my-plugin",
        pluginVersion = "1.0.0",
        pluginCategories = "ui, component, awesome",
        pluginSubtitle = "Make your UI awesome",
        pluginSourceUrl = "https://original-library.com",
        pluginWikiUrl = "https://github.com/JWebMP/JWebMP-MyPlugin/wiki",
        pluginGitUrl = "https://github.com/JWebMP/JWebMP-MyPlugin",
        pluginIconUrl = "",
        pluginIconImageUrl = "",
        pluginOriginalHomepage = "https://original-library.com",
        pluginDownloadUrl = "https://mvnrepository.com/artifact/com.jwebmp.plugins/my-plugin",
        pluginGroupId = "com.jwebmp.plugins",
        pluginArtifactId = "my-plugin",
        pluginModuleName = "com.jwebmp.plugins.myplugin",
        pluginLastUpdatedDate = "2023-05-15",
        pluginStatus = PluginStatus.Released
)
```

```java
/**
 * The AG Grid Component for JWebMP
 *
 * @author GedMarc
 * @version 1.0.0
 * @since 2023
 */
@ComponentInformation(name = "AG Grid",
        description = "AG Grid is a feature-rich data grid supporting multiple frameworks",
        url = "https://www.ag-grid.com/",
        wikiUrl = "https://github.com/JWebMP/JWebMP-AgGrid/wiki")
public class MyComponent<J extends MyComponent<J>> extends DivSimple<J> {
    // Implementation
}
```

Example:
```
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, 
                getterVisibility = JsonAutoDetect.Visibility.NONE, 
                setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyComponentOptions<J extends MyComponentOptions<J>> extends JavaScriptPart<J> {
    // JSON property name must match the TypeScript property name
    @JsonProperty("propertyName")
    private String propertyName;

    // Getter method
    public String getPropertyName() {
        return propertyName;
    }

    // Setter method using CRT pattern
    public J setPropertyName(String propertyName) {
        this.propertyName = propertyName;
        return (J) this;
    }
}
```

## Angular Annotations

> **Note:** The following annotations are only applicable to components that implement `INgComponent` and are intended to be Angular directives or components. Regular UI components should not use these annotations.

### Overridable Dynamic Methods

The `INgComponent` interface provides several overridable methods that allow you to customize various aspects of the generated Angular component. These methods all return a `List<String>` that you can populate with your custom implementations:

#### Component Configuration Methods
- `styleUrls()`: Adds additional style URLs to the component (corresponds to the `styleUrls` field in Angular's @Component decorator)
- `styles()`: Adds inline styles to the component (corresponds to the `styles` field in Angular's @Component decorator)
- `animations()`: Adds Angular animations to the component (corresponds to the `animations` field in Angular's @Component decorator)
- `providers()`: Adds service providers to the component (corresponds to the `providers` field in Angular's @Component decorator)
- `inputs()`: Defines input properties for the component and can be used to dynamically register @NgInput annotations
- `outputs()`: Defines output properties for the component and can be used to dynamically register @NgOutput annotations
- `host()`: Configures host element properties and event bindings (corresponds to the `host` field in Angular's @Component decorator)

> **Note:** Each of these methods directly corresponds to a field in Angular's @Component decorator in TypeScript. When you override these methods, you're essentially configuring the same properties you would set in an Angular component's decorator.

##### Dynamic Input and Output Registration

The `inputs()` and `outputs()` methods provide a way to dynamically register @NgInput and @NgOutput annotations at runtime:

```java
@Override
public List<String> inputs() {
    List<String> inputProps = new ArrayList<>();
    inputProps.add("dynamicProperty");
    inputProps.add("anotherProperty");
    return inputProps;
}

@Override
public List<String> outputs() {
    List<String> outputProps = new ArrayList<>();
    outputProps.add("dynamicEvent");
    outputProps.add("anotherEvent");
    return outputProps;
}
```

Alternatively, you can use AnnotationUtils to add inputs and outputs dynamically:

```java
@Override
protected void init() {
    if (!isInitialized()) {
        // Dynamically add an @Input property
        addConfiguration(AnnotationUtils.getNgInput("dynamicProperty"));

        // Dynamically add an @Output event emitter
        addConfiguration(AnnotationUtils.getNgOutput("dynamicEvent", "onDynamicEvent"));
    }
    super.init();
}
```

This approach allows you to conditionally add inputs and outputs based on runtime configuration.

#### Lifecycle Hook Methods
- `afterViewInit()`: Code to execute after view initialization
- `afterViewChecked()`: Code to execute after every check of the component's view
- `afterContentChecked()`: Code to execute after every check of the component's content
- `afterContentInit()`: Code to execute after content initialization
- `onInit()`: Code to execute during the ngOnInit lifecycle hook
- `onDestroy()`: Code to execute when the component is destroyed

#### Constructor and Structure Methods
- `constructorBody()`: Adds code to the constructor body
- `constructorParameters()`: Adds parameters to the constructor
- `methods()`: Adds additional methods to the component
- `interfaces()`: Adds interfaces that the component implements
- `decorators()`: Adds decorators to the component class

Example of overriding these methods:

```java
@Override
public List<String> styleUrls() {
    List<String> urls = new ArrayList<>();
    urls.add("./additional-styles.scss");
    return urls;
}

@Override
public List<String> constructorBody() {
    List<String> code = new ArrayList<>();
    code.add("console.log('Component initialized');");
    code.add("this.setupInitialState();");
    return code;
}

@Override
public List<String> onInit() {
    List<String> code = new ArrayList<>();
    code.add("this.loadData();");
    code.add("this.registerEventListeners();");
    return code;
}
```

### Component References and Imports
- `@NgImportReference`: Specifies imports needed for the component. This annotation is used to import Angular modules, components, or services from external libraries.
- `@NgImportModule`: Specifies Angular modules to import into the application module.
- `@NgComponentReference`: References other Angular components or directives that this component depends on.

Example:
```
@NgImportReference(value = "{ MyModule }", reference = "my-module")
@NgImportModule("MyModule")
@NgComponentReference(OtherComponent.class)
```

### Using ViewChild in Angular Components

When you need to access a child component or element in your Angular component, you should use the Angular `@ViewChild` decorator. In JWebMP, this is done using the `@NgField` annotation with the Angular `@ViewChild` decorator syntax:

```java
// Import ViewChild from Angular core
@NgImportReference(value = "ViewChild", reference = "@angular/core")

// Define the ViewChild field
@NgField("@ViewChild(childElementRef) ref?;")
```
It is preferred to use the signal based viewChild().
Make sure that full field declaration is present and is optional if not a signal

This approach correctly generates the TypeScript code for accessing child components or elements in Angular.

> **Note:** The older `@NgViewChild` annotation is deprecated and should not be used. Always use the `@NgField` annotation with the Angular `@ViewChild` decorator syntax instead.

## NgImportReference Configuration

The `@NgImportReference` annotation is used to specify import statements in Angular components. The annotation has the following parameters:

- `reference`: The module reference path
- `value`: The import value
- `onParent`: Whether to apply to parent (default false)
- `onSelf`: Whether to apply to self (default true)
- `direct`: If the reference must be a direct import, using only the value field for rendering (default false)
- `wrapValueInBraces`: If the value must be wrapped in braces or is a precise reference (default true)

### Import Statement Rules

The import statements are rendered according to the following rules:

1. By default, the import value will be wrapped in braces `{}` using the annotation value. The value must not contain the braces.
2. If the import value is required to be a string (direct import), the `direct` flag can be set to `true`. This will wrap the value in single quotes `''` instead of braces.
3. If `wrapValueInBraces` is set to `false`, the value will not be wrapped in braces.
4. If `direct` is `false` (default), " from 'reference'" will be appended to the import statement.

#### Examples:

```typescript
// Default (wrapValueInBraces=true, direct=false)
import {Component} from '@angular/core';

// Direct import (direct=true)
import '@angular/core';

// No braces (wrapValueInBraces=false, direct=false)
import Component from '@angular/core';
```


### TypeScript Fields and Methods
- `@NgField`: Defines static TypeScript fields in the component. These fields are added directly to the component class in the generated TypeScript.
- `@NgMethod`: Defines static TypeScript methods in the component. These methods are added directly to the component class in the generated TypeScript.
- Dynamic fields and methods can be added by overriding the `fields()` and `methods()` methods from the INgComponent interface.

Example:
```
@NgField("myField: string = '';")
@NgMethod("""
        void myMethod($event: any) {
            console.log('Method called');
        }
        """)
```

For dynamic generation:
```
@Override
public List<String> fields() {
    var out = INgComponent.super.fields();
    out.add("dynamicFieldValue : " + someProperty);
    return out;
}
```

#### Using Text Blocks for Multiline Strings
Whenever possible, use Java text blocks (triple quotes `"""`) for multiline strings, especially in annotations and method implementations. Text blocks improve readability and make it easier to maintain complex TypeScript code:

```
@NgMethod("""
        updateDataset(label: string, dataset: ChartDataset) {
            let found: boolean = false;
            let index: number = -1;

            // More complex logic here
            if (this.chart && this.chart.data) {
                // Do something with the chart
            }

            return result;
        }
        """)
```

This approach preserves indentation and makes the TypeScript code more readable in your Java files.

### Constructor and Dependency Injection
- `@NgConstructorParameter`: Defines parameters for the component's constructor. Used for dependency injection in Angular.
- `@NgConstructorBody`: Defines code to be executed in the constructor body. This code runs when the component is instantiated.
- `@NgInject`: Creates injected variables that can be used throughout the component. This is an alternative to constructor injection.

Example:
```
@NgConstructorParameter("private service: MyService")
@NgConstructorBody("console.log('Constructor initialized');")
@NgInject(value = "injected", referenceName = "injectedService")
```

#### Dynamic Constructor Configuration
You can dynamically configure constructor parameters and body content by overriding the appropriate methods:

```
@Override
public List<String> constructorParameters() {
    var out = INgComponent.super.constructorParameters();
    out.add("private dynamicService: " + serviceName);
    return out;
}

@Override
public List<String> constructorBodies() {
    var out = INgComponent.super.constructorBodies();
    out.add("console.log('Dynamic constructor body for " + componentName + "');");
    return out;
}
```

### Lifecycle Hooks
- `@NgOnInit`: Code to execute during the ngOnInit lifecycle hook. This runs after the component is initialized.
- `@NgAfterContentInit`: Code to execute after content initialization.
- `@NgAfterContentChecked`: Code to execute during the ngAfterContentChecked lifecycle hook. This runs after every check of the component's content.
- `@NgAfterViewInit`: Code to execute after view initialization.
- `@NgAfterViewChecked`: Code to execute after every check of the component's view.
- `@NgOnDestroy`: Code to execute when the component is destroyed.
- `@NgOnChanges`: Code to execute when input properties change.
- `@NgDoCheck`: Code to execute during custom change detection. (Note: This is planned for future implementation)

Example:
```
@NgOnInit("console.log('Component initialized');")
@NgAfterContentChecked("console.log('Content checked');")
@NgOnDestroy("console.log('Component destroyed');")
```

## TypeScript Integration

> **Note:** The following TypeScript integration features are only applicable to components that implement `INgComponent` and are intended to be Angular directives or components. Regular UI components do not generate TypeScript code.

### TypeScript Dependencies
JWebMP provides several annotations for managing npm dependencies and script imports:

- `@TsDevDependency`: Used for dependencies that should be added to the `devDependencies` section in package.json
- `@TsDependency`: Used for dependencies that should be added to the main dependencies section in package.json
- `@NgScript`: Used for including JavaScript files directly (should be avoided when possible in favor of proper Angular/TypeScript imports)

Example:
```
// For development dependencies (devDependencies in package.json)
@TsDevDependency(value = "my-dev-package", version = "^1.0.0")

// For runtime dependencies (dependencies in package.json)
@TsDependency(value = "my-runtime-package", version = "^2.0.0")
```

#### Prefer Angular/TypeScript Imports Over @NgScript

Whenever possible, use proper Angular/TypeScript imports with `@NgImportReference` instead of `@NgScript`. This approach:

1. Follows Angular best practices for module imports
2. Ensures proper tree-shaking and bundling
3. Provides better type safety
4. Integrates better with Angular's dependency injection system

Example of preferred approach:
```java
// Preferred: Using proper Angular/TypeScript imports
@NgImportReference(value = "{ AgGridModule }", reference = "ag-grid-angular")
@NgImportReference(value = "{ ColDef, GridOptions }", reference = "ag-grid-community")
@NgImportModule("AgGridModule")
```

Example of approach to avoid:
```java
// Avoid when possible: Using direct script inclusion
@NgScript(name = "AG Grid", value = "node_modules/ag-grid-community/dist/ag-grid-community.min.js")
```

Only use `@NgScript` when a module doesn't support proper imports or when you need to include a script that isn't available as an npm package.

### Dynamic TypeScript Generation
Override the `fields()` and `methods()` methods to dynamically generate TypeScript code:

```
@Override
public List<String> fields() {
    var out = INgComponent.super.fields();
    out.add("dynamicField: string = '" + someValue + "';");
    return out;
}

@Override
public List<String> methods() {
    var out = INgComponent.super.methods();
    out.add("dynamicMethod($event: any) { console.log('Dynamic method called'); }");
    return out;
}
```

## HTML Rendering

### Tag and Attributes
The following applies to all JWebMP components, whether they implement `INgComponent` or not:
- Set the component's HTML tag using `setTag()`
- Add attributes using `addAttribute()` when working directly with components
- When working with interfaces, use `.asAttributeBase()`, `.asHierarchyBase()`, `.asStyleBase()`, etc. to access the interface methods

For Angular components specifically:
- Use square brackets for Angular binding: `[property]="value"`

Example with direct component:
```
// When using component directly
myComponent.setTag("my-component");
myComponent.addAttribute("class", "my-class");
myComponent.addAttribute("[value]", "myField");
```

Example with interfaces:
```
// When using interfaces
myComponent.asAttributeBase().addAttribute("class", "my-class");
myComponent.asHierarchyBase().addChildren(childComponent);
```

### Attribute Binding Methods
For fields in component classes that set attributes, provide two methods for each attribute:
1. A regular setter method for setting a plain string attribute
2. A `bindXXX` method for adding an Angular binding attribute

This pattern allows for both static values and dynamic Angular bindings:

```java
public class MyComponent<J extends MyComponent<J>> extends DivSimple<J> {
    // Field for the attribute
    private String value;

    // Regular setter for plain string attribute
    public J setValue(String value) {
        this.value = value;
        return (J) this;
    }

    // Binding method for Angular binding
    public J bindValue(String variableName) {
        addAttribute("[value]", variableName);
        return (J) this;
    }

    @Override
    protected void init() {
        if (!isInitialized()) {
            if (value != null) {
                addAttribute("value", value);
            }
        }
        super.init();
    }
}
```

Usage example:
```java
// Static value
myComponent.setValue("static text");

// Angular binding
myComponent.bindValue("dynamicVariable");
```

This approach provides flexibility for both static and dynamic attribute values while maintaining a clean API.

### Angular Signals and Input Binding

When working with Angular components, prefer using signals and input() where possible as they provide better reactivity and type safety. When binding to signals, models, inputs, or outputs, you must use the method reference syntax with parentheses:

```java
// Correct: Binding to a signal using method reference syntax
addAttribute("[data]", "chartData()");
addAttribute("[options]", "chartOptions()");

// Incorrect: Treating a signal as a field
addAttribute("[data]", "chartData");
```

#### Binding to Signals

Signals are defined using the `@NgSignal` annotation and should be bound using method reference syntax:

```java
// Define a signal in your component
@NgSignal(type = "ChartConfiguration | undefined", referenceName = "chartConfiguration", value = "undefined")

// Bind to the signal in your component's constructor or init method
addAttribute("[data]", "chartData()");
addAttribute("*ngIf", "chartConfiguration() && chartData()");
```

#### Binding to Inputs and Models

Similarly, when binding to Angular inputs or ngModel, use method reference syntax if they're implemented as signals or getter methods:

```java
// For a regular field
addAttribute("[ngModel]", "value");

// For a signal or getter method
addAttribute("[ngModel]", "value()");
```

This distinction is crucial because:
1. Fields are accessed directly by their name
2. Signals and getter methods need to be invoked with parentheses to get their current value

Always check whether you're binding to a field or a signal/method and use the appropriate syntax.

### Dynamic Configuration
All components can use the `init()` method for initialization:

```
@Override
protected void init() {
    if (!isInitialized()) {
        addAttribute("class", "my-class");
        // Common initialization
    }
    super.init();
}
```

For Angular components specifically, you can add Angular-related configurations:

```
@Override
protected void init() {
    if (!isInitialized()) {
        // Angular-specific configuration
        addConfiguration(AnnotationUtils.getNgComponentReference(SomeDirective.class));
        addAttribute("[value]", "myField");
    }
    super.init();
}
```

#### Using AnnotationUtils for Dynamic Configuration

The `AnnotationUtils` class provides utility methods to dynamically add annotations to components at runtime. This is particularly useful when you need to configure components based on runtime conditions or when you want to add annotations programmatically.

Here's a list of available annotations that can be dynamically added using AnnotationUtils:

1. **Component Structure Annotations**
   - `getNgField(String value)`: Adds a TypeScript field to the component
   - `getNgMethod(String value)`: Adds a TypeScript method to the component
   - `getNgInterface(String value)`: Adds an interface that the component implements
   - `getNgComponentReference(Class<? extends IComponent<?>> aClass)`: References another component or directive

2. **Import and Module Annotations**
   - `getNgImportReference(String importName, String reference)`: Adds an import statement
   - `getNgImportModule(String importName)`: Adds a module import
   - `getNgImportProvider(String importName)`: Adds a provider import

3. **Constructor and Dependency Injection Annotations**
   - `getNgConstructorParameter(String value)`: Adds a parameter to the constructor
   - `getNgConstructorBody(String value)`: Adds code to the constructor body
   - `getNgInject(String referenceName, String type)`: Creates an injected variable

4. **Lifecycle Hook Annotations**
   - `getNgOnInit(String value)`: Adds code to the ngOnInit lifecycle hook
   - `getNgOnDestroy(String value)`: Adds code to the ngOnDestroy lifecycle hook
   - `getNgAfterViewInit(String value)`: Adds code to the ngAfterViewInit lifecycle hook
   - `getNgAfterViewChecked(String value)`: Adds code to the ngAfterViewChecked lifecycle hook
   - `getNgAfterContentInit(String value)`: Adds code to the ngAfterContentInit lifecycle hook
   - `getNgAfterContentChecked(String value)`: Adds code to the ngAfterContentChecked lifecycle hook

5. **Component Input/Output Annotations**
   - `getNgInput(String value)`: Adds an @Input property to the component
   - `getNgOutput(String value, String parentMethodName)`: Adds an @Output event emitter to the component

6. **Angular Features Annotations**
   - `getNgModal(String value, String referenceName)`: Adds a modal reference
   - `getNgSignal(String referenceName, String value, String type)`: Adds an Angular signal
   - `getNgComponent(String value)`: Configures the component selector
   - `getNgComponentTagAttribute(String key, String value)`: Adds a tag attribute to the component

Example of using AnnotationUtils to dynamically configure a component:

```java
@Override
protected void init() {
    if (!isInitialized()) {
        // Add a component reference
        addConfiguration(AnnotationUtils.getNgComponentReference(SomeDirective.class));

        // Add an import reference
        addConfiguration(AnnotationUtils.getNgImportReference("{ SomeModule }", "some-module"));

        // Add a field
        addConfiguration(AnnotationUtils.getNgField("dynamicField: string = 'value';"));

        // Add a method
        addConfiguration(AnnotationUtils.getNgMethod("""
                dynamicMethod() {
                    console.log('This method was added dynamically');
                }
                """));

        // Add code to ngOnInit
        addConfiguration(AnnotationUtils.getNgOnInit("console.log('Added to ngOnInit dynamically');"));

        // Bind a value
        addAttribute("[value]", "dynamicField");
    }
    super.init();
}
```

This approach allows for highly dynamic component configuration based on runtime conditions, making your components more flexible and reusable.

## WebSocket Integration

### Dynamic Data Transport with WebSockets

For components that need to receive real-time updates from the server, JWebMP provides a powerful WebSocket integration mechanism. The ChartJS component demonstrates this pattern:

1. Create WebSocket receiver classes that extend `WebSocketAbstractCallReceiver`
2. Register these receivers in your component's initialization
3. Set up event listeners in the Angular component to process incoming data

Example from ChartJS:

```java
// Define receiver classes
protected static class InitialOptionsReceiver extends WebSocketAbstractCallReceiver {
    private String listenerName;
    private Class<? extends ChartJS> actionClass;

    // Constructor and implementation
    @Override
    public AjaxResponse<?> action(AjaxCall<?> call, AjaxResponse<?> response) {
        // Process the call and return data
        var initialEvents = IGuiceContext.get(actionClass).getInitialOptions();
        response.addDataResponse(listenerName, initialEvents);
        return response;
    }
}

// Register receivers in component initialization
protected void registerWebSocketListeners() {
    if (!IGuicedWebSocket.isWebSocketReceiverRegistered(getListenerName())) {
        IGuicedWebSocket.addWebSocketMessageReceiver(
            new InitialOptionsReceiver(getListenerName(), getClass()));
    }
}
```

In the Angular component, set up subscriptions to receive the data:

```typescript
// In constructor or ngOnInit
this.subscription = this.eventBusService.listen(this.listenerName, this.handlerId)
    .subscribe({
        next: (message: any) => {
            // Process incoming data
            this.chartConfiguration.set(message);
        },
        error: (error: any) => {
            console.log(error);
        }
    });
```

This pattern allows for efficient real-time updates between the server and client components.

## Component Design Patterns

### Natural Method Naming for Component Actions

Components should use natural, descriptive method names for actions they can perform. This improves readability and makes the API more intuitive. The DataTable component demonstrates this pattern:

```java
// Examples from DataTable
public J addCopyButton(String className) { /* implementation */ }
public J addCsvButton(String className) { /* implementation */ }
public J addExcelButton(String className) { /* implementation */ }
public J addPrintButton(String className) { /* implementation */ }
```

These method names clearly describe what the method does and follow a consistent pattern. Use this approach for your component's public API.

### Options Configuration Pattern

For complex components like AngularMaterialTable, use a dedicated options class to manage configuration:

```java
// In MatTable
private MatTablePaginator paginator = new MatTablePaginator();
private boolean sortEnabled;
private boolean paginateEnabled;

// In initialization
if (column.isSort()) {
    sortEnabled = true;
    addAttribute("matSort", "");
}

// In constructor body
if (sortEnabled) {
    strings.add("""
            if (this.dataSource && this.sort)
                this.dataSource.sort = this.sort!;""");
}
```

This pattern separates configuration from rendering logic and makes the component more maintainable.

## Parent-Child Component Relationships

### Understanding Parent-Child Methods

In many Angular applications, components need to interact with each other in a parent-child relationship. A common example is a button that opens a dialog. JWebMP provides mechanisms to handle these relationships elegantly.

#### The onParent Attribute

Several Angular annotations support an `onParent = true` attribute that indicates the annotation should be applied to the parent component rather than the component itself:

- `@NgConstructorParameter(value = "...", onParent = true)`: Adds a constructor parameter to the parent component
- `@NgImportReference(value = "...", reference = "...", onParent = true)`: Adds an import reference to the parent component
- `@NgImportModule(value = "...", onParent = true)`: Adds a module import to the parent component
- `@NgMethod(value = "...", onParent = true)`: Adds a method to the parent component

This pattern allows a component to define what its parent needs to properly use it, ensuring that all necessary configurations are available.

Example from MatDialog:
```java
// Add constructor parameter to parent component
@NgConstructorParameter(value = "public dialog:MatDialog", onParent = true)

// Add import reference to parent component
@NgImportReference(value = "MatDialog", reference = "@angular/material/dialog", onParent = true)
```

#### Rendering Methods on Parent Components

For more complex parent-child interactions, you can create methods that add all necessary configurations to the parent component. The MatDialog component demonstrates this pattern with its `renderOpenMethod`:

```java
public String renderOpenMethod(IComponentHierarchyBase<?, ?> component, String width, String height, String dataBinding, String performWithResult)
{
    // Format the method string
    String format = String.format(openMethodString, getTsFilename(getClass()), width, height, dataBinding, ...);

    // Add necessary configurations to the parent component
    component.addConfiguration(AnnotationUtils.getNgImportReference("MatDialogModule", "@angular/material/dialog"));
    component.addConfiguration(AnnotationUtils.getNgImportModule("MatDialogModule"));
    component.addConfiguration(AnnotationUtils.getNgConstructorParameter("public dialog:MatDialog"));
    component.addConfiguration(AnnotationUtils.getNgMethod(format));

    return format;
}
```

This method adds:
1. Import references for required modules
2. Module imports
3. Constructor parameters for dependency injection
4. Methods to open the dialog

#### Output Bindings for Events

When a child component needs to emit events to its parent, use output bindings:

1. Define an output event emitter in the child component:
```java
@NgOutput("eventName")
```

2. Add a method to handle the event in the parent component:
```java
component.addConfiguration(AnnotationUtils.getNgMethod("""
        handleEvent(event: any) {
            console.log('Event received:', event);
            // Handle the event
        }
        """));
```

3. Bind the event in the parent component:
```java
childComponent.addAttribute("(eventName)", "handleEvent($event)");
```

### Best Practices for Parent-Child Components

1. **Use onParent Attribute**: Use the `onParent = true` attribute for annotations that need to be applied to the parent component.

2. **Create Helper Methods**: Create helper methods like `renderOpenMethod` that add all necessary configurations to the parent component.

3. **Document Required Configurations**: Clearly document what configurations are needed for a component to function properly.

4. **Use Output Bindings for Events**: Use output bindings to handle events from child components.

5. **Keep Parent-Child Relationships Clear**: Maintain clear separation of concerns between parent and child components.

## Common Mistakes to Avoid

### Annotation Usage
- Don't forget to add `@NgImportReference` for all external Angular modules
- Use `@TsDevDependency` for development dependencies and `@TsDependency` for runtime dependencies
- Use the correct syntax for Angular bindings (e.g., `[property]="value"`)
- Use text blocks (""") in annotations whenever possible for multiline strings
- Ensure all referenced components have proper `@NgComponentReference` annotations

### Component Structure
- Always use the Curiously Recursive Template pattern (CRT) with `ClassNameE<J extends ClassName<J>>`
- Ensure builder/chain/fluent methods always return the `J` type, not `ClassName`
- Only implement `INgComponent` for actual Angular components/directives, not for regular UI components
- Don't mix HTML rendering logic with business logic
- Ensure options classes are properly serializable with Jackson annotations
- Ensure options classes also use the CRT pattern just like components
- Ensure JSON property names in options classes match exactly the names expected in TypeScript
- Don't hardcode values that should be configurable
- Don't avoid deep inheritance hierarchies for components - by default they are deep and this is the expected pattern
- Keep component responsibilities focused and specific

### TypeScript Generation
- Ensure all TypeScript code is syntactically correct
- Use proper TypeScript types for fields and method parameters
- Avoid generating duplicate fields or methods
- Be careful with string concatenation in dynamic field/method generation
- Escape special characters properly in generated TypeScript strings

### Performance Considerations
- Initialize collections only when needed
- Use lazy initialization for expensive operations
- Check `isInitialized()` before performing initialization logic
- Avoid unnecessary object creation in render methods
- Cache expensive computations where appropriate

### Lessons from AgGrid Implementation
The AgGrid implementation revealed several important lessons:

1. **Module Dependencies**: Ensure all required Angular modules are properly imported. AgGrid requires both `ag-grid-community` and `ag-grid-angular` dependencies.

2. **Component Configuration**: Use a dedicated options class for complex components rather than adding numerous properties to the component itself.

3. **Attribute Binding**: Be careful with attribute binding syntax. AgGrid uses attributes like `[rowData]` and `[columnDefs]` which must be properly bound to component properties.

4. **Event Handling**: Properly define event handlers using the `@NgMethod` annotation or by overriding the `methods()` method.

5. **Initialization Order**: Be mindful of the initialization order. Some configurations need to be set before others to work properly.

6. **Styling Considerations**: Include necessary CSS classes and styles. AgGrid requires theme classes like `ag-theme-alpine` to render properly.

7. **Dynamic Content**: When generating dynamic content, ensure proper escaping and formatting of the generated TypeScript code.

By following these rules and best practices, you can create well-structured, maintainable Angular components in the JWebMP framework.
