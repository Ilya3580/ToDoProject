package com.example.todoproject

import com.example.todoproject.api_Service.APIService
import com.example.todoproject.worker_and_receiver.SomeWorker
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.example.todoproject.databinding.ActivityMainBinding
import com.example.todoproject.dao.DatabaseStorage
import com.example.todoproject.dao.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var adapterRecyclerView: AdapterRecyclerView? = null
    private lateinit var mutableLiveDataDate: MutableLiveData<Calendar>
    private lateinit var apiService: APIService

    private val viewModelList: ViewModelList by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateData(intent.getSerializableExtra("idName")?.toString())

        mutableLiveDataDate = MutableLiveData(null)

        binding.iconCalendar.setOnClickListener {
            FunctionsProject.onCreateAlertDialogCalendar(
                this,
                mutableLiveDataDate,
                Calendar.getInstance().timeInMillis
            )
        }

        binding.actionButton.setOnClickListener {
            val intent = Intent(this, ActivityTask::class.java)
            startActivity(intent)
        }

        mutableLiveDataDate.observe(this, {
            //TODO("Действие с числом календаря")
        })

    }

    override fun onSaveInstanceState(outState: Bundle) {
        try {
            outState.putBoolean(
                "flagScope",
                (binding.recyclerView.adapter as AdapterRecyclerView).flagScope
            )
        } catch (e: Exception) {
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val flagScope = savedInstanceState.getBoolean("flagScope")
        if (!flagScope) {
            binding.motionLayout.transitionToEnd()
        }
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun updateData(id: String?) {
        apiService = FunctionsProject.userService()
        val builder = DatabaseStorage.build(applicationContext)
        val handler = Handler(Looper.getMainLooper())
        Log.d("TAGA", id.toString())
        if (id == null) {
            if (viewModelList.userList.value?.count() ?: 0 > 0) {
                settingRecyclerView(viewModelList.userList.value!!)
                return
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    val itemList = builder.taskDao().listTask()
                    handler.post {
                        settingRecyclerView(itemList)
                    }
                }

            }
        } else {
            if (viewModelList.userList.value?.count() ?: 0 > 0) {
                settingRecyclerView(viewModelList.userList.value!!)
                return
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    val itemList = builder.taskDao().listTask()
                    handler.post {
                        viewModelList.userList.value = itemList
                        settingWorker()
                        settingRecyclerView(itemList)
                    }
                }
            }
        }
    }

    private fun settingRecyclerView(lst: List<Task>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapterRecyclerView = AdapterRecyclerView(lst as ArrayList<Task>)

        val callbackLeft: ItemTouchHelper.Callback =
            SwipeToLeftCallback(this, adapterRecyclerView as ItemTouchHelperAdapter)
        val touchHelperLeft = ItemTouchHelper(callbackLeft)
        touchHelperLeft.attachToRecyclerView(binding.recyclerView)

        val callbackRight: ItemTouchHelper.Callback =
            SwipeToRightCallback(this, adapterRecyclerView as ItemTouchHelperAdapter)
        val touchHelperRight = ItemTouchHelper(callbackRight)
        touchHelperRight.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.adapter = adapterRecyclerView
    }

    private fun settingReceiver() {

    }

    private fun settingWorker() {
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val someWorker = OneTimeWorkRequest.Builder(SomeWorker::class.java)
            .setConstraints(constraint)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "someWorker",
            ExistingWorkPolicy.REPLACE,
            someWorker
        )
    }
}


