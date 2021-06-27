package com.example.todoproject

import java.lang.Exception

class InfoTask(var textTitle : String = "", var importance : Int = IMPORTANCE_NOT,
               var status : Int = DONE, var deadline : String? = null, var stopDeadline : String? = null) {
    companion object{
        val DONE = 1
        val FAILED = 2
        val OVERDUE = 3

        val IMPORTANCE_NOT = 4
        val IMPORTANCE_LITLE = 5
        val IMPORTANCE_BIG = 6

        public fun parsString(str : String) : InfoTask?{
            return try{
                val mas = str.split("$")
                InfoTask(mas[0], mas[2].toInt(), mas[3].toInt(), mas[4], mas[5])
            }catch (e : Exception){
                null
            }
        }
    }

    override fun toString(): String {
        return textTitle+"$"+importance+"$"+status+"$"+deadline+"$"+stopDeadline
    }

}