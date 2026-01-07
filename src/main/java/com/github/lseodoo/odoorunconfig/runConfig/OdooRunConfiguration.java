package com.github.lseodoo.odoorunconfig.runConfig;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.run.PythonRunConfiguration;

public class OdooRunConfiguration extends PythonRunConfiguration {

//    private OdooRunConfigurationOptions odooOptions = new OdooRunConfigurationOptions();

    // The factory creates the configuration with a name.
    // We must call the parent's 2-argument constructor and then set the name.
    public OdooRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory);
        setName(name);
    }

//    @Override
//    public void writeExternal(@NotNull Element element) {
//        super.writeExternal(element);
//        Element odooOptionsElement = new Element("odoo-options");
//        XmlSerializer.serializeInto(this.odooOptions, odooOptionsElement);
//        element.addContent(odooOptionsElement);
//    }
//
//    @Override
//    public void readExternal(@NotNull Element element) {
//        super.readExternal(element);
//        Element odooOptionsElement = element.getChild("odoo-options");
//        if (odooOptionsElement != null) {
//            XmlSerializer.deserializeInto(this.odooOptions, odooOptionsElement);
//        }
//    }

//    @Override
//    protected @NotNull SettingsEditor<PythonRunConfiguration> createConfigurationEditor() {
//        SettingsEditorGroup<PythonRunConfiguration> group = new SettingsEditorGroup<>();
//        group.addEditor("Configuration", new PythonRunConfigurationEditor(this));
//        group.addEditor("Odoo", new OdooSettingsEditor());
//        return group;
//    }
}
