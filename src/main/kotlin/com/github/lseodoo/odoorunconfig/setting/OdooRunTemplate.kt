package com.github.lseodoo.odoorunconfig.setting

import com.github.lseodoo.odoorunconfig.common.OdooRunConfig

data class OdooRunTemplate(
    var name: String="Standard",
    var version: String="19.0",
    var runConfig: OdooRunConfig = OdooRunConfig(),
)

data class OdooState(
    var runTemplates: MutableList<OdooRunTemplate> = mutableListOf()
)
