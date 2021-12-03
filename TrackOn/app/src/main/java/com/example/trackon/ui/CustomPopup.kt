package com.example.trackon.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.trackon.R
import java.util.*

class CustomPopup(context: Context) : Dialog(context) {

    lateinit var content: TextView
    lateinit var positiveBtn: TextView
    lateinit var negativeBtn: TextView
    lateinit var title: TextView


    private lateinit var positiveListener: View.OnClickListener
    private lateinit var negativeListener: View.OnClickListener
    private var titleText: String = "신고하시겠습니까?"
    private var contentText: String = "신고할 마커의 ID값을 입력해주세요"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_popup)

        content = findViewById(R.id.content_popup)
        title = findViewById(R.id.title_popup)
        positiveBtn = findViewById(R.id.yes_button)
        negativeBtn = findViewById(R.id.no_button)

        Objects.requireNonNull(window)?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        title.text = titleText
        content.text = contentText

        positiveBtn.setOnClickListener(positiveListener)
        negativeBtn.setOnClickListener(negativeListener)
    }

    fun setPositiveListener(listener: View.OnClickListener) {
        this.positiveListener = listener
    }

    fun setNegativeListener(listener: View.OnClickListener) {
        this.negativeListener = listener
    }

    fun setTitleAndContent(title: String, content: String) {
        this.titleText = title
        this.contentText = content
    }
}