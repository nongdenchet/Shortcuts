package com.sentio.shortcuts

import android.content.Context
import android.content.Intent
import android.content.pm.ComponentInfo
import android.content.pm.LauncherApps
import android.content.pm.LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC
import android.content.pm.LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST
import android.content.pm.LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED
import android.content.pm.PackageManager.MATCH_ALL
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Drawable
import android.os.Process
import android.util.ArrayMap
import android.util.Log
import java.util.Collections


class AppManager(private val context: Context) {
    private val TAG = AppManager::class.java.simpleName
    private val CACHE_SIZE = 100
    private val appIconCache = ArrayMap<String, Drawable>(CACHE_SIZE)
    private val shortcutIconCache = ArrayMap<String, Drawable>(CACHE_SIZE)
    private val packageManager = context.packageManager
    private val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    fun getLaunchableApps(): List<App> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(intent, MATCH_ALL)
                .map { it.activityInfo }
                .map { App(it.packageName, it.loadLabel(packageManager).toString(), it) }
    }

    fun getShortcutFromApp(packageName: String): List<Shortcut> {
        val shortcutQuery = LauncherApps.ShortcutQuery()
        shortcutQuery.setQueryFlags(FLAG_MATCH_DYNAMIC or FLAG_MATCH_MANIFEST or FLAG_MATCH_PINNED)
        shortcutQuery.setPackage(packageName)
        return try {
            launcherApps.getShortcuts(shortcutQuery, Process.myUserHandle())
                    .map { Shortcut(it.id, it.`package`, it.shortLabel.toString(), it) }
        } catch (e: SecurityException) {
            Collections.emptyList()
        }
    }

    fun startApp(app: App) {
        context.startActivity(packageManager.getLaunchIntentForPackage(app.packageName))
    }

    fun getAppIcon(componentInfo: ComponentInfo): Drawable?
            = appIconCache[componentInfo.packageName] ?: loadAppIcon(componentInfo)

    private fun loadAppIcon(componentInfo: ComponentInfo): Drawable? {
        return try {
            val drawable = componentInfo.loadIcon(packageManager)
            shortcutIconCache[componentInfo.packageName] = drawable
            drawable
        } catch (e: SecurityException) {
            Log.e(TAG, e.message)
            null
        }
    }

    fun startShortcut(shortcut: Shortcut) {
        launcherApps.startShortcut(shortcut.packageName, shortcut.id, null, null, Process.myUserHandle())
    }

    fun getShortcutIcon(shortcutInfo: ShortcutInfo)
            = shortcutIconCache[shortcutIdentity(shortcutInfo)] ?: loadShortcutIcon(shortcutInfo)

    private fun shortcutIdentity(shortcutInfo: ShortcutInfo)
            = "${shortcutInfo.`package`}/${shortcutInfo.id}"

    private fun loadShortcutIcon(shortcutInfo: ShortcutInfo): Drawable? {
        return try {
            val drawable = launcherApps.getShortcutIconDrawable(shortcutInfo,
                    context.resources.displayMetrics.densityDpi)
            shortcutIconCache[shortcutIdentity(shortcutInfo)] = drawable
            drawable
        } catch (e: SecurityException) {
            Log.e(TAG, e.message)
            null
        }
    }
}
