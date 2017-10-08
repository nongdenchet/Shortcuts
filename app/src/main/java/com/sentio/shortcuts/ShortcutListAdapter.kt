package com.sentio.shortcuts

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class ShortcutListAdapter(private val shortcuts: List<Shortcut>, private val appManager: AppManager)
    : RecyclerView.Adapter<ShortcutListAdapter.ViewHolder>() {

    override fun getItemCount(): Int = shortcuts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_shortcut, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(shortcuts[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        private val tvLabel: TextView = itemView.findViewById(R.id.tvLabel)

        fun bind(shortcut: Shortcut) {
            itemView.setOnClickListener { appManager.startShortcut(shortcut) }
            ivIcon.setImageDrawable(appManager.getShortcutIcon(shortcut.shortcutInfo))
            tvLabel.text = shortcut.label
        }
    }
}
