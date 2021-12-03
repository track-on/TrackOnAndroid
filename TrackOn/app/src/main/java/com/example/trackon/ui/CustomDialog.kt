package com.example.trackon.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.trackon.R
import java.util.*

class CustomDialog(context: Context) : Dialog(context) {

    lateinit var content: TextView
    lateinit var positiveBtn: TextView
    lateinit var negativeBtn: TextView
    lateinit var title: TextView
    lateinit var editId: EditText
    lateinit var editMessage: EditText

    private lateinit var positiveListener: View.OnClickListener
    private lateinit var negativeListener: View.OnClickListener
    private var titleText: String = "신고하시겠습니까?"
    private var contentText: String = "신고할 마커의 ID값을 입력해주세요"
    private var poText: String = "네"
    private var neText: String = "아니요"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog)

        content = findViewById(R.id.content_dialog)
        title = findViewById(R.id.title_dialog)
        positiveBtn = findViewById(R.id.yes_button)
        negativeBtn = findViewById(R.id.no_button)
        editId = findViewById(R.id.edit_marker_id)
        editMessage = findViewById(R.id.edit_marker_message)

        Objects.requireNonNull(window)?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        positiveBtn.text = poText
        negativeBtn.text = neText
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

    fun setBtnTexts(posText: String, negText: String) {
        this.poText = posText
        this.neText = negText
    }

    fun setTitleAndContent(title: String, content: String) {
        this.titleText = title
        this.contentText = content
    }
}