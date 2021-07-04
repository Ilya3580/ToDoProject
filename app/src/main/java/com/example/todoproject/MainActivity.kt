package com.example.todoproject

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var settingButton: ImageButton
    private lateinit var calendarButton: ImageButton
    private lateinit var visibilityButton: ImageButton
    private lateinit var motionLayout: MotionLayout
    private lateinit var actionButton: FloatingActionButton
    private var adapterRecyclerView: AdapterRecyclerView? = null
    private lateinit var viewModelData: MyViewModel<Calendar>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main_vertical)
        } else {
            setContentView(R.layout.activity_main_horizontal)
        }

        recyclerView = findViewById(R.id.recyclerView)
        calendarButton = findViewById(R.id.iconCalendar)
        settingButton = findViewById(R.id.iconSetting)
        visibilityButton = findViewById(R.id.iconVisibility)
        motionLayout = findViewById(R.id.motionLayout)
        actionButton = findViewById(R.id.actionButton)
        viewModelData = MyViewModel(null)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val lst = ArrayList<InfoTask>()
        for (i in 0 until 50) {
            lst.add(InfoTask("aaa" + i, InfoTask.IMPORTANCE_NOT, InfoTask.DONE, 0L, 0L))
        }

        adapterRecyclerView = AdapterRecyclerView(lst)

        val callbackLeft: ItemTouchHelper.Callback =
            SwipeToLeftCallback(this, adapterRecyclerView as ItemTouchHelperAdapter)
        val touchHelperLeft = ItemTouchHelper(callbackLeft)
        touchHelperLeft.attachToRecyclerView(recyclerView)

        val callbackRight: ItemTouchHelper.Callback =
            SwipeToRightCallback(this, adapterRecyclerView as ItemTouchHelperAdapter)
        val touchHelperRight = ItemTouchHelper(callbackRight)
        touchHelperRight.attachToRecyclerView(recyclerView)

        recyclerView.adapter = adapterRecyclerView

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        settingButton.setOnClickListener {
        }

        calendarButton.setOnClickListener {
            FunctionsProject.onCreateAlertDialogCalendar(
                this,
                viewModelData,
                Calendar.getInstance().timeInMillis
            )
        }

        actionButton.setOnClickListener {
            val intent = Intent(this, ActivityTask::class.java)
            startActivity(intent)
        }


        viewModelData.getUsersValue().observe(this, Observer {

        })

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("flagScope", (recyclerView.adapter as AdapterRecyclerView).flagScope)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val flagScope = savedInstanceState.getBoolean("flagScope")
        if (!flagScope) {
            motionLayout.transitionToEnd()
        }
        super.onRestoreInstanceState(savedInstanceState)
    }

}


