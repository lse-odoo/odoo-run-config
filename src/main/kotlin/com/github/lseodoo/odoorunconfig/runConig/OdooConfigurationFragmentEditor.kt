package com.github.lseodoo.odoorunconfig.runConig

import com.github.lseodoo.odoorunconfig.runConfig.OdooRunConfiguration
import com.intellij.execution.ui.CommandLinePanel
import com.intellij.execution.ui.SettingsEditorFragment
import com.intellij.execution.ui.SettingsEditorFragmentType
import com.intellij.ide.macro.MacrosDialog
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.emptyText
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.components.TextComponentEmptyText
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.jetbrains.python.run.PythonRunConfiguration
import com.jetbrains.python.run.configuration.AbstractPythonConfigurationFragmentedEditor
import javax.swing.DefaultListModel

class OdooConfigurationFragmentEditor(odooRunConfiguration: OdooRunConfiguration) :
    AbstractPythonConfigurationFragmentedEditor<PythonRunConfiguration>(odooRunConfiguration) {

    override fun customizeFragments(fragments: MutableList<SettingsEditorFragment<PythonRunConfiguration, *>>) {
        // We add ONE consolidated fragment that contains all our Odoo-specific UI
        fragments.add(createOdooSettingsFragment())
    }

    private fun createOdooSettingsFragment(): SettingsEditorFragment<PythonRunConfiguration, DialogPanel> {

        // 1. Initialize Complex Platform Widgets (These stay as standard Swing components)
        val addonsListModel = DefaultListModel<String>()
        val addonsList = JBList(addonsListModel).apply {
            emptyText.text = "No addons path specified"
            visibleRowCount = 4
        }
        val addonsDecorator = createAddonsDecorator(addonsList, addonsListModel)

        val paramsEditor = RawCommandLineEditor().apply {
            CommandLinePanel.setMinimumWidth(this, MIN_FRAGMENT_WIDTH)
            MacrosDialog.addMacroSupport(editorField, MacrosDialog.Filters.ALL) { false }
            editorField.emptyText.text = "-i crm -u account,stock ..."
            TextComponentEmptyText.setupPlaceholderVisibility(editorField)
        }

        // We declare a variable to hold our pure-DSL text field so we can read/write to it later
        lateinit var odooBinField: TextFieldWithBrowseButton

        // 2. Build the Layout
        val settingsPanel = panel {
            group("Odoo Configuration") {

                row("Path to 'odoo-bin':") {
                    // PURE KOTLIN DSL: Using the modernized, non-deprecated signature
                    textFieldWithBrowseButton(
                        FileChooserDescriptorFactory.singleFile().withTitle("Select odoo-bin File"),
                        project = null
                    ).applyToComponent {
                        emptyText.text = "/home/.../odoo/odoo-bin"
                        odooBinField = this
                    }.align(AlignX.FILL)
                }

                row("Addons paths:") {
                    cell(addonsDecorator.createPanel()).align(AlignX.FILL)
                }

                row("Arbitrary parameters:") {
                    cell(paramsEditor).align(AlignX.FILL)
                }
            }
        }

        // 3. Return the Fragment
        return SettingsEditorFragment<PythonRunConfiguration, DialogPanel>(
            "odoo.script.settings",
            "Odoo Configuration",
            "Odoo",
            settingsPanel,
            SettingsEditorFragmentType.COMMAND_LINE,
            { config, _ ->
                (config as? OdooRunConfiguration)?.let {
                    odooBinField.text = it.odooBinFilePath
                    addonsListModel.apply {
                        clear()
                        addAll(it.addonsPaths)
                    }
                    paramsEditor.text = it.odooParametersExtra
                }
            },
            { config, _ ->
                (config as? OdooRunConfiguration)?.let {
                    it.odooBinFilePath = odooBinField.text
                    it.addonsPaths = addonsListModel.elements().toList()
                    it.odooParametersExtra = paramsEditor.text.trim()
                }
            },
            { true }
        ).apply {
            setHint("Configure Odoo specific paths and parameters")
        }
    }

    private fun createAddonsDecorator(addonsList: JBList<String>, listModel: DefaultListModel<String>): ToolbarDecorator {
        return ToolbarDecorator.createDecorator(addonsList)
            .setAddAction {
                val descriptor = FileChooserDescriptorFactory.createMultipleFoldersDescriptor().apply {
                    title = "Select Odoo Addons Folders"
                }
                FileChooser.chooseFiles(descriptor, null, null) { files ->
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
                val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor().apply {
                    title = "Modify Addons Path"
                }
                val oldVirtualFile = LocalFileSystem.getInstance().findFileByPath(oldPath)
                val fileToSelect = oldVirtualFile?.takeIf { it.isDirectory }

                FileChooser.chooseFile(descriptor, null, fileToSelect) { chosenFile ->
                    listModel.setElementAt(chosenFile.path, selectedIndex)
                }
            }
    }
}