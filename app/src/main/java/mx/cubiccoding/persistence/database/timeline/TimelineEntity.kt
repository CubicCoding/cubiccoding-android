package mx.cubiccoding.persistence.database.timeline

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "timeline", indices = [Index(value = ["classroomName"], unique = true)])
data class TimelineEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = 0,

    @ColumnInfo(name = "classroomName")
    val classroomName: String?,

    @ColumnInfo(name = "timelineSteps")
    val timelineSteps: String?,

    @ColumnInfo(name = "timelineProgress")
    val timelineProgress: Int?
)
