package com.github.lseodoo.odoorunconfig.setting

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.JBSplitter
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import javax.swing.DefaultListModel

class OdooConfigurable : BoundConfigurable("Odoo Settings") {

    private val settings get() = OdooSettingService.instance.state

    // The Master List Model
    private val listModel = DefaultListModel<OdooRunTemplate>()
    private val templateList = JBList(listModel).apply {
        // Render only the 'name' property in the list
        setCellRenderer { list, value, index, isSelected, cellHasFocus ->
            javax.swing.DefaultListCellRenderer().getListCellRendererComponent(
                list, value.name, index, isSelected, cellHasFocus
            )
        }
    }

    private val odooTemplateSetting = OdooRunPanelSetting()

    // Track the previously selected index to save its state before switching
    private var lastSelectedIndex = -1

    init {
        // Listen for selection changes in the list
        templateList.addListSelectionListener { e ->
            if (e.valueIsAdjusting) return@addListSelectionListener

            // Save current UI data to the PREVIOUSLY selected template
            if (lastSelectedIndex in 0 until listModel.size) {
                val previousTemplate = listModel.get(lastSelectedIndex)
                odooTemplateSetting.applyTo(previousTemplate)

                // Refresh the list visually in case the user edited the template's name
                listModel.setElementAt(previousTemplate, lastSelectedIndex)
            }

            // Load the NEWLY selected template's data into the UI
            val currentIndex = templateList.selectedIndex
            if (currentIndex in 0 until listModel.size) {
                val currentTemplate = listModel.get(currentIndex)
                odooTemplateSetting.resetFrom(currentTemplate)
                // Enable the right panel so the user can type
                odooTemplateSetting.panel.apply { isEnabled = true; isVisible = true }
            } else {
                // Nothing selected, disable/hide the right panel
                odooTemplateSetting.panel.apply { isEnabled = false; isVisible = false }
            }

            lastSelectedIndex = currentIndex
        }
    }

    override fun createPanel(): DialogPanel {
        return panel {
            group("Configuration Templates") {
                row {
                    // Create the left pane (Master List with Add/Remove buttons)
                    val decoratorPanel = ToolbarDecorator.createDecorator(templateList)
                        .setAddAction {
                            listModel.addElement(OdooRunTemplate(name = "New Template"))
                            // Automatically select the newly added item
                            templateList.selectedIndex = listModel.size - 1
                        }
                        .setRemoveAction {
                            templateList.selectedIndices.sortedDescending().forEach { index ->
                                listModel.remove(index)
                            }
                            if (listModel.isEmpty) {
                                lastSelectedIndex = -1
                                odooTemplateSetting.panel.apply { isEnabled = false; isVisible = false }
                            }
                        }
                        .createPanel()

                    // Create the Splitter (35% width for the list, 65% for the form)
                    val splitter = JBSplitter(false, 0.35f).apply {
                        firstComponent = decoratorPanel
                        secondComponent = odooTemplateSetting.panel
                    }

                    // Embed the Splitter into the DSL panel
                    cell(splitter)
                        .align(Align.FILL)
                        .onIsModified {
                            // Force save the active row before comparing
                            if (lastSelectedIndex >= 0) {
                                odooTemplateSetting.applyTo(listModel.get(lastSelectedIndex))
                            }
                            listModel.elements().toList() != settings.runTemplates
                        }
                        .onApply {
                            // Save the active row before applying to state
                            if (lastSelectedIndex >= 0) {
                                odooTemplateSetting.applyTo(listModel.get(lastSelectedIndex))
                            }
                            // Deep copy the list so we don't store UI list references in the state
                            settings.runTemplates = listModel.elements().toList().map {
                                it.copy(runConfig = it.runConfig.copy())
                            }.toMutableList()
                        }
                        .onReset {
                            // Deep copy state to the list
                            listModel.clear()
                            listModel.addAll(settings.runTemplates.map {
                                it.copy(runConfig = it.runConfig.copy())
                            })

                            // Select the first item by default if the list isn't empty
                            if (!listModel.isEmpty) {
                                templateList.selectedIndex = 0
                            } else {
                                odooTemplateSetting.panel.apply { isEnabled = false; isVisible = false }
                            }
                        }
                }
            }
        }
    }
}