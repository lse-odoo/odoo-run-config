package com.github.lseodoo.odoorunconfig.runConfig;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class OdooSettingsEditor extends SettingsEditor<OdooRunConfiguration> {

    private final JPanel myPanel;
    private final TextFieldWithBrowseButton scriptPathField;

    public OdooSettingsEditor() {
        scriptPathField = new TextFieldWithBrowseButton();
        scriptPathField.addBrowseFolderListener(null,
                FileChooserDescriptorFactory.createSingleFileDescriptor().withTitle("Select Script File"));
        myPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Script file", scriptPathField)
                .getPanel();
    }

    @Override
    protected void resetEditorFrom(OdooRunConfiguration odooRunConfiguration) {
        scriptPathField.setText(odooRunConfiguration.getScriptName());
    }

    @Override
    protected void applyEditorTo(@NotNull OdooRunConfiguration odooRunConfiguration) {
        odooRunConfiguration.setScriptName(scriptPathField.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }

}
