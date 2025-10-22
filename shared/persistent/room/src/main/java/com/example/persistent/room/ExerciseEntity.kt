package com.example.persistent.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.presistent.api.ExercisePersistentModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

@Serializable
@Entity
internal data class ExerciseEntity(
//    @SerialName("PrimaryKey")
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,

    @SerialName("name")
    @ColumnInfo(name = "name")
    val name: String,

    @SerialName("numberOfSets")
    @ColumnInfo(name = "numberOfSets")
    val numberOfSets: Int,

    @SerialName("workDurationMilliseconds")
    @ColumnInfo(name = "workDurationMilliseconds")
    val workDurationMilliseconds: Long,

    @SerialName("restDurationMilliseconds")
    @ColumnInfo(name = "restDurationMilliseconds")
    val restDurationMilliseconds: Long,

    @SerialName("finishWorkRemainingDurationMilliseconds")
    @ColumnInfo(name = "finishWorkRemainingDurationMilliseconds")
    val finishWorkRemainingDurationMilliseconds: Long = 0L,

    @SerialName("finishRestRemainingDurationMilliseconds")
    @ColumnInfo(name = "finishRestRemainingDurationMilliseconds")
    val finishRestRemainingDurationMilliseconds: Long = 0L,

    @SerialName("uuid")
    @ColumnInfo(name = "uuid")
    val uuid: String,
) {
    val domainModel: ExercisePersistentModel
        get() = ExercisePersistentModel(
            name = this.name,
            numberOfSets = this.numberOfSets,
            workDuration = this.workDurationMilliseconds.milliseconds,
            restDuration = this.restDurationMilliseconds.milliseconds,
            finishWorkRemainingDuration = this.finishWorkRemainingDurationMilliseconds.milliseconds,
            finishRestRemainingDuration = this.finishRestRemainingDurationMilliseconds.milliseconds,
            uuid = this.uuid
        )

    companion object {
        fun ExercisePersistentModel.toDataModel() = ExerciseEntity(
            name = this.name,
            numberOfSets = this.numberOfSets,
            workDurationMilliseconds = this.workDuration.inWholeMilliseconds,
            restDurationMilliseconds = this.restDuration.inWholeMilliseconds,
            finishWorkRemainingDurationMilliseconds = this.finishRestRemainingDuration.inWholeMilliseconds,
            finishRestRemainingDurationMilliseconds = this.finishRestRemainingDuration.inWholeMilliseconds,
            uuid = this.uuid
        )
    }
}
