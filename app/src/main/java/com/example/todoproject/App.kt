package com.example.todoproject

import android.app.Application
import com.example.todoproject.dagger.ComponentDagger
import com.example.todoproject.dagger.DaggerComponentDagger
import dagger.Component

class App : Application(){
    private var componentDagger : ComponentDagger? = null
    var flagUpdateData = false

    fun getComponent() : ComponentDagger{
        if(componentDagger == null){
            componentDagger = DaggerComponentDagger.factory().create(applicationContext)
        }
        return componentDagger!!
    }

}