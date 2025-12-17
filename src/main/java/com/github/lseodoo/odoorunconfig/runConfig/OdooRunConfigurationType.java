package com.github.lseodoo.odoorunconfig.runConfig;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.NotNullLazyValue;

final class OdooRunConfigurationType extends ConfigurationTypeBase {
    static final String ID = "OdooRunConfiguration";

    OdooRunConfigurationType() {
        super(ID, "Odoo", "Odoo run configuration type",
                NotNullLazyValue.createValue(() -> AllIcons.Nodes.Console));
        addFactory(new OdooConfigurationFactory(this));
    }
}
