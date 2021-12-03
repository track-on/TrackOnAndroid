package com.example.trackon.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trackon.R
import com.example.trackon.model.callback.ItemClickListener
import com.example.trackon.model.data.Authority
import com.example.trackon.model.data.User

class UserListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = ArrayList<User>()
    private lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return UserViewHolder(inflater.inflate(R.layout.item_user, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as UserViewHolder
        viewHolder.info.text = "${list[position].userId} | ${list[position].nickName}"
        viewHolder.age.text = list[position].age.toString()
        viewHolder.userAuthority.text = list[position].authority.name
        val color = when (list[position].authority) {
            Authority.USER -> Color.RED
            else -> Color.BLUE
        }
        viewHolder.userAuthority.setTextColor(color)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getUser(index: Int): User {
        return list[index]
    }

    @JvmName("setList1")
    fun setList(list: ArrayList<User>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: ItemClickListener) {
        this.itemClickListener = onClickListener
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var info: TextView = itemView.findViewById(R.id.user_id_text)
        var age: TextView = itemView.findViewById(R.id.user_age_text)
        var userAuthority: TextView = itemView.findViewById(R.id.user_authorizaty)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener.onItemClick(this@UserListAdapter, itemView, position)
            }
        }
    }
}