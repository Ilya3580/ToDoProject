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

    public suspend fun listApi(
        context: Context,
        mutableLiveData: MutableLiveData<ArrayList<Task>>? = null
    ) {
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

        val userService: APIService = retrofit.create(APIService::class.java)

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val builder = DatabaseStorage.build(context)
            val listAct: List<TaskAct> = builder.taskDao().listTaskAct()
            val list: List<Task> = builder.taskDao().listTask()
            var listApi: List<Task> = userService.getTasks()

            for (i in listAct) {
                when (i.act) {
                    TaskAct.ACT_ADD -> {
                        userService.setTasks(list.find { it.id == i.id }!!)
                    }
                    TaskAct.ACT_DELETE -> {
                        userService.deleteTasks(i.id)
                    }
                    TaskAct.ACT_UPDATE -> {
                        if(listApi.find { it.id == i.id }!!.update_at!! < list.find { it.id == i.id }!!.update_at!!)
                            userService.updateTasks(i.id)
                    }
                }

                builder.taskDao().deleteTaskAct(i)
            }
            listApi = userService.getTasks()

            for(i in listApi){
                builder.taskDao().insertTask(i)
            }

            if(mutableLiveData != null){
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    mutableLiveData.value = listApi
                }
            }
        }
    }

}