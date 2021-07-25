package com.example.todoproject.view.date

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.todoproject.R
import com.example.todoproject.TaskActivity
import com.example.todoproject.database.Task
import com.example.todoproject.viewmodel.TaskActivityViewModel
import java.util.*

object CalendarFunction {
    public fun onCreateAlertDialogCalendar(
        context: Context,
        viewModel: TaskActivityViewModel,
        timeUnix: Long
    ) {
        val dateSetting = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth, 0, 0)
            viewModel.updateDate(calendar)
        }
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeUnix
        val datePickerDialog = DatePickerDialog(
            context,
            R.style.DialogTheme,
            dateSetting,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
        datePickerDialog.window?.setBackgroundDrawableResource(R.color.BackSecondary)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(context.resources.getColor(R.color.Blue))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(context.resources.getColor(R.color.Blue))
    }

    public fun onCreateAlertDialogTime(
        context: Context,
        viewModel: TaskActivityViewModel,
        timeUnix: Long
    ) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeUnix
        val timeSetting = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.set(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute
            )
            viewModel.updateDate(calendar)
        }

        val timeDialog = TimePickerDialog(
            context, R.style.DialogThemeTimePicker,
            timeSetting, 0, 0, true
        )
        timeDialog.show()
        timeDialog.window?.setBackgroundDrawableResource(R.color.BackSecondary)
        timeDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(context.resources.getColor(R.color.Blue))
        timeDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(context.resources.getColor(R.color.Blue))

    }

    public fun convertDate(task : Task, context: Context) : String{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = task.deadline!!
        var str: String = calendar.get(Calendar.DAY_OF_MONTH).toString()
        when (calendar.get(Calendar.MONTH)) {
            0 -> str += " " + context.resources.getString(R.string.month0)
            1 -> str += " " + context.resources.getString(R.string.month1)
            2 -> str += " " + context.resources.getString(R.string.month2)
            3 -> str += " " + context.resources.getString(R.string.month3)
            4 -> str += " " + context.resources.getString(R.string.month4)
            5 -> str += " " + context.resources.getString(R.string.month5)
            6 -> str += " " + context.resources.getString(R.string.month6)
            7 -> str += " " + context.resources.getString(R.string.month7)
            8 -> str += " " + context.resources.getString(R.string.month8)
            9 -> str += " " + context.resources.getString(R.string.month9)
            10 -> str += " " + context.resources.getString(R.string.month10)
            11 -> str += " " + context.resources.getString(R.string.month11)
        }

        str += " " + calendar.get(Calendar.YEAR)

        if (calendar.get(Calendar.HOUR) != 0 || calendar.get(Calendar.MINUTE) != 0) {
            str += "\n${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)}"
        }

        return str
    }
}