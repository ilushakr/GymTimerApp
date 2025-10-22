package com.example.persistent.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.persistent.room.ExerciseEntity.Companion.toDataModel
import com.example.presistent.api.WorkoutPersistentModel

@Entity
internal data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "workoutName") val workoutName: String,
    @ColumnInfo(name = "exerciseList") val exerciseList: List<ExerciseEntity>
) {
    val domainModel: WorkoutPersistentModel
        get() = WorkoutPersistentModel(
            name = this.workoutName,
            exerciseList = this.exerciseList.map { it.domainModel }
        )

    companion object {
        fun WorkoutPersistentModel.toDataModel() = WorkoutEntity(
            uid = this.hashCode(),
            workoutName = this.name,
            exerciseList = this.exerciseList.map { it.toDataModel() }
        )
    }
}
