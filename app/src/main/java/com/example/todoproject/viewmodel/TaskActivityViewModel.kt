package com.example.todoproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todoproject.database.Task
import java.util.*

class TaskActivityViewModel : ViewModel() {
    var task: Task? = null
    var flagSave : Boolean? = null

    var calendarLivedata = MutableLiveData<Calendar>()

    fun updateDate(value : Calendar){
        calendarLivedata.value = value
    }

}