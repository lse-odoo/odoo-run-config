package com.github.lseodoo.odoorunconfig.runConfig;

import com.github.lseodoo.odoorunconfig.common.OdooRunConfig;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializer;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OdooRunConfiguration extends PythonRunConfiguration {

    public final OdooRunConfig myOdooRunConfig = new OdooRunConfig();

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
        List<String> allOdooParams = new ArrayList<>();

        if (getOdooBinFilePath() != null) {
            allOdooParams.add("-d");
            allOdooParams.add(getOdooParametersDb());
        }

        if (!getAddonsPaths().isEmpty()) {
            allOdooParams.add("--addons-path="+String.join(",", getAddonsPaths()));
        }

        allOdooParams.add(super.getScriptParameters());

        return String.join(" ", allOdooParams);
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

    // TODO: double check if still used and why
    public String getOdooBinFilePath() { return myOdooRunConfig.getOdooBinFilePath(); }
    public void setOdooBinFilePath(String path) {
        myOdooRunConfig.setOdooBinFilePath(path);
        setScriptName(path);
    }
    public String getOdooParametersDb() { return myOdooRunConfig.getOdooParametersDb(); }
    public void setOdooParametersDb(String odooParametersDb) { this.myOdooRunConfig.setOdooParametersDb(odooParametersDb); }
    public String getOdooParametersExtra() { return myOdooRunConfig.getOdooParametersExtra(); }
    public void setOdooParametersExtra(String params) {
        myOdooRunConfig.setOdooParametersExtra(params);
        setScriptParameters(params);
    }
    public List<String> getAddonsPaths() { return myOdooRunConfig.getOdooParametersAddonsPath(); }
    public void setAddonsPaths(List<String> paths) { myOdooRunConfig.setOdooParametersAddonsPath(paths); }
}
