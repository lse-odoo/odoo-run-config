package com.github.lseodoo.odoorunconfig.runConfig;

import com.github.lseodoo.odoorunconfig.runConig.OdooConfigurationFragmentEditor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class OdooRunConfiguration extends PythonRunConfiguration {

    public static final String ODOO_BIN_PATH_NAME = "ODOO_BIN_PATH_NAME";

    private String odooBinFilePath;

    // The factory creates the configuration with a name.
    // We must call the parent's 2-argument constructor and then set the name.
    public OdooRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory);
        setName(name);
    }

    public String getOdooBinFilePath() {
        return odooBinFilePath;
    }

    public void setOdooBinFilePath(String myCustomField) {
        this.odooBinFilePath = myCustomField;
        // The odoo-bin path is the file to run as a script (in pycharm run config context)
        setScriptName(odooBinFilePath);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();

        if (!odooBinFilePath.endsWith("odoo-bin")) {
            throw new RuntimeConfigurationException("The odoo-bin file path must end with 'odoo-bin'");
        }
    }

    @Override
    public void readExternal(@NotNull Element element) {
        super.readExternal(element);
        // Load our custom field
        odooBinFilePath = JDOMExternalizerUtil.readField(element, ODOO_BIN_PATH_NAME);
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        super.writeExternal(element);
        // Save our custom field
        JDOMExternalizerUtil.writeField(element, ODOO_BIN_PATH_NAME, odooBinFilePath);
    }

    @NotNull
    @Override
    protected SettingsEditor<PythonRunConfiguration> createConfigurationEditor() {
        return new OdooConfigurationFragmentEditor(this);
    }
}
