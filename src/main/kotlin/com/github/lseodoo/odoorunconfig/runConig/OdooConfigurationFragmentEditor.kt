package com.github.lseodoo.odoorunconfig.runConig

import com.github.lseodoo.odoorunconfig.runConfig.OdooRunConfiguration
import com.intellij.execution.ui.SettingsEditorFragment
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.ui.components.JBTextField
import com.jetbrains.python.run.PythonRunConfiguration
import com.jetbrains.python.run.configuration.AbstractPythonConfigurationFragmentedEditor

// Note: we can't override from PythonConfigurationFragmentedEditor as "This type is final, so it cannot be extended."
class OdooConfigurationFragmentEditor(odooRunConfiguration: OdooRunConfiguration)
    : AbstractPythonConfigurationFragmentedEditor<PythonRunConfiguration>(odooRunConfiguration) {

    override fun customizeFragments(fragments: MutableList<SettingsEditorFragment<PythonRunConfiguration, *>>) {
//        super.customizeFragments(fragments) // Can't call as parent is abstrat
        addToFragmentsBeforeEditors(fragments, createOdooCustomFieldFragment())
    }

    private fun createOdooCustomFieldFragment(): SettingsEditorFragment<PythonRunConfiguration, LabeledComponent<JBTextField>> {
        val textField = JBTextField()
        val component = LabeledComponent.create(textField, "Odoo Custom Field:")

        return SettingsEditorFragment(
            "odoo.my.custom.field",
            "Odoo Settings",
            "Odoo",
            component,
            { config, _ -> (config as? OdooRunConfiguration)?.let { textField.text = it.myCustomField } },
            { config, _ -> (config as? OdooRunConfiguration)?.let { it.myCustomField = textField.text } },
            { true }
        )
    }
}
