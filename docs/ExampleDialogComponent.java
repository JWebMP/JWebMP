package com.jwebmp.plugins.aggrid;

import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportModule;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgMethod;
import com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils;
import com.jwebmp.core.base.angular.client.services.interfaces.INgComponent;
import com.jwebmp.core.base.html.Button;
import com.jwebmp.core.base.html.DivSimple;
import com.jwebmp.core.base.interfaces.IComponentHierarchyBase;

/**
 * Example component demonstrating parent-child relationships in JWebMP.
 * This component shows how to implement a dialog that can be opened from a parent component.
 * 
 * @author GedMarc
 * @version 1.0
 * @since 2023
 * 
 * @param <J> The type parameter for the Curiously Recursive Template pattern
 */
@NgConstructorParameter("public dialogRef: ExampleDialogRef<any>")
@NgConstructorParameter("@Inject(EXAMPLE_DIALOG_DATA) public data: any")

// This constructor parameter will be added to the parent component
@NgConstructorParameter(value = "public dialog:ExampleDialog", onParent = true)

// Import references for this component
@NgImportReference(value = "Inject", reference = "@angular/core")
@NgImportReference(value = "ExampleDialog", reference = "./example-dialog")
@NgImportReference(value = "ExampleDialogRef", reference = "./example-dialog-ref")
@NgImportReference(value = "EXAMPLE_DIALOG_DATA", reference = "./example-dialog-data")

// This import reference will be added to the parent component
@NgImportReference(value = "ExampleDialog", reference = "./example-dialog", onParent = true)

// Module imports
@NgImportModule("ExampleDialogModule")

// Method to close the dialog
@NgMethod("""
        closeDialog(returnedData : any) {
            this.dialogRef.close(returnedData);
        }
        """)
public class ExampleDialogComponent<J extends ExampleDialogComponent<J>> extends DivSimple<J> implements INgComponent<J> {

    /**
     * The template for the open dialog method that will be added to the parent component
     */
    private static final String openMethodString = """
            openExampleDialog(data: any): void {
                this.dialog.open(ExampleDialogComponent, {
                    width: '%s',
                    height: '%s',
                    data: data
                })%s;
            }
            """;

    /**
     * The template for handling the dialog result
     */
    private static final String resultHandlerString = """
            .afterClosed().subscribe(result => {
                console.log('Dialog closed with result:', result);
                if (result !== undefined) {
                    %s
                }
            })
            """;

    /**
     * Renders the open dialog method on the parent component.
     * This method adds all necessary configurations to the parent component to make it capable of opening this dialog.
     * 
     * @param component The parent component that will open this dialog
     * @param width The width of the dialog
     * @param height The height of the dialog
     * @param resultHandler Code to execute when the dialog is closed with a result
     * @return The formatted method string
     */
    public String renderOpenMethod(IComponentHierarchyBase<?, ?> component, String width, String height, String resultHandler) {
        String resultHandlerCode = "";
        if (resultHandler != null && !resultHandler.isEmpty()) {
            resultHandlerCode = String.format(resultHandlerString, resultHandler);
        }

        String methodCode = String.format(openMethodString, width, height, resultHandlerCode);

        // Add all necessary configurations to the parent component
        component.addConfiguration(AnnotationUtils.getNgImportReference("ExampleDialog", "./example-dialog"));
        component.addConfiguration(AnnotationUtils.getNgImportModule("ExampleDialogModule"));
        component.addConfiguration(AnnotationUtils.getNgConstructorParameter("public dialog: ExampleDialog"));
        component.addConfiguration(AnnotationUtils.getNgMethod(methodCode));

        return methodCode;
    }

    /**
     * Helper method to configure a button to open this dialog.
     * This method adds all necessary configurations to the button and sets up the click event.
     * 
     * @param button The button that will open the dialog
     * @param width The width of the dialog
     * @param height The height of the dialog
     * @param data The data to pass to the dialog
     * @param resultHandler Code to execute when the dialog is closed with a result
     */
    public void configureButton(IComponentHierarchyBase<?, ?> button, String width, String height, String data, String resultHandler) {
        renderOpenMethod(button, width, height, resultHandler);
        button.asAttributeBase().addAttribute("(click)", "openExampleDialog(" + (data != null ? data : "{}") + ")");
    }

    @Override
    public Boolean standaloneOverride() {
        return true; // This component will be generated as a standalone component
    }
}
