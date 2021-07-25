package com.example.todoproject.worker_and_receiver

import android.content.Context
import android.util.Log
import androidx.annotation.RequiresOptIn
import androidx.work.*
import com.example.todoproject.database.DatabaseStorage
import com.example.todoproject.database.Task
import com.example.todoproject.database.TaskAct
import com.example.todoproject.network.APIService
import com.example.todoproject.viewmodel.MainViewModel
import kotlinx.coroutines.*
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.logging.Handler
import javax.inject.Inject

class SynchronizeWorker(
    val context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val scope = CoroutineScope(Dispatchers.IO)
        val buildAPIService = APIService.userService()
        val buildDatabaseStorage = DatabaseStorage.build(context)
        scope.launch {
            synchronizeTasks(null, buildAPIService, buildDatabaseStorage)
            uploadTask(buildAPIService, buildDatabaseStorage)
        }

        return Result.success()
    }

    private suspend fun uploadTask(
        buildAPIService: APIService,
        buildDatabaseStorage: DatabaseStorage
    ): Result {
        synchronizeTasks(null, buildAPIService, buildDatabaseStorage)
        val listTaskAct = buildDatabaseStorage.taskDao().listTaskAct()
        val listTask = buildDatabaseStorage.taskDao().listTask()
        val scope = CoroutineScope(Dispatchers.IO)
        val arrayListResult = ArrayList<Deferred<Unit>>()
        for (i in listTaskAct) {
            val task = listTask.find { it.id == i.id }
            val job = scope.async {
                when (i.act) {
                    TaskAct.ACT_ADD -> {
                        Log.d("TAGA", task.toString())
                        buildAPIService.setTasks(task!!)
                    }
                    TaskAct.ACT_UPDATE -> {
                        buildAPIService.updateTasks(i.id, task!!)
                    }
                    TaskAct.ACT_DELETE -> {
                        buildAPIService.deleteTasks(i.id)
                    }
                }
            }
            arrayListResult.add(job)

        }
        var result = Result.success()
        for(i in (0 until listTaskAct.count())){
            try{
                arrayListResult[i].await()
                buildDatabaseStorage.taskDao().deleteTaskAct(listTaskAct[i])
            }catch (e : Exception){
                result = Result.failure()
            }
        }
        return result

    }

    companion object {

        public fun oneTimeWorkerSetting(): OneTimeWorkRequest {
            val constraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return OneTimeWorkRequest.Builder(SynchronizeWorker::class.java)
                .setConstraints(constraint)
                .setInitialDelay(5, TimeUnit.SECONDS)
                .build()
        }

        public fun periodTimeWorkSetting(): PeriodicWorkRequest {
            val constraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return PeriodicWorkRequest.Builder(
                SynchronizeWorker::class.java,
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraint)
                .build()
        }


        public suspend fun synchronizeTasks(
            viewModel: MainViewModel? = null,
            buildAPIService: APIService,
            buildDatabase: DatabaseStorage,
        ) {
            val scope = CoroutineScope(Dispatchers.IO)
            val listAPIService = scope.async {
                try {
                    buildAPIService.getTasks()
                } catch (e: Exception) {
                    null
                }
            }
            val listAct = scope.async {
                buildDatabase.taskDao().listTaskAct()
            }
            val listTask = scope.async {
                buildDatabase.taskDao().listTask().toCollection(ArrayList())
            }

            if (listAPIService.await() == null) return

            for (i in listAPIService.await()!!) {
                val itemAct = scope.async { listAct.await().find { it.id == i.id } }
                val itemList = scope.async { listTask.await().find { it.id == i.id } }
                if (itemList.await() == null) {
                    if (itemAct.await() == null) {
                        buildDatabase.taskDao().insertTask(i)
                    }
                } else {
                    if (itemList.await()!!.update_at!! < i.update_at!!) {
                        if (itemAct.await() != null) {
                            buildDatabase.taskDao().deleteTaskAct(itemAct.await()!!)
                        }
                        buildDatabase.taskDao().updateTask(i)
                    }
                }
            }

            if (viewModel == null) return

            viewModel.updateListUsers(buildDatabase.taskDao().listTask())
        }
    }
}