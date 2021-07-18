package com.example.todoproject

import android.content.Context
import android.content.Intent
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todoproject.dao.DatabaseStorage
import com.example.todoproject.dao.Task
import com.example.todoproject.dao.TaskAct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.logging.Handler

class AdapterRecyclerView(
    private val values : ArrayList<Task>
        ):RecyclerView.Adapter<AdapterRecyclerView.MyViewHolder>(), ItemTouchHelperAdapter {

    var flagScope = false

    private lateinit var builder : DatabaseStorage
    private lateinit var scope : CoroutineScope
    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item,
            parent, false)

        context = parent.context
        builder = DatabaseStorage.build(context)
        scope = CoroutineScope(Dispatchers.IO)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(position == 0){
            flagScope = true
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

        holder.textTextView.text = FunctionsProject.convertDate(task, context)

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
            flagScope = false
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
            builder.taskDao().updateTask(values[position])
        }else{
            builder.taskDao().deleteTask(values[position])
        }

        builder.taskDao().insertTaskAct(TaskAct(values[position].id, actStatus))

        FunctionsProject.settingWorker(context)

    }

    private fun onClick(task : Task){
        val intent = Intent(context, ActivityTask :: class.java)
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
            builder.taskDao().updateTask(values[position])
        }
        notifyItemChanged(position)
    }

}


