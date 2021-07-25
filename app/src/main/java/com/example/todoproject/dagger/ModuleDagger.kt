package com.example.todoproject.dagger

import android.content.Context
import com.example.todoproject.database.DatabaseStorage
import com.example.todoproject.network.APIService
import com.example.todoproject.worker_and_receiver.SynchronizeWorker
import dagger.Module
import dagger.Provides

@Module
class ModuleDagger {

    @Provides
    fun providesAPIService() = APIService.userService()

    @Provides
    fun providesBuilderRoom(context: Context) = DatabaseStorage.build(context)

    @Provides
    fun providesOneTimeWorkerSetting() = SynchronizeWorker.oneTimeWorkerSetting()

    @Provides
    fun providesPeriodTimeWorkerSetting() = SynchronizeWorker.periodTimeWorkSetting()

}