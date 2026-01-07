package com.github.lseodoo.odoorunconfig.runConfig;

import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.run.PythonConfigurationFactoryBase;
import org.jetbrains.annotations.NotNull;

public class OdooConfigurationFactory extends PythonConfigurationFactoryBase {
    protected OdooConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    public @NotNull String getId() {
        return OdooRunConfigurationType.ID;
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(
            @NotNull Project project) {
        return new OdooRunConfiguration(project, this, "Odoo Demo");
    }
}
