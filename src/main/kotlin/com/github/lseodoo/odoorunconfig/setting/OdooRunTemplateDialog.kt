package com.github.lseodoo.odoorunconfig.setting

import com.github.lseodoo.odoorunconfig.common.OdooRunConfig
import com.github.lseodoo.odoorunconfig.common.OdooRunConfigUI
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent

class OdooRunTemplateDialog(originalConfig: OdooRunConfig) : DialogWrapper(true) {

    // Create a copy of the config so we don't mutate the original until "OK" is clicked
    val workingCopy = originalConfig.copy()
    private val commonUi = OdooRunConfigUI()

    init {
        title = "Edit Odoo Configuration Template"
        init() // This is required to initialize the DialogWrapper
        commonUi.resetFrom(
            workingCopy.odooBinFilePath,
            workingCopy.odooParametersDb,
            workingCopy.odooParametersAddonsPath as List<String>,
            workingCopy.odooParametersExtra,
        )
    }

    override fun createCenterPanel(): JComponent = commonUi.panel

    override fun doOKAction() {
        commonUi.applyTo(workingCopy)
        super.doOKAction()
    }
}
