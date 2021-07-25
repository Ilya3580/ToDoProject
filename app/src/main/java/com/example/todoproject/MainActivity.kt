package com.example.todoproject

import android.content.BroadcastReceiver
import com.example.todoproject.network.APIService
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.todoproject.database.DatabaseStorage
import com.example.todoproject.databinding.ActivityMainBinding
import com.example.todoproject.database.Task
import com.example.todoproject.view.recyclerview.AdapterRecyclerView
import com.example.todoproject.view.recyclerview.ItemTouchHelperAdapter
import com.example.todoproject.view.recyclerview.SwipeToLeftCallback
import com.example.todoproject.view.recyclerview.SwipeToRightCallback
import com.example.todoproject.viewmodel.MainViewModel
import com.example.todoproject.worker_and_receiver.InternetReceiver
import com.example.todoproject.worker_and_receiver.SynchronizeWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var buildAPIService: APIService

    @Inject
    lateinit var buildDatabase: DatabaseStorage

    @Inject
    lateinit var oneTimeWorkRequest: OneTimeWorkRequest

    @Inject
    lateinit var periodicWorkRequest: PeriodicWorkRequest

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var receiver : BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as App).getComponent().inject(this)
        settingReceiver()
        settingRecyclerView()
        if (mainViewModel.flagScope) {
            binding.motionLayout.transitionToEnd()
        }

        val taskId = intent.getSerializableExtra("task") as? Task
        if(taskId != null){
            oneWorkStart()
        }

        binding.actionButton.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            startActivity(intent)
        }

        mainViewModel.userList.observe(this, {
            updateValuesList(it)
        })

        mainViewModel.internetConnection?.observe(this, {
            if (it && !(application as App).flagUpdateData) {
                (application as App).flagUpdateData = true
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        SynchronizeWorker.synchronizeTasks(
                            mainViewModel,
                            buildAPIService,
                            buildDatabase
                        )
                    } catch (e: Exception) {
                        (application as App).flagUpdateData = false
                        lifecycleScope.launch(Dispatchers.IO) {
                            mainViewModel.updateListUsers(buildDatabase.taskDao().listTask())
                        }
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        periodWorkStart()
        try {
            unregisterReceiver(receiver)
        }catch (e : Exception){

        }
    }

    private fun settingRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        if (mainViewModel.adapterRecyclerView == null) {
            mainViewModel.adapterRecyclerView =
                AdapterRecyclerView(ArrayList(), mainViewModel, buildDatabase)
        }

        val callbackLeft: ItemTouchHelper.Callback =
            SwipeToLeftCallback(this, mainViewModel.adapterRecyclerView as ItemTouchHelperAdapter)
        val touchHelperLeft = ItemTouchHelper(callbackLeft)
        touchHelperLeft.attachToRecyclerView(binding.recyclerView)

        val callbackRight: ItemTouchHelper.Callback =
            SwipeToRightCallback(this, mainViewModel.adapterRecyclerView as ItemTouchHelperAdapter)
        val touchHelperRight = ItemTouchHelper(callbackRight)
        touchHelperRight.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.adapter = mainViewModel.adapterRecyclerView
    }

    private fun updateValuesList(lst: List<Task>) {
        mainViewModel.adapterRecyclerView?.values = lst.toCollection(ArrayList())
        mainViewModel.adapterRecyclerView?.notifyDataSetChanged()
    }

    private fun settingReceiver() {
        if (mainViewModel.internetConnection == null) {
            mainViewModel.internetConnection =
                MutableLiveData(InternetReceiver.checkInternet(applicationContext))
        }
        receiver = InternetReceiver(mainViewModel)
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(receiver, intentFilter)
    }

    private fun oneWorkStart() {
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "mainWorker",
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
    }

    private fun periodWorkStart() {
        WorkManager.getInstance(applicationContext).enqueue(
            periodicWorkRequest
        )
    }
}


