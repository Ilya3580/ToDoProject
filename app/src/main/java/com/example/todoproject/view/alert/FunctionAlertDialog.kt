package com.example.todoproject.view.alert

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog

object FunctionAlertDialog {

    public fun startProgressBar(context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val progressBar = ProgressBar(context)

        builder.setView(progressBar)

        builder.setCancelable(false)

        val alert = builder.create()
        alert.show()

        alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.window?.setLayout(300, 300)
        return alert
    }

}