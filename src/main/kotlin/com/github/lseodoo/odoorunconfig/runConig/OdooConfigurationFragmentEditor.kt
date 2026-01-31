package com.github.lseodoo.odoorunconfig.runConig

import com.github.lseodoo.odoorunconfig.runConfig.OdooRunConfiguration
import com.intellij.execution.ui.CommandLinePanel
import com.intellij.execution.ui.SettingsEditorFragment
import com.intellij.ide.macro.MacrosDialog
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.components.TextComponentEmptyText
import com.jetbrains.python.PyBundle
import com.jetbrains.python.run.PythonRunConfiguration
import com.jetbrains.python.run.configuration.AbstractPythonConfigurationFragmentedEditor

// Note: we can't override from PythonConfigurationFragmentedEditor as "This type is final, so it cannot be extended."
class OdooConfigurationFragmentEditor(odooRunConfiguration: OdooRunConfiguration)
    : AbstractPythonConfigurationFragmentedEditor<PythonRunConfiguration>(odooRunConfiguration) {

    override fun customizeFragments(fragments: MutableList<SettingsEditorFragment<PythonRunConfiguration, *>>) {
        // super.customizeFragments(fragments) // Can't call as parent is abstract
        addToFragmentsBeforeEditors(fragments, createOdooArbitraryParametersFragment())
        addToFragmentsBeforeEditors(fragments, createOdooBinCustomFragment())
    }

    private fun createOdooArbitraryParametersFragment(): SettingsEditorFragment<PythonRunConfiguration, *> {
        // Heavily inspired by intellij code, look for: `PythonConfigurationFragmentedEditor` `py.script.parameters`
        val parametersEditor = RawCommandLineEditor()
        CommandLinePanel.setMinimumWidth(parametersEditor, MIN_FRAGMENT_WIDTH)
        val scriptParametersFragment: SettingsEditorFragment<PythonRunConfiguration, RawCommandLineEditor> = SettingsEditorFragment<PythonRunConfiguration, RawCommandLineEditor>(
            "odoo.script.parameters.arbitrary",
            "Odoo arbitrary parameters",
            "Odoo",
            parametersEditor,
            { config: PythonRunConfiguration, field: RawCommandLineEditor -> (config as? OdooRunConfiguration)?.let { field.text = it.odooParameters } },
            { config: PythonRunConfiguration , field: RawCommandLineEditor -> (config as? OdooRunConfiguration)?.let { it.odooParameters = field.text.trim() } },
            { true })
        MacrosDialog.addMacroSupport(parametersEditor.editorField, MacrosDialog.Filters.ALL) { false }
        parametersEditor.editorField.emptyText.setText(PyBundle.message("python.run.configuration.fragments.script.parameters.hint"))
        TextComponentEmptyText.setupPlaceholderVisibility(parametersEditor.editorField)
        scriptParametersFragment.setHint(PyBundle.message("python.run.configuration.fragments.script.parameters.hint"))
        scriptParametersFragment.actionHint = PyBundle.message("python.run.configuration.fragments.script.parameters.hint")
        return scriptParametersFragment
    }

    private fun createOdooBinCustomFragment(): SettingsEditorFragment<PythonRunConfiguration, LabeledComponent<TextFieldWithBrowseButton>> {
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
