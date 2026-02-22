package com.github.lseodoo.odoorunconfig.setting

import com.github.lseodoo.odoorunconfig.common.AbstractOdooRunPanel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row

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

    override fun Row.buildPostTemplate() {}
}