package com.github.lseodoo.odoorunconfig.common

import com.github.lseodoo.odoorunconfig.runConfig.OdooRunConfiguration
import com.github.lseodoo.odoorunconfig.setting.OdooSettingService
import com.intellij.execution.ui.CommandLinePanel
import com.intellij.ide.macro.MacrosDialog
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.emptyText
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.TextComponentEmptyText
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.panel
import javax.swing.DefaultListModel

class OdooRunConfigUI {
    val addonsListModel = DefaultListModel<String>()
    val addonsList = JBList(addonsListModel).apply {
        emptyText.text = "No addons path specified"
        visibleRowCount = 4
    }

    val paramsEditor = RawCommandLineEditor().apply {
        // Adjust MIN_FRAGMENT_WIDTH or remove this line if it's specific to the fragment
        CommandLinePanel.setMinimumWidth(this, 400)
        MacrosDialog.addMacroSupport(editorField, MacrosDialog.Filters.ALL) { false }
        editorField.emptyText.text = "-i crm -u account,stock ..."
        TextComponentEmptyText.setupPlaceholderVisibility(editorField)
    }

    lateinit var odooBinField: TextFieldWithBrowseButton
    lateinit var databaseField: JBTextField

    // 1. The Shared UI Panel
    val panel: DialogPanel = panel {
        group("Odoo Configuration") {
            row("Copy values from:") {
                val templates = OdooSettingService.instance.getState().runTemplates
                val odooRunTemplateComboBox = comboBox(templates.map { it.name })

                button("Apply") {
                    val selectedName = odooRunTemplateComboBox.component.selectedItem as? String
                    val selectedTemplate = templates.find { it.name == selectedName }

                    selectedTemplate?.runConfig?.let { tplConfig ->
                        // Reuse our existing method to overwrite the UI fields instantly!
                        resetFrom(
                            tplConfig.odooBinFilePath,
                            tplConfig.odooParametersDb,
                            tplConfig.odooParametersAddonsPath as List<String>,
                            tplConfig.odooParametersExtra
                        )
                    }
                }
            }.bottomGap(BottomGap.SMALL)
            separator()

            row("Path to 'odoo-bin':") {
                textFieldWithBrowseButton(
                    FileChooserDescriptorFactory.singleFile().withTitle("Select odoo-bin File"),
                    project = null
                ).applyToComponent {
                    emptyText.text = "/home/.../odoo/odoo-bin"
                    odooBinField = this
                }.align(AlignX.FILL)
            }

            row("Database name:") {
                textField().applyToComponent {
                    emptyText.text = "e.g., my_odoo_db"
                    databaseField = this
                }.align(AlignX.FILL)
                    .comment("The database to connect to (appends the -d flag). Leave empty to use the default.")
            }

            row("Addons paths:") {
                cell(createAddonsDecorator().createPanel()).align(AlignX.FILL)
            }

            row("Arbitrary parameters:") {
                cell(paramsEditor).align(AlignX.FILL)
            }
        }
    }

    // 2. Populate the UI from existing data
    fun resetFrom(binPath: String?, dbName: String?, addons: List<String>, extraParams: String?) {
        odooBinField.text = binPath ?: ""
        databaseField.text = dbName ?: ""
        addonsListModel.apply {
            clear()
            addAll(addons)
        }
        paramsEditor.text = extraParams ?: ""
    }

    // 3A. Write UI data to the Settings Template
    fun applyTo(config: OdooRunConfig) {
        config.odooBinFilePath = odooBinField.text.ifBlank { null }
        config.odooParametersDb = databaseField.text.ifBlank { null }
        config.odooParametersAddonsPath = addonsListModel.elements().toList().toMutableList()
        config.odooParametersExtra = paramsEditor.text.trim().ifBlank { null }
    }

    // 3B. Write UI data to the Run Configuration
    fun applyTo(runConfig: OdooRunConfiguration) { // Assuming this is your actual RunConfig class
        applyTo(runConfig.myOdooRunConfig)
    }

    // Extracted decorator logic
    private fun createAddonsDecorator(): ToolbarDecorator {
        return ToolbarDecorator.createDecorator(addonsList)
            .setAddAction {
                val descriptor = FileChooserDescriptorFactory.createMultipleFoldersDescriptor().apply {
                    title = "Select Odoo Addons Folders"
                }
                FileChooser.chooseFiles(descriptor, null, null) { files ->
                    files.forEach { addonsListModel.addElement(it.path) }
                }
            }
            .setRemoveAction {
                addonsList.selectedValuesList.forEach { addonsListModel.removeElement(it) }
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
                    addonsListModel.setElementAt(chosenFile.path, selectedIndex)
                }
            }
    }
}