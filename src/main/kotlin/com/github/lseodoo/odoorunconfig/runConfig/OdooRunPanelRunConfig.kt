package com.github.lseodoo.odoorunconfig.runConfig

import com.github.lseodoo.odoorunconfig.common.AbstractOdooRunPanel
import com.github.lseodoo.odoorunconfig.setting.OdooConfigurable
import com.github.lseodoo.odoorunconfig.setting.OdooRunTemplate
import com.github.lseodoo.odoorunconfig.setting.OdooSettingService
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.ui.Messages
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.Panel


class OdooRunPanelRunConfig : AbstractOdooRunPanel() {
    override fun Panel.buildHeader() {}

    override fun Panel.buildTemplateManagement() {
        val availableTemplates = OdooSettingService.instance.state.runTemplates
        row("Templates:") {
            // Only show the ComboBox and Apply button if there are templates to apply
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
            // The Shortcut Link -> Opens your Settings Page
            link(if (availableTemplates.isNotEmpty()) "Manage..." else "Create template...") {
                ShowSettingsUtil.getInstance().showSettingsDialog(null, OdooConfigurable::class.java)
            }

            // The Save Button -> Captures current UI instantly
            button("Save Current as Template...") {
                // Ask the user for a name
                val templateName = Messages.showInputDialog(
                    panel,
                    "Enter a name for the new template:",
                    "Save Template",
                    null
                )

                if (!templateName.isNullOrBlank()) {
                    // Create the new template
                    val newTemplate = OdooRunTemplate(name = templateName as String)
                    applyTo(newTemplate)
                    OdooSettingService.instance.state.runTemplates.add(newTemplate)
                }
            }
        }.bottomGap(BottomGap.SMALL)
        separator()
    }
}