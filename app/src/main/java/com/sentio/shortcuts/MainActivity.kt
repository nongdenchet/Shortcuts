package com.sentio.shortcuts

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var appList: RecyclerView
    private lateinit var appManager: AppManager
    private var shortcutPopup: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appManager = AppManager(applicationContext)
        initAppList()
    }

    private fun initAppList() {
        val adapter = AppListAdapter(appManager)
        adapter.itemLongClickListener = { app, itemView -> showPopup(app, itemView) }
        appList = findViewById(R.id.appList)
        appList.layoutManager = GridLayoutManager(this, 4)
        appList.adapter = adapter
    }

    private fun showPopup(app: App, itemView: View): Boolean {
        val shortcuts = appManager.getShortcutFromApp(app.packageName)
        if (shortcuts.isNotEmpty()) {
            val contentView = createShortcutListView(shortcuts)
            val locations = IntArray(2, { 0 })
            itemView.getLocationOnScreen(locations)
            shortcutPopup?.dismiss()
            shortcutPopup = PopupWindow(contentView, WRAP_CONTENT, WRAP_CONTENT, true)
            shortcutPopup?.animationStyle = R.style.PopupAnimation
            shortcutPopup?.showAtLocation(itemView, Gravity.NO_GRAVITY,
                    locations[0] + itemView.width / 2,
                    locations[1] + itemView.height / 2)
        } else {
            Toast.makeText(this, getString(R.string.no_shortcut), Toast.LENGTH_SHORT).show()
        }
        return true
    }

    @SuppressLint("InflateParams")
    private fun createShortcutListView(shortcuts: List<Shortcut>): View {
        val view = LayoutInflater.from(this).inflate(R.layout.popup_shortcut, null)
        val shortcutList: RecyclerView = view.findViewById(R.id.shortcutList)
        shortcutList.adapter = ShortcutListAdapter(shortcuts, appManager)
        shortcutList.layoutManager = LinearLayoutManager(this)
        return view
    }

    override fun onBackPressed() {
        // Disable
    }
}
