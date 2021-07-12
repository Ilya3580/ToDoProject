package com.example.todoproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
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
    private lateinit var builder: DatabaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelCalendar = MutableLiveData(null)
        viewModelTime = MutableLiveData(null)
        builder = DatabaseStorage.build(applicationContext)

        task = if (savedInstanceState?.getSerializable("task") != null) {
            flagSave = savedInstanceState.getSerializable("flagSave") as Boolean
            savedInstanceState.getSerializable("task") as Task
        } else {
            flagSave = intent.getSerializableExtra("task") != null
            intent.getSerializableExtra("task") as? Task ?: Task(UUID.randomUUID().toString())
        }

        fixTask()

        binding.closeImageButton.setOnClickListener {
            onBackPressed()
        }

        binding.saveTextView.setOnClickListener {
            clickSaveTextView()
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
            clickSwitch()
        }

        binding.contentScrolling.deleteContainer.setOnClickListener {
            clickDelete()
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
            binding.contentScrolling.deleteImage.setColorFilter(resources.getColor(R.color.Red))
            binding.contentScrolling.deleteText.setTextColor(resources.getColor(R.color.Red))
        } else {
            binding.contentScrolling.deleteImage.setColorFilter(resources.getColor(R.color.Gray))
            binding.contentScrolling.deleteText.setTextColor(resources.getColor(R.color.Gray))
        }
    }

    private fun fixTextDeadline() {
        if (task.deadline != null) {
            binding.contentScrolling.deadlineText.text =
                FunctionsProject.convertDate(task, applicationContext)
            binding.contentScrolling.switchDeadline.isChecked = true
            binding.contentScrolling.deadlineText.visibility = View.VISIBLE
        }

    }

    private fun clickSaveTextView() {
        val builder = DatabaseStorage.build(applicationContext)
        val alert = FunctionsProject.startProgressBar(this)

        task.text = binding.contentScrolling.editTextTask.text.toString()
        task.update_at = Calendar.getInstance().time.time
        if (!flagSave) {
            task.created_at = Calendar.getInstance().time.time
        }
        if (task.deadline == null) {
            task.deadline = Calendar.getInstance().timeInMillis
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val taskAct = TaskAct(task.id, TaskAct.ACT_ADD)
            builder.taskDao().insertTaskAct(taskAct)
            builder.taskDao().insertTask(task)
            startMainActivity(alert)
        }
    }

    private fun clickSwitch() {
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

    private fun clickDelete() {
        if (flagSave) {
            val alert = FunctionsProject.startProgressBar(this)
            lifecycleScope.launch(Dispatchers.IO) {
                val taskAct = TaskAct(task.id, TaskAct.ACT_DELETE)
                builder.taskDao().insertTaskAct(taskAct)
                builder.taskDao().deleteTask(task)
                startMainActivity(alert)
            }
        }
    }

    private fun startMainActivity(alert: AlertDialog) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            FunctionsProject.stopProgressBar(alert)
            val intent = Intent(this@ActivityTask, MainActivity::class.java)
            intent.putExtra("idName", task.id)
            startActivity(intent)

        }
    }

}