# odoo-run-config

![Build](https://github.com/lse-odoo/odoo-run-config/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Adjust the [pluginGroup](./gradle.properties) and [pluginName](./gradle.properties), as well as the [id](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [ ] Adjust the plugin description in `README` (see [Tips][docs:plugin-description])
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [ ] Set the `MARKETPLACE_ID` in the above README badges. You can obtain it once the plugin is published to JetBrains Marketplace.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.
- [ ] Configure the [CODECOV_TOKEN](https://docs.codecov.com/docs/quick-start) secret for automated test coverage reports on PRs

<!-- Plugin description -->
Supercharge your Odoo development workflow in PyCharm and IntelliJ IDEA with a dedicated and powerful run configuration manager.
Tired of manually configuring a new generic Python script for every Odoo project, database, or test run? This plugin eliminates repetitive setup by providing a first-class "Odoo" run configuration type, designed specifically for the needs of Odoo developers.

## Key Features:
 - Dedicated Odoo Run Configuration: A new "Odoo" configuration type appears directly in your Run/Debug Configurations dialog, with specialized fields for Odoo.
 - Odoo-Specific Fields: Easily set the path to `odoo-bin`, specify a database (-d flag), and add any other arbitrary command-line arguments.
 - Advanced Addons Path Editor: Manage your addons paths with a user-friendly list editor that supports adding, removing, and editing multiple directory paths.
 - Powerful Template System:
   - Save frequently used configurations (e.g., "Run Odoo 18 Server", "Run Odoo 19", "Run Tests," "Update Modules") as templates.
   - Manage your templates in a dedicated settings panel (`File > Settings > Odoo Settings`).
   - Quickly apply a template to any run configuration to instantly populate all fields.
 
## How It Helps:
 - Boost Productivity: Spend less time configuring and more time coding.
 - Ensure Consistency: Standardize run configurations across your team and projects with shared templates.
 - Reduce Errors: Avoid typos and mistakes from manually entering paths and parameters.

## Getting Started:
 1. Go to `Run > Edit Configurations...`
 2. Click the + (Add New Configuration) button.
 3. Select "Odoo" from the list and start configuring
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "odoo-run-config"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/lse-odoo/odoo-run-config/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
