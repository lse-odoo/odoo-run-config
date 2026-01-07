package com.github.lseodoo.odoorunconfig.runConfig;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.NotNullLazyValue;

final class OdooRunConfigurationType extends ConfigurationTypeBase {
    static final String ID = "OdooRunConfiguration";

    OdooRunConfigurationType() {
        super(
                ID,
                "Odoo",
                "Odoo run configuration type",
                NotNullLazyValue.createValue(() -> IconLoader.getIcon("/icons/odoo_o_logo.svg", OdooRunConfigurationType.class))
        );
        addFactory(new OdooConfigurationFactory(this));
    }
}
