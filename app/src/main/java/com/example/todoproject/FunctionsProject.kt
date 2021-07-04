package com.example.todoproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

object FunctionsProject {
    public fun onCreateAlertDialogCalendar(context: Context, viewModel: MyViewModel<Calendar>, timeUnix : Long) {
        val dateSetting = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            viewModel.user = calendar
            viewModel.getUsersValue()
        }
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeUnix
        val datePickerDialog = DatePickerDialog(context,R.style.DialogTheme, dateSetting,
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
        datePickerDialog.window?.setBackgroundDrawableResource(R.color.BackSecondary)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(context.resources.getColor(R.color.Blue))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(context.resources.getColor(R.color.Blue))
    }

    public fun onCreateAlertDialogTime(context: Context, viewModel: MyViewModel<Calendar>, timeUnix : Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeUnix
        val timeSetting = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute)
            viewModel.user = calendar
            viewModel.getUsersValue()
        }

        val timeDialog = TimePickerDialog(context, R.style.DialogThemeTimePicker,
            timeSetting, 0, 0, true)
        timeDialog.show()
        timeDialog.window?.setBackgroundDrawableResource(R.color.BackSecondary)
        timeDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(context.resources.getColor(R.color.Blue))
        timeDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(context.resources.getColor(R.color.Blue))

    }

}