package com.example.todoproject.worker_and_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.example.todoproject.viewmodel.MainViewModel

class InternetReceiver(
    private var mainViewModel: MainViewModel,
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        mainViewModel.statusInternet(checkInternet(context))
    }

    companion object{
        fun checkInternet(context: Context?) : Boolean{
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activityNetwork = cm.activeNetworkInfo
            return activityNetwork?.isConnectedOrConnecting == true
        }
    }
}