package com.example.trackon.model.callback

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface ItemLongClickListener {
    fun onItemLongClick(viewHolder: RecyclerView.Adapter<RecyclerView.ViewHolder>, view: View, position: Int)
}