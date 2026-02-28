package com.github.lseodoo.odoorunconfig.runConfig

import com.intellij.execution.ui.SettingsEditorFragment
import com.intellij.execution.ui.SettingsEditorFragmentType
import com.intellij.openapi.ui.DialogPanel
import com.jetbrains.python.run.PythonRunConfiguration
import com.jetbrains.python.run.configuration.AbstractPythonConfigurationFragmentedEditor

class OdooConfigurationFragmentEditor(odooRunConfiguration: OdooRunConfiguration) :
    AbstractPythonConfigurationFragmentedEditor<PythonRunConfiguration>(odooRunConfiguration) {

    override fun customizeFragments(fragments: MutableList<SettingsEditorFragment<PythonRunConfiguration, *>>) {
        // We add ONE consolidated fragment that contains all our Odoo-specific UI
        fragments.add(createOdooSettingsFragment())
    }

    private fun createOdooSettingsFragment(): SettingsEditorFragment<PythonRunConfiguration, DialogPanel> {
        val commonUi = OdooRunPanelRunConfig()

        return SettingsEditorFragment<PythonRunConfiguration, DialogPanel>(
            "odoo.script.settings",
            "Odoo configuration",
            "Odoo",
            commonUi.panel, // Pass the shared panel
            SettingsEditorFragmentType.COMMAND_LINE,
            { config, _ ->
                (config as? OdooRunConfiguration)?.let {
                    // Populate UI from Run Configuration
                    commonUi.resetFrom(it.myOdooRunConfig)
                }
            },
            { config, _ ->
                (config as? OdooRunConfiguration)?.let {
                    // Save UI data back into Run Configuration
                    commonUi.applyTo(it.myOdooRunConfig)
                }
            },
            { true }
        ).apply {
            setHint("Configure Odoo specific paths and parameters")
        }
    }
}