package com.example.todoproject.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todoproject.database.Task
import com.example.todoproject.view.recyclerview.AdapterRecyclerView

class MainViewModel: ViewModel() {

    var userList : MutableLiveData<List<Task>> = MutableLiveData(listOf())
    var internetConnection : MutableLiveData<Boolean>? = null
    var adapterRecyclerView: AdapterRecyclerView? = null
    var flagScope = false

    private val handler = Handler(Looper.getMainLooper())

    fun updateListUsers(value : List<Task>) {
        handler.post {
            userList.value = value
        }
    }

    fun statusInternet(value : Boolean){
        handler.post {
            internetConnection?.value = value
        }
    }
}