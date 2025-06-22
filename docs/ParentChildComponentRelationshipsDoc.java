package com.jwebmp.plugins.aggrid;

/**
 * # Parent-Child Component Relationships in JWebMP
 * 
 * <p>This class provides documentation for parent-child component relationships in JWebMP.</p>
 * 
 * <h2>Understanding Parent-Child Methods</h2>
 * 
 * <p>In many Angular applications, components need to interact with each other in a parent-child relationship. 
 * A common example is a button that opens a dialog. JWebMP provides mechanisms to handle these relationships elegantly.</p>
 * 
 * <h3>The onParent Attribute</h3>
 * 
 * <p>Several Angular annotations support an <code>onParent = true</code> attribute that indicates the annotation 
 * should be applied to the parent component rather than the component itself:</p>
 * 
 * <ul>
 * <li><code>@NgConstructorParameter(value = "...", onParent = true)</code>: Adds a constructor parameter to the parent component</li>
 * <li><code>@NgImportReference(value = "...", reference = "...", onParent = true)</code>: Adds an import reference to the parent component</li>
 * <li><code>@NgImportModule(value = "...", onParent = true)</code>: Adds a module import to the parent component</li>
 * <li><code>@NgMethod(value = "...", onParent = true)</code>: Adds a method to the parent component</li>
 * </ul>
 * 
 * <p>This pattern allows a component to define what its parent needs to properly use it, ensuring that all necessary 
 * configurations are available.</p>
 * 
 * <p>Example from MatDialog:</p>
 * <pre>
 * // Add constructor parameter to parent component
 * @NgConstructorParameter(value = "public dialog:MatDialog", onParent = true)
 * 
 * // Add import reference to parent component
 * @NgImportReference(value = "MatDialog", reference = "@angular/material/dialog", onParent = true)
 * </pre>
 * 
 * <h3>Rendering Methods on Parent Components</h3>
 * 
 * <p>For more complex parent-child interactions, you can create methods that add all necessary configurations 
 * to the parent component. The MatDialog component demonstrates this pattern with its <code>renderOpenMethod</code>:</p>
 * 
 * <pre>
 * public String renderOpenMethod(IComponentHierarchyBase&lt;?, ?&gt; component, String width, String height, String dataBinding, String performWithResult)
 * {
 *     // Format the method string
 *     String format = String.format(openMethodString, getTsFilename(getClass()), width, height, dataBinding, ...);
 *     
 *     // Add necessary configurations to the parent component
 *     component.addConfiguration(AnnotationUtils.getNgImportReference("MatDialogModule", "@angular/material/dialog"));
 *     component.addConfiguration(AnnotationUtils.getNgImportModule("MatDialogModule"));
 *     component.addConfiguration(AnnotationUtils.getNgConstructorParameter("public dialog:MatDialog"));
 *     component.addConfiguration(AnnotationUtils.getNgMethod(format));
 *     
 *     return format;
 * }
 * </pre>
 * 
 * <p>This method adds:</p>
 * <ol>
 * <li>Import references for required modules</li>
 * <li>Module imports</li>
 * <li>Constructor parameters for dependency injection</li>
 * <li>Methods to open the dialog</li>
 * </ol>
 * 
 * <h3>Output Bindings for Events</h3>
 * 
 * <p>When a child component needs to emit events to its parent, use output bindings:</p>
 * 
 * <ol>
 * <li>Define an output event emitter in the child component:
 * <pre>@NgOutput("eventName")</pre>
 * </li>
 * 
 * <li>Add a method to handle the event in the parent component:
 * <pre>
 * component.addConfiguration(AnnotationUtils.getNgMethod("""
 *         handleEvent(event: any) {
 *             console.log('Event received:', event);
 *             // Handle the event
 *         }
 *         """));
 * </pre>
 * </li>
 * 
 * <li>Bind the event in the parent component:
 * <pre>childComponent.addAttribute("(eventName)", "handleEvent($event)");</pre>
 * </li>
 * </ol>
 * 
 * <h2>Best Practices for Parent-Child Components</h2>
 * 
 * <ol>
 * <li><strong>Use onParent Attribute</strong>: Use the <code>onParent = true</code> attribute for annotations that need to be applied to the parent component.</li>
 * <li><strong>Create Helper Methods</strong>: Create helper methods like <code>renderOpenMethod</code> that add all necessary configurations to the parent component.</li>
 * <li><strong>Document Required Configurations</strong>: Clearly document what configurations are needed for a component to function properly.</li>
 * <li><strong>Use Output Bindings for Events</strong>: Use output bindings to handle events from child components.</li>
 * <li><strong>Keep Parent-Child Relationships Clear</strong>: Maintain clear separation of concerns between parent and child components.</li>
 * </ol>
 * 
 * <h2>Example: Button Opening a Dialog</h2>
 * 
 * <p>A common parent-child relationship is a button that opens a dialog. Here's how to implement this pattern:</p>
 * 
 * <ol>
 * <li>In the dialog component, use annotations with <code>onParent = true</code> to specify what the parent needs:
 * <pre>
 * @NgConstructorParameter(value = "public dialog:MatDialog", onParent = true)
 * @NgImportReference(value = "MatDialog", reference = "@angular/material/dialog", onParent = true)
 * </pre>
 * </li>
 * 
 * <li>Create a method to render the open dialog method on the parent:
 * <pre>
 * public void addOpenDialogMethod(Button&lt;?&gt; button, String width, String height) {
 *     renderOpenMethod(button, width, height, "{}", "");
 *     button.addAttribute("(click)", "openDialog({}, {})");
 * }
 * </pre>
 * </li>
 * 
 * <li>Use the method to configure a button:
 * <pre>
 * Button&lt;?&gt; openDialogButton = new Button&lt;&gt;();
 * openDialogButton.setText("Open Dialog");
 * myDialog.addOpenDialogMethod(openDialogButton, "500px", "400px");
 * </pre>
 * </li>
 * </ol>
 * 
 * <p>This approach ensures that all necessary configurations are added to the button component, making it fully functional for opening the dialog.</p>
 * 
 * @author GedMarc
 * @version 1.0
 * @since 2023
 */
public class ParentChildComponentRelationshipsDoc {
    // This class is for documentation purposes only
}