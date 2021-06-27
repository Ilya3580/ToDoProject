package com.example.todoproject

import android.icu.text.IDNA
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.todoproject.databinding.ActivityTaskBinding

class ActivityTask : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    private lateinit var task : InfoTask
    private var flagSave = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val argument = intent.extras
        val strTask = argument?.get("task")

        task = if(strTask == null) {
            flagSave = false
            InfoTask()
        }else{
            flagSave = true
            fixTask()
            fixTask()
            InfoTask.parsString(strTask.toString()) ?: InfoTask()
        }

        binding.closeImageButton.setOnClickListener {
            onBackPressed()
        }

        binding.contentScrolling.importanceContainer.setOnClickListener {
            showMenu(binding.contentScrolling.importanceContainer)
        }

    }

    fun showMenu(view: View?) {
        val menu = PopupMenu(this, view)
        menu.setOnMenuItemClickListener({ item: MenuItem? ->
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
        })
        menu.inflate(R.menu.menu_importance)
        menu.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("task", task.toString())
        outState.putBoolean("flagSave", flagSave)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val task = savedInstanceState.getString("task")
        this.task = InfoTask.parsString(task.toString()) ?: InfoTask()
        flagSave = savedInstanceState.getBoolean("flagSave")
        super.onRestoreInstanceState(savedInstanceState)
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
}