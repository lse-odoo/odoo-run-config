package com.github.lseodoo.odoorunconfig.runConfig;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OdooConfigurationFactory extends ConfigurationFactory {
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
        return new OdooRunConfiguration(project, this, "Demo");
    }

    @Nullable
    @Override
    public Class<? extends BaseState> getOptionsClass() {
        return OdooRunConfigurationOptions.class;
    }

}
