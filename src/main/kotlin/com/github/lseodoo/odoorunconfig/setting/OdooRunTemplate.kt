package com.github.lseodoo.odoorunconfig.setting

import com.github.lseodoo.odoorunconfig.state.OdooRunConfig
import com.intellij.util.ui.ColumnInfo

data class OdooRunTemplate(
    var name: String="Standard",
    var version: String="19.0",
    var runConfig: OdooRunConfig = OdooRunConfig(),
)


class NameColumn : ColumnInfo<OdooRunTemplate, String>("Name") {
    override fun valueOf(item: OdooRunTemplate): String = item.name
    override fun setValue(item: OdooRunTemplate, value: String) { item.name = value }
    override fun isCellEditable(item: OdooRunTemplate): Boolean = true
}


class VersionColumn : ColumnInfo<OdooRunTemplate, String>("Version") {
    override fun valueOf(item: OdooRunTemplate): String = item.version
    override fun setValue(item: OdooRunTemplate, value: String) { item.version = value }
    override fun isCellEditable(item: OdooRunTemplate): Boolean = true
}

data class OdooState(
    var runTemplates: MutableList<OdooRunTemplate> = mutableListOf()
)
