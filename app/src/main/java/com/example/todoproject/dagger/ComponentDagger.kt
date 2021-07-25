package com.example.todoproject.dagger

import android.content.Context
import com.example.todoproject.TaskActivity
import com.example.todoproject.MainActivity
import com.example.todoproject.view.recyclerview.AdapterRecyclerView
import com.example.todoproject.worker_and_receiver.SynchronizeWorker
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ModuleDagger::class])
interface ComponentDagger {
    fun inject(activity: MainActivity)
    fun inject(activity: TaskActivity)
    fun inject(adapter: AdapterRecyclerView)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ComponentDagger
    }
}