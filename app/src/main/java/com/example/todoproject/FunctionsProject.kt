package com.example.todoproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.*

object FunctionsProject {
    public fun onCreateAlertDialogCalendar(context: Context, viewModel: MyViewModel<Date>, date : Date) {
        /*val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)*/
        val dateSetting = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            viewModel.user = Date(year, month, dayOfMonth)
            viewModel.getUsersValue()
        }
        val datePickerDialog = DatePickerDialog(context,R.style.DialogTheme, dateSetting,
            date.year, date.month, date.day)
        datePickerDialog.show()
        datePickerDialog.window?.setBackgroundDrawableResource(R.color.BackSecondary)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(context.resources.getColor(R.color.Blue))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(context.resources.getColor(R.color.Blue))
    }

    public fun onCreateAlertDialogTime(context: Context, viewModel: MyViewModel<Date>, date : Date) {
        val timeSetting = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            viewModel.user = Date(0,0,0,hourOfDay, minute)
            viewModel.getUsersValue()
        }
        val timeDialog = TimePickerDialog(context, R.style.DialogThemeTimePicker, timeSetting, date.hours, date.minutes, true)
        timeDialog.show()
        timeDialog.window?.setBackgroundDrawableResource(R.color.BackSecondary)
        timeDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(context.resources.getColor(R.color.Blue))
        timeDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(context.resources.getColor(R.color.Blue))

    }

}