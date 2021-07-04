package com.example.todoproject

import java.lang.Exception

class InfoTask(var textTitle : String = "", var importance : Int = IMPORTANCE_NOT,
               var status : Int = DONE, var deadline : Long? = null,
               var stopDeadline : Long? = null) {
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
                InfoTask(mas[0], mas[2].toInt(), mas[3].toInt(), mas[4].toLong(), mas[5].toLong())
            }catch (e : Exception){
                null
            }
        }
    }

    override fun toString(): String {
        return textTitle+"$"+importance+"$"+status+"$"+deadline+"$"+stopDeadline
    }

}