package com.github.lseodoo.odoorunconfig.runConig

import com.github.lseodoo.odoorunconfig.runConfig.OdooRunConfiguration
import com.intellij.execution.ui.SettingsEditorFragment
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.jetbrains.python.run.PythonRunConfiguration
import com.jetbrains.python.run.configuration.AbstractPythonConfigurationFragmentedEditor

// Note: we can't override from PythonConfigurationFragmentedEditor as "This type is final, so it cannot be extended."
class OdooConfigurationFragmentEditor(odooRunConfiguration: OdooRunConfiguration)
    : AbstractPythonConfigurationFragmentedEditor<PythonRunConfiguration>(odooRunConfiguration) {

    override fun customizeFragments(fragments: MutableList<SettingsEditorFragment<PythonRunConfiguration, *>>) {
        // super.customizeFragments(fragments) // Can't call as parent is abstract
        addToFragmentsBeforeEditors(fragments, createOdooCustomFieldFragment())
    }

    private fun createOdooCustomFieldFragment(): SettingsEditorFragment<PythonRunConfiguration, LabeledComponent<TextFieldWithBrowseButton>> {
        val odoobinPathWidget = TextFieldWithBrowseButton()
        odoobinPathWidget.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.singleFile()))
        val component = LabeledComponent.create(odoobinPathWidget, "Path for `odoo-bin` file")

        return SettingsEditorFragment(
            "odoo.script.odoo-bin.file.path",
            "Path for `odoo-bin` file",
            "Odoo",
            component,
            { config, _ -> (config as? OdooRunConfiguration)?.let { odoobinPathWidget.text = it.odooBinFilePath } },
            { config, _ -> (config as? OdooRunConfiguration)?.let { it.odooBinFilePath = odoobinPathWidget.text } },
            { true }
        )
    }
}
