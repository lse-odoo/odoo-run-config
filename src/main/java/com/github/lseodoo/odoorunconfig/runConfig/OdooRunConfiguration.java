package com.github.lseodoo.odoorunconfig.runConfig;

import com.github.lseodoo.odoorunconfig.runConig.OdooConfigurationFragmentEditor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.XmlSerializer;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class OdooRunConfiguration extends PythonRunConfiguration {

    public static class OdooState {
        @Attribute("odoo-bin-path")
        public String odooBinFilePath;

        @Attribute("odoo-parameters")
        public String odooParameters;
    }

    private final OdooState myOdooState = new OdooState();

    public OdooRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory);
        setName(name);

        // Default option (difference from python configuration)
        setAddContentRoots(false);
        setAddSourceRoots(false);
        setEmulateTerminal(true);
    }


    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();

        if (getOdooBinFilePath() != null && !getOdooBinFilePath().endsWith("odoo-bin")) {
            throw new RuntimeConfigurationException("The odoo-bin file path must end with 'odoo-bin'");
        }
    }

    @Override
    public void readExternal(@NotNull Element element) {
        super.readExternal(element);
        XmlSerializer.deserializeInto(this, element);
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        super.writeExternal(element);
        XmlSerializer.serializeInto(this, element);
    }

    @NotNull
    @Override
    protected SettingsEditor<PythonRunConfiguration> createConfigurationEditor() {
        return new OdooConfigurationFragmentEditor(this);
    }

    public String getOdooBinFilePath() { return myOdooState.odooBinFilePath; }
    public void setOdooBinFilePath(String path) {
        myOdooState.odooBinFilePath = path;
        setScriptName(path);
    }
    public String getOdooParameters() { return myOdooState.odooParameters; }
    public void setOdooParameters(String params) {
        myOdooState.odooParameters = params;
        setScriptParameters(params);
    }
}
