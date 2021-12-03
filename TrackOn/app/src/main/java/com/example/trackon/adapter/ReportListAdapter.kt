package com.example.trackon.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trackon.R
import com.example.trackon.model.callback.ItemClickListener
import com.example.trackon.model.callback.ItemLongClickListener
import com.example.trackon.model.data.ReportItem
import com.example.trackon.model.data.ReportType
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import kotlin.math.round

@Suppress("DEPRECATION")
class ReportListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var reportItem = ArrayList<ReportItem>()

    private lateinit var itemClickListener: ItemClickListener
    private lateinit var itemLongCLickListener: ItemLongClickListener

    private val sparseBooleanArray = SparseBooleanArray(0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ReportViewHolder(inflater.inflate(R.layout.report_list, parent,false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val reportId = reportItem[position].reportId
        val reportMessage = reportItem[position].message
        val reportAt = reportItem[position].reportAt
        val reportType = reportItem[position].reportType
        val longitude = reportItem[position].longitude
        val latitude = reportItem[position].latitude
        val name = reportItem[position].name
        val reporter = reportItem[position].reporter

        val mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
        val address = MapReverseGeoCoder.findAddressForMapPoint("2766585da6e51ecd7c02786b6e8674bf", mapPoint)
        println("장소 $address")

        val viewHolder = (holder as ReportViewHolder)
        viewHolder.reportId.text = "$reportId | $reporter -> $name"
        viewHolder.reportMessage.text = reportMessage
        viewHolder.reportAt.text = reportAt
        viewHolder.reportType.text = when (reportType) {
            ReportType.PROGRESS -> "처리중"
            else -> "처리 완료"
        }
        val textColor: Int = when (reportType) {
            ReportType.PROGRESS -> Color.RED
            else -> Color.GREEN
        }
        viewHolder.reportType.setTextColor(textColor)
        viewHolder.reportPlace.text = (round((latitude * 10000)) / 10000).toString()
        viewHolder.reportPlace2.text = (round((longitude * 10000)) / 10000).toString()
    }

    override fun getItemCount(): Int {
        return reportItem.size
    }

    fun setList(list: ArrayList<ReportItem>) {
        this.reportItem = list
        notifyDataSetChanged()
    }

    fun getReport(index: Int) : ReportItem {
        return reportItem[index]
    }

    fun setOnClickListener(onClickListener: ItemClickListener) {
        this.itemClickListener = onClickListener
    }

    fun setOnLongClickListener(onLongClickListener: ItemLongClickListener) {
        this.itemLongCLickListener = onLongClickListener
    }

    inner class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reportId: TextView = view.findViewById(R.id.report_id_text)
        val reportMessage: TextView = view.findViewById(R.id.report_message_text)
        val reportAt: TextView = view.findViewById(R.id.report_date_text)
        val reportType: TextView = view.findViewById(R.id.report_status_text)
        val reportPlace: TextView = view.findViewById(R.id.report_place_text)
        val reportPlace2: TextView = view.findViewById(R.id.report_place2_text)

        init {
            view.setOnLongClickListener {
                val position = adapterPosition
                when {
                    sparseBooleanArray.get(position, false) -> {
                    }
                    else -> {
                        sparseBooleanArray.put(position, false)
                    }
                }
                itemLongCLickListener.onItemLongClick(this@ReportListAdapter, view, position)
                false
            }
            view.setOnClickListener {
                val position = adapterPosition
                itemClickListener.onItemClick(this@ReportListAdapter, view, position)
            }
        }
    }
}