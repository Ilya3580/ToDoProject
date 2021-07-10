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

            for(i in listAct){
                val task = withContext(Dispatchers.IO){
                    builder.taskDao().findTask(i.id)
                }

                when(i.act){
                    TaskAct.ACT_ADD -> {
                        apiService.setTasks(task)
                    }
                }
            }
        }
        return Result.success()
    }


}