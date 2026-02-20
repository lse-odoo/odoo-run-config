package com.github.lseodoo.odoorunconfig.setting

import com.github.lseodoo.odoorunconfig.common.OdooRunConfigUI
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.JBSplitter
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.ListTableModel

class OdooConfigurable : BoundConfigurable("Odoo Settings") {

    private val settings get() = OdooSettingService.instance.state

    private val odooRunTemplateTableModel = ListTableModel<OdooRunTemplate>(
        arrayOf(NameColumn(), VersionColumn()),
        mutableListOf()
    )
    private val odooRunTemplateTable = JBTable(odooRunTemplateTableModel)

    // 1. Instantiate the shared UI for the Detail view
    private val commonUi = OdooRunConfigUI()

    // Track the previously selected row to save its state before switching
    private var lastSelectedRow = -1

    init {
        // 2. Listen for selection changes in the table
        odooRunTemplateTable.selectionModel.addListSelectionListener { e ->
            if (e.valueIsAdjusting) return@addListSelectionListener

            // Save current UI data to the PREVIOUSLY selected template
            if (lastSelectedRow in 0 until odooRunTemplateTableModel.rowCount) {
                val previousTemplate = odooRunTemplateTableModel.getItem(lastSelectedRow)
                commonUi.applyTo(previousTemplate.runConfig)
            }

            // Load the NEWLY selected template's data into the UI
            val currentRow = odooRunTemplateTable.selectedRow
            if (currentRow in 0 until odooRunTemplateTableModel.rowCount) {
                val currentTemplate = odooRunTemplateTableModel.getItem(currentRow)
                commonUi.resetFrom(
                    currentTemplate.runConfig.odooBinFilePath,
                    currentTemplate.runConfig.odooParametersDb,
                    currentTemplate.runConfig.odooParametersAddonsPath as List<String>,
                    currentTemplate.runConfig.odooParametersExtra,
                )
                // Enable the right panel so the user can type
                commonUi.panel.apply { isEnabled = true; isVisible = true }
            } else {
                // Nothing selected, disable/hide the right panel
                commonUi.panel.apply { isEnabled = false; isVisible = false }
            }

            lastSelectedRow = currentRow
        }
    }

    override fun createPanel(): DialogPanel {
        return panel {
            group("Configuration Templates") {
                row {
                    // 3. Create the left pane (Master Table with Add/Remove buttons)
                    // Note: setEditAction is gone because the Detail view replaces it!
                    val decoratorPanel = ToolbarDecorator.createDecorator(odooRunTemplateTable)
                        .setAddAction {
                            odooRunTemplateTableModel.addRow(OdooRunTemplate())
                            // Automatically select the newly added row
                            val newRowIndex = odooRunTemplateTableModel.rowCount - 1
                            odooRunTemplateTable.setRowSelectionInterval(newRowIndex, newRowIndex)
                        }
                        .setRemoveAction {
                            odooRunTemplateTable.selectedRows.sortedDescending().forEach { index ->
                                odooRunTemplateTableModel.removeRow(index)
                            }
                            if (odooRunTemplateTableModel.rowCount == 0) {
                                lastSelectedRow = -1
                                commonUi.panel.apply { isEnabled = false; isVisible = false }
                            }
                        }
                        .createPanel()

                    // 4. Create the Splitter (35% width for the table, 65% for the form)
                    val splitter = JBSplitter(false, 0.35f).apply {
                        firstComponent = decoratorPanel
                        secondComponent = commonUi.panel
                    }

                    // 5. Embed the Splitter into the DSL panel
                    cell(splitter)
                        .align(Align.FILL)
                        .onIsModified {
                            // Force save the active row before comparing
                            if (lastSelectedRow >= 0) {
                                commonUi.applyTo(odooRunTemplateTableModel.getItem(lastSelectedRow).runConfig)
                            }
                            odooRunTemplateTableModel.items != settings.runTemplates
                        }
                        .onApply {
                            // Save the active row before applying to state
                            if (lastSelectedRow >= 0) {
                                commonUi.applyTo(odooRunTemplateTableModel.getItem(lastSelectedRow).runConfig)
                            }
                            // Deep copy the list so we don't store table references in the state
                            settings.runTemplates = odooRunTemplateTableModel.items.map {
                                it.copy(runConfig = it.runConfig.copy())
                            }.toMutableList()
                        }
                        .onReset {
                            // Deep copy state to the table
                            odooRunTemplateTableModel.items = settings.runTemplates.map {
                                it.copy(runConfig = it.runConfig.copy())
                            }.toMutableList()

                            // Select the first item by default if the list isn't empty
                            if (odooRunTemplateTableModel.rowCount > 0) {
                                odooRunTemplateTable.setRowSelectionInterval(0, 0)
                            } else {
                                commonUi.panel.apply { isEnabled = false; isVisible = false }
                            }
                        }
                }
            }
        }
    }
}