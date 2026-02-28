package com.github.lseodoo.odoorunconfig.setting

import com.github.lseodoo.odoorunconfig.common.AbstractOdooRunPanel
import com.github.lseodoo.odoorunconfig.common.OdooRunConfig
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.Panel

class OdooRunPanelSetting: AbstractOdooRunPanel() {
    lateinit var nameField: JBTextField

    override fun Panel.buildHeader() {
        row("Template name:") {
            textField().applyToComponent {
                emptyText.text = "e.g., Odoo 18 Production"
                nameField = this
            }.align(AlignX.FILL)
        }
    }

    fun resetFrom(odooRunTemplate: OdooRunTemplate) {
        nameField.text = odooRunTemplate.name
        return super.resetFrom(odooRunTemplate.runConfig)
    }

    fun applyTo(odooRunTemplate: OdooRunTemplate) {
        odooRunTemplate.name = nameField.text.ifBlank { "Unnamed Odoo Template" }
        return super.applyTo(odooRunTemplate.runConfig)
    }
}