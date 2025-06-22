# Parent-Child Component Relationships in JWebMP

## Understanding Parent-Child Methods

In many Angular applications, components need to interact with each other in a parent-child relationship. A common example is a button that opens a dialog. JWebMP provides mechanisms to handle these relationships elegantly.

### The onParent Attribute

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

### Rendering Methods on Parent Components

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

### Output Bindings for Events

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

## Best Practices for Parent-Child Components

1. **Use onParent Attribute**: Use the `onParent = true` attribute for annotations that need to be applied to the parent component.

2. **Create Helper Methods**: Create helper methods like `renderOpenMethod` that add all necessary configurations to the parent component.

3. **Document Required Configurations**: Clearly document what configurations are needed for a component to function properly.

4. **Use Output Bindings for Events**: Use output bindings to handle events from child components.

5. **Keep Parent-Child Relationships Clear**: Maintain clear separation of concerns between parent and child components.

## Example: Button Opening a Dialog

A common parent-child relationship is a button that opens a dialog. Here's how to implement this pattern:

1. In the dialog component, use annotations with `onParent = true` to specify what the parent needs:
```java
@NgConstructorParameter(value = "public dialog:MatDialog", onParent = true)
@NgImportReference(value = "MatDialog", reference = "@angular/material/dialog", onParent = true)
```

2. Create a method to render the open dialog method on the parent:
```java
public void addOpenDialogMethod(Button<?> button, String width, String height) {
    renderOpenMethod(button, width, height, "{}", "");
    button.addAttribute("(click)", "openDialog({}, {})");
}
```

3. Use the method to configure a button:
```java
Button<?> openDialogButton = new Button<>();
openDialogButton.setText("Open Dialog");
myDialog.addOpenDialogMethod(openDialogButton, "500px", "400px");
```

This approach ensures that all necessary configurations are added to the button component, making it fully functional for opening the dialog.