package com.example.todoproject

import APIService.APIService
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import dao.DatabaseStorage
import dao.Task
import dao.TaskAct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

object FunctionsProject {
    public fun onCreateAlertDialogCalendar(
        context: Context,
        viewModel: MutableLiveData<Calendar>,
        timeUnix: Long
    ) {
        val dateSetting = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth, 0, 0)
            viewModel.value = calendar
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
        viewModel: MutableLiveData<Calendar>,
        timeUnix: Long
    ) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeUnix
        val timeSetting = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.set(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute
            )
            viewModel.value = calendar
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

    public fun stopProgressBar(alert: AlertDialog) {
        alert.dismiss()
    }

    public fun userService() : APIService{
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                val request = builder
                    .addHeader("Authorization", "Bearer 8142b4fa951c4dd99fda81aab42b745c")
                    .build()

                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("https://d5dps3h13rv6902lp5c8.apigw.yandexcloud.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(APIService::class.java)
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