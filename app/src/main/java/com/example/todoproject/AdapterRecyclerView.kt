package com.example.todoproject

import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dao.DatabaseStorage
import dao.Task
import dao.TaskAct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AdapterRecyclerView(
    private val values : List<Task>
        ):RecyclerView.Adapter<AdapterRecyclerView.MyViewHolder>(), ItemTouchHelperAdapter {

    var flagScope = false
    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item,
            parent, false)

        context = parent.context

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
    }

    override fun getItemCount(): Int = values.count()

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        if(holder.adapterPosition == 0){
            flagScope = false
        }
        super.onViewDetachedFromWindow(holder)
    }



    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var textTitleTextView : TextView
        var textTextView : TextView
        var statusTask : ImageButton
        var infoImageButton : ImageButton
        init {
            textTitleTextView = itemView.findViewById(R.id.textTitleTextView)
            textTextView = itemView.findViewById(R.id.textTextView)
            statusTask = itemView.findViewById(R.id.statusTask)
            infoImageButton = itemView.findViewById(R.id.infoImageButton)
        }


    }

    override fun onSwipeLeft(position: Int) {
        notifyItemChanged(position)
        updateInformationTask(position, TaskAct.ACT_DELETE)
    }

    override fun onSwipeRight(position: Int) {
        notifyItemChanged(position)
        updateInformationTask(position, TaskAct.ACT_UPDATE)
    }

    private fun updateInformationTask(position: Int, actStatus : Int){
        val builder = DatabaseStorage.build(context)
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            if(actStatus == TaskAct.ACT_UPDATE) {
                builder.taskDao().updateTask(values[position])
            }else{
                builder.taskDao().deleteTask(values[position])
            }

            builder.taskDao().insertTaskAct(TaskAct(values[position].id, actStatus))
        }
    }

}


