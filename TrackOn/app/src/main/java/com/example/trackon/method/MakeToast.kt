package com.example.trackon.method

import android.content.Context
import android.widget.Toast

class MakeToast {
    fun setToast(content: String, context: Context) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }
}