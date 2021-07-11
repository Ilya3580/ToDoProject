package WorkerAndReceiver

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todoproject.FunctionsProject
import dao.DatabaseStorage
import dao.Task
import dao.TaskAct
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*

class SomeWorker(
    context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val apiService = FunctionsProject.userService()
        val builder = DatabaseStorage.build(applicationContext)
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val listAct = withContext(Dispatchers.IO) {
                builder.taskDao().listTaskAct()
            }

            val listApi = withContext(Dispatchers.IO) {
                apiService.getTasks()
            }

            for (i in listAct) {
                val task = withContext(Dispatchers.IO) {
                    builder.taskDao().findTask(i.id)
                }

                when (i.act) {
                    TaskAct.ACT_ADD -> {
                        apiService.setTasks(task)
                    }

                    TaskAct.ACT_DELETE -> {
                        apiService.deleteTasks(task.id)
                    }

                    TaskAct.ACT_UPDATE -> {
                        if(listApi.find { it.id == i.id }?.update_at ?: Long.MAX_VALUE < task.update_at ?: 0){
                            apiService.updateTasks(task.id, task)
                        }
                    }
                }

                builder.taskDao().deleteTaskAct(i)
            }
        }
        return Result.success()
    }


}