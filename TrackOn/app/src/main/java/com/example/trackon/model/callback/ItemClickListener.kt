package com.example.trackon.model.callback

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface ItemClickListener {
    fun onItemClick(viewHolder: RecyclerView.Adapter<RecyclerView.ViewHolder>, view: View, position: Int)
}