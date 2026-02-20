package com.github.lseodoo.odoorunconfig.setting

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
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

    override fun createPanel(): DialogPanel {
        return panel {
            group("Configuration Templates") {

                row {
                    val decorator = ToolbarDecorator.createDecorator(odooRunTemplateTable)
                        .setAddAction {
                            odooRunTemplateTableModel.addRow(
                                OdooRunTemplate()
                            )
                        }
                        .setRemoveAction {
                            odooRunTemplateTable.selectedRows.sortedDescending().forEach { index ->
                                odooRunTemplateTableModel.removeRow(index)
                            }
                        }
                        .setEditAction {
                            // 1. Get the currently selected row
                            val selectedRow = odooRunTemplateTable.selectedRow
                            if (selectedRow >= 0) {
                                val selectedTemplate = odooRunTemplateTableModel.getItem(selectedRow)

                                // 2. Open the pop-up dialog, passing the nested runConfig
                                val dialog = OdooRunTemplateDialog(selectedTemplate.runConfig)

                                // 3. showAndGet() returns true if the user clicked "OK"
                                if (dialog.showAndGet()) {
                                    // Update the original object with the modified copy
                                    selectedTemplate.runConfig = dialog.workingCopy

                                    // Tell the table model that the row was updated
                                    odooRunTemplateTableModel.fireTableRowsUpdated(selectedRow, selectedRow)
                                }
                            }
                        }

                    cell(decorator.createPanel())
                        .align(Align.FILL)
                        .onIsModified { odooRunTemplateTableModel.items != settings.runTemplates }
                        .onApply { settings.runTemplates = odooRunTemplateTableModel.items.map { it.copy() }.toMutableList() }
                        .onReset { odooRunTemplateTableModel.items = settings.runTemplates.map { it.copy() }.toMutableList() }
                }
            }
        }
    }
}