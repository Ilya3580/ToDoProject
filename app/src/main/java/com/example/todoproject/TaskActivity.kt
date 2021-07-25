package com.example.todoproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.todoproject.database.DatabaseStorage
import com.example.todoproject.databinding.ActivityTaskBinding
import com.example.todoproject.database.Task
import com.example.todoproject.database.TaskAct
import com.example.todoproject.network.APIService
import com.example.todoproject.view.alert.FunctionAlertDialog
import com.example.todoproject.view.date.CalendarFunction
import com.example.todoproject.viewmodel.TaskActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class TaskActivity : AppCompatActivity() {
    @Inject
    lateinit var buildDatabase : DatabaseStorage

    private lateinit var binding: ActivityTaskBinding

    private val viewModel : TaskActivityViewModel by viewModels()

    private var flagShowCalendar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as App).getComponent().inject(this)

        if(viewModel.task == null){
            viewModel.task = intent.getSerializableExtra("task") as? Task ?: Task(UUID.randomUUID().toString())
        }

        if(viewModel.flagSave == null){
            viewModel.flagSave  = intent.getSerializableExtra("task") != null
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
            flagShowCalendar = false
            CalendarFunction.onCreateAlertDialogCalendar(
                this,
                viewModel,
                Calendar.getInstance().time.time
            )
        }

        binding.contentScrolling.switchDeadline.setOnClickListener {
            clickSwitch()
        }

        binding.contentScrolling.deleteContainer.setOnClickListener {
            clickDelete()
        }

        viewModel.calendarLivedata.observe(this, {
            if(!flagShowCalendar){
                flagShowCalendar = true
                CalendarFunction.onCreateAlertDialogCalendar(
                    this,
                    viewModel,
                    Calendar.getInstance().time.time
                )
            }
            fixTextDeadline()
        })
    }

    private fun showMenu(view: View?) {
        val menu = PopupMenu(this, view)
        menu.setOnMenuItemClickListener { item: MenuItem? ->
            when (item?.itemId) {
                R.id.item_not -> {
                    viewModel.task?.importance = Task.IMPORTANCE
                    binding.contentScrolling.statusImportance.text =
                        resources.getString(R.string.no)
                }
                R.id.item_little -> {
                    viewModel.task?.importance = Task.IMPORTANCE_LOW
                    binding.contentScrolling.statusImportance.text =
                        resources.getString(R.string.little)
                }

                R.id.item_big -> {
                    viewModel.task?.importance = Task.IMPORTANCE_BASIC
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
        binding.contentScrolling.editTextTask.setText(viewModel.task?.text)
        when (viewModel.task?.importance) {
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

        if (viewModel.flagSave!!) {
            binding.contentScrolling.deleteImage.setColorFilter(resources.getColor(R.color.Red))
            binding.contentScrolling.deleteText.setTextColor(resources.getColor(R.color.Red))
        } else {
            binding.contentScrolling.deleteImage.setColorFilter(resources.getColor(R.color.Gray))
            binding.contentScrolling.deleteText.setTextColor(resources.getColor(R.color.Gray))
        }
    }

    private fun fixTextDeadline() {
        if (viewModel.task?.deadline != null) {
            binding.contentScrolling.deadlineText.text =
                CalendarFunction.convertDate(viewModel.task!!, applicationContext)
            binding.contentScrolling.switchDeadline.isChecked = true
            binding.contentScrolling.deadlineText.visibility = View.VISIBLE
        }

    }

    private fun clickSaveTextView() {
        val alert = FunctionAlertDialog.startProgressBar(this)
        var taskAct = TaskAct(viewModel.task!!.id, TaskAct.ACT_UPDATE)
        viewModel.task?.text = binding.contentScrolling.editTextTask.text.toString()
        viewModel.task?.update_at = Calendar.getInstance().time.time
        if (!viewModel.flagSave!!) {
            viewModel.task?.created_at = Calendar.getInstance().time.time
            taskAct.act = TaskAct.ACT_ADD
        }
        if (viewModel.task?.deadline == null) {
            viewModel.task?.deadline = Calendar.getInstance().timeInMillis
        }

        viewModel.task!!.update_at = viewModel.task!!.update_at!!/1000
        viewModel.task!!.deadline = viewModel.task!!.deadline!!/1000
        viewModel.task!!.created_at = viewModel.task!!.created_at!!/1000

        lifecycleScope.launch(Dispatchers.IO) {
            buildDatabase.taskDao().insertTaskAct(taskAct)
            buildDatabase.taskDao().insertTask(viewModel.task!!)
            startMainActivity(alert)
        }
    }

    private fun clickSwitch() {
        if (binding.contentScrolling.switchDeadline.isChecked) {
            CalendarFunction.onCreateAlertDialogCalendar(
                this,
                viewModel,
                Calendar.getInstance().time.time
            )
        } else {
            viewModel.task?.deadline = null
            binding.contentScrolling.deadlineText.visibility = View.GONE
        }
    }

    private fun clickDelete() {
        if (viewModel.flagSave!!) {
            val alert = FunctionAlertDialog.startProgressBar(this)
            lifecycleScope.launch(Dispatchers.IO) {
                val taskAct = TaskAct(viewModel.task!!.id, TaskAct.ACT_DELETE)
                buildDatabase.taskDao().insertTaskAct(taskAct)
                buildDatabase.taskDao().deleteTask(viewModel.task!!)
                startMainActivity(alert)
            }
        }
    }

    private fun startMainActivity(alert: AlertDialog) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            alert.dismiss()
            val intent = Intent(this@TaskActivity, MainActivity::class.java)
            intent.putExtra("idName", viewModel.task?.id)
            startActivity(intent)

        }
    }
}