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
    public static final String ODOO_PARAMETERS_NAME = "ODOO_PARAMETERS";

    private String odooBinFilePath;
    private String odooArbitraryParameters;

    public OdooRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory);
        setName(name);

        // Default option (difference from python configuration)
        setAddContentRoots(false);
        setAddSourceRoots(false);
        setEmulateTerminal(true);
    }

    public String getOdooBinFilePath() {
        return odooBinFilePath;
    }

    public void setOdooBinFilePath(String path) {
        this.odooBinFilePath = path;
        // The odoo-bin path is the file to run as a script
        setScriptName(odooBinFilePath);
    }

    public String getOdooArbitraryParameters() {
        return odooArbitraryParameters;
    }

    public void setOdooArbitraryParameters(String params) {
        this.odooArbitraryParameters = params;
        // Arbitrary parameters is equivalent to the script parameters
        setScriptParameters(params);
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
        odooBinFilePath = JDOMExternalizerUtil.readField(element, ODOO_BIN_PATH_NAME);
        odooArbitraryParameters = JDOMExternalizerUtil.readField(element, ODOO_PARAMETERS_NAME);
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, ODOO_BIN_PATH_NAME, odooBinFilePath);
        JDOMExternalizerUtil.writeField(element, ODOO_PARAMETERS_NAME, odooArbitraryParameters);
    }

    @NotNull
    @Override
    protected SettingsEditor<PythonRunConfiguration> createConfigurationEditor() {
        return new OdooConfigurationFragmentEditor(this);
    }
}
