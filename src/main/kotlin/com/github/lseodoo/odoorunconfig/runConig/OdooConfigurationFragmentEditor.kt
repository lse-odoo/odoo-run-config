package com.github.lseodoo.odoorunconfig.runConig

import com.github.lseodoo.odoorunconfig.runConfig.OdooRunConfiguration
import com.intellij.execution.ui.CommandLinePanel
import com.intellij.execution.ui.SettingsEditorFragment
import com.intellij.execution.ui.SettingsEditorFragmentType
import com.intellij.ide.macro.MacrosDialog
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.emptyText
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.components.TextComponentEmptyText
import com.jetbrains.python.run.PythonRunConfiguration
import com.jetbrains.python.run.configuration.AbstractPythonConfigurationFragmentedEditor
import java.awt.BorderLayout
import javax.swing.DefaultListModel

// Note: we can't override from PythonConfigurationFragmentedEditor as "This type is final, so it cannot be extended."
class OdooConfigurationFragmentEditor(odooRunConfiguration: OdooRunConfiguration)
    : AbstractPythonConfigurationFragmentedEditor<PythonRunConfiguration>(odooRunConfiguration) {

    override fun customizeFragments(fragments: MutableList<SettingsEditorFragment<PythonRunConfiguration, *>>) {
        // super.customizeFragments(fragments) // Can't call as parent is abstract
        fragments.add(createOdooBinCustomFragment())
        fragments.add(createOdooParametersFragmentAddonsPath())
        fragments.add(createOdooArbitraryParametersFragment())

        // TODO: considering the use of "SettingsEditorGroup" instead of fragment. It can use swing stuff and would be easier to layout and maintain in the time
    }

    private fun createOdooBinCustomFragment(): SettingsEditorFragment<PythonRunConfiguration, LabeledComponent<TextFieldWithBrowseButton>> {
        val odoobinPathWidget = TextFieldWithBrowseButton()
        odoobinPathWidget.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.singleFile()))
        odoobinPathWidget.emptyText.text = "/home/.../odoo/odoo-bin"
        val odoobinLabel = LabeledComponent.create(odoobinPathWidget, "Path to \"odoo-bin\" file")
        odoobinLabel.labelLocation = BorderLayout.WEST

        val odoobinPathFragment: SettingsEditorFragment<PythonRunConfiguration, LabeledComponent<TextFieldWithBrowseButton>> = SettingsEditorFragment(
            "odoo.script.odoo-bin.file.path",
            "Path to \"odoo-bin\" file",
            "Odoo",
            odoobinLabel,
            SettingsEditorFragmentType.COMMAND_LINE,
            { config, _ -> (config as? OdooRunConfiguration)?.let { odoobinPathWidget.text = it.odooBinFilePath } },
            { config, _ -> (config as? OdooRunConfiguration)?.let { it.odooBinFilePath = odoobinPathWidget.text } },
            { true }
        )
        odoobinPathFragment.setHint("Path to \"odoo-bin\" file")
        return odoobinPathFragment
    }

    private fun createOdooParametersFragmentAddonsPath(): SettingsEditorFragment<PythonRunConfiguration, *> {
        val listModel = DefaultListModel<String>()
        val addonsList = JBList(listModel)
        addonsList.emptyText.text = "No addons path specified"

        addonsList.visibleRowCount = 4
        val decorator = ToolbarDecorator.createDecorator(addonsList)
            .setAddAction {
                val descriptor = FileChooserDescriptorFactory.createMultipleFoldersDescriptor()
                descriptor.title = "Select Odoo Addons Folders"

                com.intellij.openapi.fileChooser.FileChooser.chooseFiles(descriptor, null, null) { files ->
                    files.forEach { listModel.addElement(it.path) }
                }
            }
            .setRemoveAction {
                addonsList.selectedValuesList.forEach { listModel.removeElement(it) }
            }
            .setEditAction {
                val selectedIndex = addonsList.selectedIndex
                if (selectedIndex < 0) return@setEditAction

                val oldPath = addonsList.selectedValue

                // 1. Create a descriptor for selecting a SINGLE folder.
                val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                descriptor.title = "Modify Addons Path"

                // 2. Find the VirtualFile for the old path to use as a starting point.
                val oldVirtualFile = com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByPath(oldPath)

                // 3. The file to select in the chooser must be a directory.
                val fileToSelect = if (oldVirtualFile != null && oldVirtualFile.isDirectory) oldVirtualFile else null

                // 4. Show the file chooser for a single file.
                com.intellij.openapi.fileChooser.FileChooser.chooseFile(descriptor, null, fileToSelect) { chosenFile ->
                    // This lambda is executed when the user selects a new folder.
                    // Update the model with the new path at the same index.
                    listModel.setElementAt(chosenFile.path, selectedIndex)
                }
            }

        val component = decorator.createPanel()

        // The fragment should be typed with <PythonRunConfiguration, JComponent>
        // to match the component and the lambdas below.
        val odooAddonsPathFragment: SettingsEditorFragment<PythonRunConfiguration, *> = SettingsEditorFragment(
            "odoo.script.parameters.addons-path",
            "Odoo addons path",
            "Odoo",
            component,
            SettingsEditorFragmentType.COMMAND_LINE,
            // resetEditorFrom lambda: (config, component) -> Unit
            { config, _ ->
                // Safely cast to OdooRunConfiguration to access your specific fields
                (config as? OdooRunConfiguration)?.let { odooConfig ->
                    listModel.clear()
                    odooConfig.addonsPaths.forEach { listModel.addElement(it) }
                }
            },

            // applyEditorTo lambda: (config, component) -> Unit
            { config, _ ->
                (config as? OdooRunConfiguration)?.let { odooConfig ->
                    val paths = mutableListOf<String>()
                    for (i in 0 until listModel.size()) {
                        paths.add(listModel.getElementAt(i))
                    }
                    odooConfig.addonsPaths = paths
                }
            },
            { true }
        )
        odooAddonsPathFragment.setHint("List of folders that will be used as odoo addons path")
        return odooAddonsPathFragment
    }

    private fun createOdooArbitraryParametersFragment(): SettingsEditorFragment<PythonRunConfiguration, *> {
        // Heavily inspired by intellij code, look for: `PythonConfigurationFragmentedEditor` `py.script.parameters`
        val parametersEditor = RawCommandLineEditor()
        CommandLinePanel.setMinimumWidth(parametersEditor, MIN_FRAGMENT_WIDTH)
        val odoobinLabel = LabeledComponent.create(parametersEditor, "Odoo arbitrary parameters")
        odoobinLabel.labelLocation = BorderLayout.WEST

        val scriptParametersFragment: SettingsEditorFragment<PythonRunConfiguration, LabeledComponent<RawCommandLineEditor>> = SettingsEditorFragment(
            "odoo.script.parameters.arbitrary",
            "Odoo arbitrary parameters",
            "Odoo",
            odoobinLabel,
            SettingsEditorFragmentType.COMMAND_LINE,
            { config: PythonRunConfiguration, _ -> (config as? OdooRunConfiguration)?.let { parametersEditor.text = it.odooParameters } },
            { config: PythonRunConfiguration , _ -> (config as? OdooRunConfiguration)?.let { it.odooParameters = parametersEditor.text.trim() } },
            { true })
        MacrosDialog.addMacroSupport(parametersEditor.editorField, MacrosDialog.Filters.ALL) { false }
        parametersEditor.editorField.emptyText.text = "-i crm -u account,stock ..."
        TextComponentEmptyText.setupPlaceholderVisibility(parametersEditor.editorField)
        scriptParametersFragment.setHint("Add any arbitrary Odoo CLI parameters")
        return scriptParametersFragment
    }
}
