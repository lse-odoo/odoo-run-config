package com.github.lseodoo.odoorunconfig.common

import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.XCollection

/**
 * Basic Odoo state with low-level information useful for any Odoo run configuration.
 */
data class OdooRunConfig(
    @Attribute("odoo-bin-path")
    var odooBinFilePath: String? = null,

    @Attribute("odoo-parameters-db")
    var odooParametersDb: String? = null,

    @XCollection(elementName = "odoo-parameters-addons-path")
    var odooParametersAddonsPath: MutableList<String> = mutableListOf(),

    @Attribute("odoo-parameters-extra")
    var odooParametersExtra: String? = null,
)
