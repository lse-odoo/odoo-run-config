package com.github.lseodoo.odoorunconfig.runConfig;

import com.github.lseodoo.odoorunconfig.runConig.OdooConfigurationFragmentEditor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class OdooRunConfiguration extends PythonRunConfiguration {

    private String myCustomField = ""; // Initialize to avoid nulls

    // The factory creates the configuration with a name.
    // We must call the parent's 2-argument constructor and then set the name.
    public OdooRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory);
        setName(name);
    }

    public String getMyCustomField() {
        return myCustomField;
    }

    public void setMyCustomField(String myCustomField) {
        this.myCustomField = myCustomField;
    }

    @Override
    public void readExternal(@NotNull Element element) {
        super.readExternal(element);
        // Load our custom field
        setMyCustomField(JDOMExternalizerUtil.readField(element, "MY_CUSTOM_FIELD", ""));
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        super.writeExternal(element);
        // Save our custom field
        JDOMExternalizerUtil.writeField(element, "MY_CUSTOM_FIELD", getMyCustomField());
    }

    @NotNull
    @Override
    protected SettingsEditor<PythonRunConfiguration> createConfigurationEditor() {
        return new OdooConfigurationFragmentEditor(this);
    }
}
