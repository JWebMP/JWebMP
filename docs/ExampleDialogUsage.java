package com.jwebmp.plugins.aggrid;

import com.jwebmp.core.base.interfaces.IComponentHierarchyBase;

/**
 * Example documentation for using the ExampleDialogComponent.
 * This class provides a conceptual guide on how to create a dialog and configure a button to open it.
 * 
 * @author GedMarc
 * @version 1.0
 * @since 2023
 */
public class ExampleDialogUsage {

    /**
     * This class provides documentation on how to use the ExampleDialogComponent.
     * 
     * <h2>Basic Usage</h2>
     * 
     * <p>To use the ExampleDialogComponent, follow these steps:</p>
     * 
     * <h3>1. Create a dialog component</h3>
     * <pre>
     * ExampleDialogComponent<?> dialog = new ExampleDialogComponent<>();
     * </pre>
     * 
     * <h3>2. Add content to the dialog</h3>
     * <pre>
     * // Add a title
     * H1 dialogTitle = new H1("Example Dialog");
     * dialog.add(dialogTitle);
     * 
     * // Add content
     * Paragraph dialogContent = new Paragraph("This is an example dialog.");
     * dialog.add(dialogContent);
     * 
     * // Add a close button
     * Button closeButton = new Button("Close");
     * closeButton.asAttributeBase().addAttribute("(click)", "closeDialog(null)");
     * dialog.add(closeButton);
     * </pre>
     * 
     * <h3>3. Create a parent component with a button to open the dialog</h3>
     * <pre>
     * // Create a parent component
     * Div parentComponent = new Div();
     * 
     * // Create a button to open the dialog
     * Button openDialogButton = new Button("Open Dialog");
     * parentComponent.add(openDialogButton);
     * </pre>
     * 
     * <h3>4. Configure the button to open the dialog</h3>
     * <pre>
     * // This adds all necessary configurations to the button
     * dialog.configureButton(openDialogButton, "500px", "300px", 
     *         "{message: 'Hello from parent!'}", 
     *         "console.log('Dialog returned:', result);");
     * </pre>
     * 
     * <p>At this point, the button has all the necessary configurations to open the dialog:</p>
     * <ol>
     * <li>It has the openExampleDialog method added to it</li>
     * <li>It has the necessary imports and constructor parameters</li>
     * <li>It has the click event bound to the openExampleDialog method</li>
     * </ol>
     * 
     * <h3>5. Add components to your page</h3>
     * <pre>
     * page.add(parentComponent);
     * page.getBody().add(dialog);
     * </pre>
     * 
     * <h2>Alternative Approach: Using renderOpenMethod Directly</h2>
     * 
     * <p>For more control, you can use the renderOpenMethod directly:</p>
     * 
     * <pre>
     * // Create a dialog component
     * ExampleDialogComponent<?> dialog = new ExampleDialogComponent<>();
     * 
     * // Create a parent component with a button
     * Div parentComponent = new Div();
     * Button openDialogButton = new Button("Open Dialog");
     * parentComponent.add(openDialogButton);
     * 
     * // Render the open method on the button
     * dialog.renderOpenMethod(openDialogButton, "500px", "300px", 
     *         "console.log('Dialog returned:', result);");
     * 
     * // Manually add the click event to the button
     * openDialogButton.asAttributeBase().addAttribute("(click)", 
     *         "openExampleDialog({message: 'Hello from parent!'})");
     * </pre>
     * 
     * <p>This approach gives you more control over how the button is configured
     * but requires more manual work.</p>
     */
    public ExampleDialogUsage() {
        // This class is for documentation purposes only
    }
}
