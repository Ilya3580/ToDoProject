package com.example.todoproject

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class AdapterRecyclerView(
    private val values : ArrayList<InfoTask>
        ):RecyclerView.Adapter<AdapterRecyclerView.MyViewHolder>(), ItemTouchHelperAdapter {

    var flagScope = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item,
            parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(position == 0){
            flagScope = true
        }
        holder.textTitleTextView.text = values[position].textTitle
        when(values[position].status){
            InfoTask.DONE -> holder.checkOrUncheckImageButton.setImageResource(R.drawable.ic_done)
            InfoTask.OVERDUE -> holder.checkOrUncheckImageButton.setImageResource(R.drawable.ic_unchecked_red)
            InfoTask.FAILED -> holder.checkOrUncheckImageButton.setImageResource(R.drawable.ic_unchecked)
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
        var textTitleTextView : TextView
        var textTextView : TextView
        var checkOrUncheckImageButton : ImageButton
        var infoImageButton : ImageButton
        init {
            textTitleTextView = itemView.findViewById(R.id.textTitleTextView)
            textTextView = itemView.findViewById(R.id.textTextView)
            checkOrUncheckImageButton = itemView.findViewById(R.id.checkOrUncheckImageButton)
            infoImageButton = itemView.findViewById(R.id.infoImageButton)
        }


    }

    override fun onSwipeLeft(position: Int) {

    }

    override fun onSwipeRight(position: Int) {

    }

}


