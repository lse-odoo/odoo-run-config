package com.github.lseodoo.odoorunconfig.common

import com.github.lseodoo.odoorunconfig.runConfig.OdooRunConfiguration
import com.github.lseodoo.odoorunconfig.setting.OdooRunTemplate
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
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import javax.swing.DefaultListModel


/**
 * Abstract base class for creating a panel used to configure and manage Odoo run configurations.
 * This class defines common components and standard UI layout while allowing subclasses to implement
 * specific custom header and post-template sections via abstract methods.
 *
 * Key functionalities include:
 * - Managing fields for Odoo binary path, database name, addons paths, and additional parameters.
 * - Providing methods to reset data into the UI and apply data from the UI to various configurations.
 * - Supporting customization of the panel with additional headers and post-template elements implemented by subclasses.
 */
abstract class AbstractOdooRunPanel {

    lateinit var odooBinField: TextFieldWithBrowseButton
    lateinit var databaseField: JBTextField
    val addonsListModel = DefaultListModel<String>()
    val addonsList = JBList(addonsListModel).apply {
        emptyText.text = "No addons path specified"
        visibleRowCount = 4
    }
    val paramsEditor = RawCommandLineEditor().apply {
        CommandLinePanel.setMinimumWidth(this, 400)
        MacrosDialog.addMacroSupport(editorField, MacrosDialog.Filters.ALL) { false }
        editorField.emptyText.text = "-i crm -u account,stock ..."
        TextComponentEmptyText.setupPlaceholderVisibility(editorField)
    }

    val panel: DialogPanel = panel {
        buildHeader()

        group("Odoo Configuration") {
            row("Templates:") {
                val availableTemplates = OdooSettingService.instance.state.runTemplates
                if (availableTemplates.isNotEmpty()) {
                    val comboBox = comboBox(availableTemplates.map { it.name })

                    button("Apply") {
                        val selectedName = comboBox.component.selectedItem as? String
                        val selectedTemplate = availableTemplates.find { it.name == selectedName }

                        selectedTemplate?.runConfig?.let { tplConfig ->
                            odooBinField.text = tplConfig.odooBinFilePath ?: ""
                            databaseField.text = tplConfig.odooParametersDb ?: ""
                            addonsListModel.apply {
                                clear()
                                addAll(tplConfig.odooParametersAddonsPath as List<String>)
                            }
                            paramsEditor.text = tplConfig.odooParametersExtra ?: ""
                        }
                    }
                }
                buildPostTemplate()
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


    open fun Panel.buildHeader() {}
    open fun Row.buildPostTemplate() {}


    // --- Data Binding Methods ---

    fun resetFrom(name: String, binPath: String?, dbName: String?, addons: List<String>, extraParams: String?) {
        odooBinField.text = binPath ?: ""
        databaseField.text = dbName ?: ""
        addonsListModel.apply {
            clear()
            addAll(addons)
        }
        paramsEditor.text = extraParams ?: ""
    }

    fun resetFrom(binPath: String?, dbName: String?, addons: List<String>, extraParams: String?) {
        // Just call the main method and pass an empty string for the name
        resetFrom("", binPath, dbName, addons, extraParams)
    }

    // Save data from UI to an OdooRunTemplate (Used in Settings)
    fun applyTo(template: OdooRunTemplate) {
        template.runConfig.odooBinFilePath = odooBinField.text.ifBlank { null }
        template.runConfig.odooParametersDb = databaseField.text.ifBlank { null }
        template.runConfig.odooParametersAddonsPath = addonsListModel.elements().toList().toMutableList()
        template.runConfig.odooParametersExtra = paramsEditor.text.trim().ifBlank { null }
    }

    // Save data from UI to an active Run Configuration (Used in Fragments)
    fun applyTo(runConfig: OdooRunConfiguration) {
        runConfig.odooBinFilePath = odooBinField.text.ifBlank { null }
        runConfig.odooParametersDb = databaseField.text.ifBlank { null }
        runConfig.addonsPaths = addonsListModel.elements().toList().toMutableList()
        runConfig.odooParametersExtra = paramsEditor.text.trim().ifBlank { null }
    }

    // --- Toolbar Helper ---
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