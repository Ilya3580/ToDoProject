package dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "task"
)
data class Task(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id : String,
    @ColumnInfo(name = "text")
    var text: String = "",
    @ColumnInfo(name = "importance")
    var importance: String = IMPORTANCE,
    @ColumnInfo(name = "done")
    var done: Boolean = NOT_DONE,
    @ColumnInfo(name = "deadline")
    var deadline: Long? = null,
    @ColumnInfo(name = "created")
    var created_at: Long? = null,
    @ColumnInfo(name = "update")
    var update_at: Long? = null,
) : Serializable{
    companion object {
        const val DONE = true
        const val NOT_DONE = false

        const val IMPORTANCE = "important"
        const val IMPORTANCE_LOW = "low"
        const val IMPORTANCE_BASIC = "basic"
    }
}


