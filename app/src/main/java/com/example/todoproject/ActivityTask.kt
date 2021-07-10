package com.example.todoproject

import WorkerAndReceiver.SomeWorker
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.work.*
import com.example.todoproject.databinding.ActivityTaskBinding
import dao.DatabaseStorage
import dao.Task
import dao.TaskAct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ActivityTask : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    @Volatile
    private lateinit var task: Task
    private var flagSave = false

    private lateinit var viewModelCalendar: MutableLiveData<Calendar>
    private lateinit var viewModelTime: MutableLiveData<Calendar>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelCalendar = MutableLiveData(null)
        viewModelTime = MutableLiveData(null)

        task = if (savedInstanceState?.getSerializable("task") != null) {
            savedInstanceState.getSerializable("task") as Task
        } else {
            intent.getSerializableExtra("task") as? Task ?: Task(UUID.randomUUID().toString())
        }

        fixTask()

        binding.closeImageButton.setOnClickListener {
            onBackPressed()
        }

        val builder = DatabaseStorage.build(applicationContext)

        binding.saveTextView.setOnClickListener {
            val alert = FunctionsProject.startProgressBar(this)
            task.text = binding.contentScrolling.editTextTask.text.toString()
            task.update_at = Calendar.getInstance().time.time
            if(!flagSave){
                task.created_at = Calendar.getInstance().time.time
            }
            task.deadline = Calendar.getInstance().time.time
            lifecycleScope.launch(Dispatchers.IO) {
                val taskAct = TaskAct(task.id, TaskAct.ACT_ADD)
                builder.taskDao().insertTaskAct(taskAct)
                builder.taskDao().insertTask(task)
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    FunctionsProject.stopProgressBar(alert)
                    val intent = Intent(this@ActivityTask, MainActivity::class.java)
                    intent.putExtra("idName", task.id)
                    startActivity(intent)

                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
        }

        binding.contentScrolling.importanceContainer.setOnClickListener {
            showMenu(binding.contentScrolling.importanceContainer)
        }

        binding.contentScrolling.deadlineContainer.setOnClickListener {
            if (!binding.contentScrolling.switchDeadline.isChecked) {
                FunctionsProject.onCreateAlertDialogCalendar(
                    this,
                    viewModelCalendar,
                    Calendar.getInstance().time.time
                )
            }
        }

        binding.contentScrolling.switchDeadline.setOnClickListener {
            if (binding.contentScrolling.switchDeadline.isChecked) {
                binding.contentScrolling.switchDeadline.isChecked = false
                FunctionsProject.onCreateAlertDialogCalendar(
                    this,
                    viewModelCalendar,
                    Calendar.getInstance().time.time
                )
            } else {
                task.deadline = null
                binding.contentScrolling.deadlineText.visibility = View.GONE
            }
        }

        viewModelCalendar.observe(this, {
            if (it != null) {
                task.deadline = it.timeInMillis
                fixTextDeadline()
                FunctionsProject.onCreateAlertDialogTime(
                    this, viewModelTime,
                    task.deadline!!
                )
            }
        })

        viewModelTime.observe(this, {
            if (it != null) {
                task.deadline = it.timeInMillis
                fixTextDeadline()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("task", task)
        outState.putBoolean("flagSave", flagSave)
        super.onSaveInstanceState(outState)
    }

    private fun showMenu(view: View?) {
        val menu = PopupMenu(this, view)
        menu.setOnMenuItemClickListener { item: MenuItem? ->
            when (item?.itemId) {
                R.id.item_not -> {
                    task.importance = Task.IMPORTANCE
                    binding.contentScrolling.statusImportance.text =
                        resources.getString(R.string.no)
                }
                R.id.item_little -> {
                    task.importance = Task.IMPORTANCE_LOW
                    binding.contentScrolling.statusImportance.text =
                        resources.getString(R.string.little)
                }

                R.id.item_big -> {
                    task.importance = Task.IMPORTANCE_BASIC
                    binding.contentScrolling.statusImportance.text =
                        resources.getString(R.string.big)
                }
            }
            false
        }
        menu.inflate(R.menu.menu_importance)
        menu.show()
    }

    private fun fixTask() {
        binding.contentScrolling.editTextTask.setText(task.text)
        when (task.importance) {
            Task.IMPORTANCE -> {
                binding.contentScrolling.statusImportance.text = resources.getString(R.string.no)
            }
            Task.IMPORTANCE_LOW -> {
                binding.contentScrolling.statusImportance.text =
                    resources.getString(R.string.little)
            }
            Task.IMPORTANCE_BASIC -> {
                binding.contentScrolling.statusImportance.text = resources.getString(R.string.big)
            }
        }

        if (flagSave) {
            binding.contentScrolling.deleteImage.setColorFilter(resources.getColor(R.color.Gray))
            binding.contentScrolling.deleteText.setTextColor(resources.getColor(R.color.Gray))
        } else {
            binding.contentScrolling.deleteImage.setColorFilter(resources.getColor(R.color.Red))
            binding.contentScrolling.deleteText.setTextColor(resources.getColor(R.color.Red))
        }
    }

    private fun fixTextDeadline() {
        if (task.deadline != null) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = task.deadline!!
            var str: String = calendar.get(Calendar.DAY_OF_MONTH).toString()
            when (calendar.get(Calendar.MONTH)) {
                0 -> str += " " + resources.getString(R.string.month0)
                1 -> str += " " + resources.getString(R.string.month1)
                2 -> str += " " + resources.getString(R.string.month2)
                3 -> str += " " + resources.getString(R.string.month3)
                4 -> str += " " + resources.getString(R.string.month4)
                5 -> str += " " + resources.getString(R.string.month5)
                6 -> str += " " + resources.getString(R.string.month6)
                7 -> str += " " + resources.getString(R.string.month7)
                8 -> str += " " + resources.getString(R.string.month8)
                9 -> str += " " + resources.getString(R.string.month9)
                10 -> str += " " + resources.getString(R.string.month10)
                11 -> str += " " + resources.getString(R.string.month11)
            }

            str += " " + calendar.get(Calendar.YEAR)

            if (calendar.get(Calendar.HOUR) != 0 || calendar.get(Calendar.MINUTE) != 0) {
                str += "\n${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)}"
            }

            binding.contentScrolling.deadlineText.text = str
            binding.contentScrolling.switchDeadline.isChecked = true
            binding.contentScrolling.deadlineText.visibility = View.VISIBLE
        }

    }

}