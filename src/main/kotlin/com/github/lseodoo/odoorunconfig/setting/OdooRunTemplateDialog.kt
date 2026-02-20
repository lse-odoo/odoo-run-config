package com.github.lseodoo.odoorunconfig.setting

import com.github.lseodoo.odoorunconfig.common.OdooRunConfig
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class OdooRunTemplateDialog(originalConfig: OdooRunConfig) : DialogWrapper(true) {

    // Create a copy of the config so we don't mutate the original until "OK" is clicked
    val workingCopy = originalConfig.copy()

    init {
        title = "Edit Odoo Configuration"
        init() // This is required to initialize the DialogWrapper
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row("Odoo-bin path:") {
                textFieldWithBrowseButton(
                    FileChooserDescriptorFactory.singleFile().withTitle("Select odoo-bin File"),
                    project = null
                )
                    .bindText(
                        // Handle the nullable String
                        { workingCopy.odooBinFilePath ?: "" },
                        { workingCopy.odooBinFilePath = it.ifBlank { null } }
                    )
            }
        }
    }
}
