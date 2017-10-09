package com.sentio.shortcuts

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class AppListAdapter(private val appManager: AppManager) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {
    private val apps: List<App> = appManager.getLaunchableApps().sortedBy { it.label }
    var itemLongClickListener: (App, View) -> Boolean = { _, _ -> false }

    override fun getItemCount(): Int = apps.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(apps[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        private val tvLabel: TextView = itemView.findViewById(R.id.tvLabel)

        fun bind(app: App) {
            itemView.setOnLongClickListener { itemLongClickListener.invoke(app, ivIcon) }
            itemView.setOnClickListener { appManager.startApp(app) }
            ivIcon.setImageDrawable(appManager.getAppIcon(app.componentInfo))
            tvLabel.text = app.label
        }
    }
}
