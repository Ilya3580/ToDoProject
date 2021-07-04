package com.example.todoproject

import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.todoproject.databinding.ActivityTaskBinding
import java.util.*

class ActivityTask : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    private lateinit var task : InfoTask
    private var flagSave = false

    private lateinit var viewModelCalendar : MyViewModel<Calendar>
    private lateinit var viewModelTime : MyViewModel<Calendar>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelCalendar = MyViewModel(null)
        viewModelTime = MyViewModel(null)

        if(savedInstanceState != null){
            val task = savedInstanceState.getString("task")
            this.task = InfoTask.parsString(task.toString()) ?: InfoTask()
            flagSave = savedInstanceState.getBoolean("flagSave")
        }else{
            val strTask = intent.extras?.get("task")
            task = if(strTask == null) {
                flagSave = false
                InfoTask()
            }else{
                flagSave = true
                InfoTask.parsString(strTask.toString()) ?: InfoTask()
            }
        }

        fixTask()

        binding.closeImageButton.setOnClickListener {
            onBackPressed()
        }

        binding.saveTextView.setOnClickListener{
            saveTask()
        }

        binding.contentScrolling.importanceContainer.setOnClickListener {
            showMenu(binding.contentScrolling.importanceContainer)
        }

        binding.contentScrolling.deadlineContainer.setOnClickListener {
            if(!binding.contentScrolling.switchDeadline.isChecked) {
                FunctionsProject.onCreateAlertDialogCalendar(
                    this,
                    viewModelCalendar,
                    Calendar.getInstance().time.time
                )
            }
        }

        binding.contentScrolling.switchDeadline.setOnClickListener{
            if(binding.contentScrolling.switchDeadline.isChecked){
                FunctionsProject.onCreateAlertDialogCalendar(this, viewModelCalendar, Calendar.getInstance().time.time)
            }else{
                binding.contentScrolling.deadlineText.visibility = View.GONE
            }
        }

        viewModelCalendar.getUsersValue().observe(this, {
            if(viewModelCalendar.user != null){
                task.deadline = it.timeInMillis
                fixTextDeadline()
                FunctionsProject.onCreateAlertDialogTime(this, viewModelTime,
                    task.deadline!!)
            }
        })

        viewModelTime.getUsersValue().observe(this, {
            if(it != null) {
                task.deadline = it.timeInMillis
                fixTextDeadline()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("task", task.toString())
        outState.putBoolean("flagSave", flagSave)
        super.onSaveInstanceState(outState)
    }

    private fun showMenu(view: View?) {
        val menu = PopupMenu(this, view)
        menu.setOnMenuItemClickListener{ item: MenuItem? ->
            when(item?.itemId){
                R.id.item_not -> {
                    task.status = InfoTask.IMPORTANCE_NOT
                }
                R.id.item_little -> {
                    task.status = InfoTask.IMPORTANCE_LITLE
                }

                R.id.item_big -> {
                    task.status = InfoTask.IMPORTANCE_BIG
                }
            }
            false
        }
        menu.inflate(R.menu.menu_importance)
        menu.show()
    }

    private fun fixTask(){
        binding.contentScrolling.editTextTask.setText(task.textTitle)
        when(task.status){
            InfoTask.IMPORTANCE_NOT ->
                {binding.contentScrolling.statusImportance.text = resources.getString(R.string.no)}
            InfoTask.IMPORTANCE_LITLE ->
                {binding.contentScrolling.statusImportance.text = resources.getString(R.string.little)}
            InfoTask.IMPORTANCE_BIG ->
                {binding.contentScrolling.statusImportance.text = resources.getString(R.string.big)}
        }

        if(flagSave){
            binding.contentScrolling.deleteImage.setColorFilter(resources.getColor(R.color.Gray))
            binding.contentScrolling.deleteText.setTextColor(resources.getColor(R.color.Gray))
        }else{
            binding.contentScrolling.deleteImage.setColorFilter(resources.getColor(R.color.Red))
            binding.contentScrolling.deleteText.setTextColor(resources.getColor(R.color.Red))
        }
    }

    private fun fixTextDeadline(){
        if(task.deadline != null) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = task.deadline!!
            var str : String = calendar.get(Calendar.DAY_OF_MONTH).toString()
            when(calendar.get(Calendar.MONTH)){
                0 -> str+=" " + resources.getString(R.string.month0)
                1 -> str+=" " + resources.getString(R.string.month1)
                2 -> str+=" " + resources.getString(R.string.month2)
                3 -> str+=" " + resources.getString(R.string.month3)
                4 -> str+=" " + resources.getString(R.string.month4)
                5 -> str+=" " + resources.getString(R.string.month5)
                6 -> str+=" " + resources.getString(R.string.month6)
                7 -> str+=" " + resources.getString(R.string.month7)
                8 -> str+=" " + resources.getString(R.string.month8)
                9 -> str+=" " + resources.getString(R.string.month9)
                10 -> str+=" " + resources.getString(R.string.month10)
                11 -> str+=" " + resources.getString(R.string.month11)
            }

            str+=" "+calendar.get(Calendar.YEAR)

            if(calendar.get(Calendar.HOUR) != 0 || calendar.get(Calendar.MINUTE) != 0)
            {
                str+="\n${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)}"
            }

            binding.contentScrolling.deadlineText.text = str
        }

//        if(binding.contentScrolling.deadlineText.visibility == View.GONE){
//            binding.contentScrolling.deadlineText.visibility = View.VISIBLE
//            fixTask()
//        }
    }

    private fun saveTask(){

    }
}