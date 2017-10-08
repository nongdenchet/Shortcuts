package com.sentio.shortcuts.model

import android.content.pm.ComponentInfo
import android.content.pm.ShortcutInfo

data class App(val packageName: String, val label: String, val componentInfo: ComponentInfo)
data class Shortcut(val id: String, val packageName: String, val label: String, val shortcutInfo: ShortcutInfo)
