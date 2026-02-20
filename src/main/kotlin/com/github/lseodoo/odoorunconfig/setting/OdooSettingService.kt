package com.github.lseodoo.odoorunconfig.setting

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage


@Service(Service.Level.APP)
@State(
    name = "OdooSettingService",
    storages = [Storage("odoo-settings.xml")]
)
class OdooSettingService : PersistentStateComponent<OdooState> {
    private var odooState = OdooState()

    override fun getState(): OdooState = odooState

    override fun loadState(state: OdooState) {
        odooState = state
    }

    companion object {
        val instance: OdooSettingService
            get() = ApplicationManager.getApplication().getService(OdooSettingService::class.java)
    }
}