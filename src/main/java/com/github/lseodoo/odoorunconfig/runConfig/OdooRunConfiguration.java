package com.github.lseodoo.odoorunconfig.runConfig;

import com.github.lseodoo.odoorunconfig.runConig.OdooConfigurationFragmentEditor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.annotations.XCollection;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OdooRunConfiguration extends PythonRunConfiguration {

    public static class OdooState {
        @Attribute("odoo-bin-path")
        public String odooBinFilePath;

        @XCollection(elementName = "odoo-parmaters-addons-path")
        public List<String> odooParametersAddonsPath = new ArrayList<>();

        @Attribute("odoo-parameters-extra")
        public String odooParametersExtra;
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
    public String getScriptParameters() {
        StringBuilder sb = new StringBuilder();

        sb.append(super.getScriptParameters());

        if (!getAddonsPaths().isEmpty()) {
            sb.append(" --addons-path=");
            sb.append(String.join(",", getAddonsPaths()));
        }

        return sb.toString();
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
    public String getOdooParametersExtra() { return myOdooState.odooParametersExtra; }
    public void setOdooParametersExtra(String params) {
        myOdooState.odooParametersExtra = params;
        setScriptParameters(params);
    }
    public List<String> getAddonsPaths() { return myOdooState.odooParametersAddonsPath; }
    public void setAddonsPaths(List<String> paths) { myOdooState.odooParametersAddonsPath = paths; }
}
