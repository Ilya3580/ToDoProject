package com.example.todoproject.view.recyclerview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todoproject.App
import com.example.todoproject.TaskActivity
import com.example.todoproject.R
import com.example.todoproject.database.DatabaseStorage
import com.example.todoproject.database.Task
import com.example.todoproject.database.TaskAct
import com.example.todoproject.network.APIService
import com.example.todoproject.view.date.CalendarFunction
import com.example.todoproject.viewmodel.MainViewModel
import com.example.todoproject.worker_and_receiver.SynchronizeWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class AdapterRecyclerView(
    var values : ArrayList<Task>,
    var mainViewModel : MainViewModel,
    var buildDatabase: DatabaseStorage,
        ):RecyclerView.Adapter<AdapterRecyclerView.MyViewHolder>(), ItemTouchHelperAdapter {

    private lateinit var context : Context
    private lateinit var scope : CoroutineScope


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_view_item,
            parent, false)
        context = parent.context
        scope = CoroutineScope(Dispatchers.IO)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(position == 0){
            mainViewModel.flagScope = false
        }

        holder.textTitleTextView.text = values[position].text

        val date = Calendar.getInstance().timeInMillis
        val task = values[position]
        if(task.done == Task.DONE){
            holder.statusTask.setImageResource(R.drawable.ic_done)
        }else{
            if(task.deadline!! < date){
                holder.statusTask.setImageResource(R.drawable.ic_unchecked_red)
            }else{
                holder.statusTask.setImageResource(R.drawable.ic_unchecked)
            }
        }

        holder.textTextView.text = CalendarFunction.convertDate(task, context)

        holder.textTitleTextView.setOnClickListener {
            onClick(values[position])
        }
        holder.textTextView.setOnClickListener {
            onClick(values[position])
        }
        holder.statusTask.setOnClickListener {
            taskStatusClick(position)
        }
        holder.infoImageButton.setOnClickListener {
            onClick(values[position])
        }
    }

    override fun getItemCount(): Int = values.count()

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        if(holder.adapterPosition == 0){
            mainViewModel.flagScope = true
        }
        super.onViewDetachedFromWindow(holder)
    }



    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var textTitleTextView : TextView = itemView.findViewById(R.id.textTitleTextView)
        var textTextView : TextView = itemView.findViewById(R.id.textTextView)
        var statusTask : ImageButton = itemView.findViewById(R.id.statusTask)
        var infoImageButton : ImageButton = itemView.findViewById(R.id.infoImageButton)
    }

    override fun onSwipeLeft(position: Int) {
        scope.launch {
            updateInformationTask(position, TaskAct.ACT_DELETE)
            values.removeAt(position)
            scope.launch(Dispatchers.Main) {
                notifyItemRemoved(position)
            }
        }
    }

    override fun onSwipeRight(position: Int) {
        taskStatusClick(position)
        notifyItemChanged(position)
        scope.launch {
            updateInformationTask(position, TaskAct.ACT_UPDATE)
        }
    }

    private suspend fun updateInformationTask(position: Int, actStatus : Int){
        if(actStatus == TaskAct.ACT_UPDATE) {
            buildDatabase.taskDao().updateTask(values[position])
        }else{
            buildDatabase.taskDao().deleteTask(values[position])
        }

        buildDatabase.taskDao().insertTaskAct(TaskAct(values[position].id, actStatus))

    }

    private fun onClick(task : Task){
        val intent = Intent(context, TaskActivity :: class.java)
        intent.putExtra("task", task)
        context.startActivity(intent)
    }

    private fun taskStatusClick(position: Int){
        if(values[position].done == Task.DONE){
            values[position].done = Task.NOT_DONE
        }else{
            values[position].done = Task.DONE
        }

        scope.launch {
            buildDatabase.taskDao().updateTask(values[position])
        }
        notifyItemChanged(position)
    }

}


