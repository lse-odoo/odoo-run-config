package com.github.lseodoo.odoorunconfig.runConfig

import com.github.lseodoo.odoorunconfig.common.AbstractOdooRunPanel
import com.github.lseodoo.odoorunconfig.setting.OdooConfigurable
import com.github.lseodoo.odoorunconfig.setting.OdooRunTemplate
import com.github.lseodoo.odoorunconfig.setting.OdooSettingService
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.ui.Messages
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row


class OdooRunPanelRunConfig : AbstractOdooRunPanel() {
    override fun Row.buildPostTemplate() {
        // The Shortcut Link -> Opens your Settings Page
        link("Configure...") {
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
                val newTemplate = OdooRunTemplate(name = templateName)
                applyTo(newTemplate.runConfig)
                OdooSettingService.instance.state.runTemplates.add(newTemplate)
            }
        }
    }
}