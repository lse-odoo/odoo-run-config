package com.github.lseodoo.odoorunconfig.setting

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel

class OdooConfigurable : BoundConfigurable("Odoo Settings") {
    private val settings get() = OdooSettingService.instance.state

    override fun createPanel(): DialogPanel {
        return panel {
            row("Odoo Bin Path:") {
                textFieldWithBrowseButton(
                    FileChooserDescriptorFactory.singleFile().withTitle("Select odoo-bin File"),
                    project = null
                ).bindText({ settings.odooBinFilePath.toString() }, {settings.odooBinFilePath = it})
            }
        }
    }
}