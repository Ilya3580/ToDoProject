package com.example.todoproject


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dao.Task

class ViewModelList: ViewModel() {

    var userList : MutableLiveData<List<Task>> = MutableLiveData(listOf())

    fun getListUsers() = userList

    fun updateListUsers(value : List<Task>) {
        userList.value = value
    }
}