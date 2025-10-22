package com.example.shared.connectivity.data

import android.os.Parcel
import android.os.Parcelable
import com.example.gymtimer.GymTimer
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.parcelableCreator
import kotlin.time.Duration.Companion.milliseconds

@Parcelize
data class WorkoutDataModel(
    val exerciseList: List<ExerciseDataModel>
) : Parcelable {

    val byteArrayPresentation: ByteArray
        get() {
            val parcel = Parcel.obtain()
            this.writeToParcel(parcel, 0)
            val byteArray = parcel.marshall()
            parcel.recycle()
            return byteArray
        }

    val domainModelPresentation: GymTimer.WorkoutModel
        get() =  GymTimer.WorkoutModel(
            exerciseList = this.exerciseList.map {
                GymTimer.ExerciseModel(
                    name = it.name,
                    numberOfSets = it.numberOfSets,
                    workDuration = it.workDurationMilliseconds.milliseconds,
                    restDuration = it.restDurationMilliseconds.milliseconds,
                    finishWorkRemainingDuration = it.finishWorkRemainingDurationMilliseconds.milliseconds,
                    finishRestRemainingDuration = it.finishRestRemainingDurationMilliseconds.milliseconds
                )
            }
        )

    companion object {
        fun GymTimer.WorkoutModel.toDataModel() = WorkoutDataModel(
            exerciseList = this.exerciseList.map {
                ExerciseDataModel(
                    name = it.name,
                    numberOfSets = it.numberOfSets,
                    workDurationMilliseconds = it.workDuration.inWholeMilliseconds,
                    restDurationMilliseconds = it.restDuration.inWholeMilliseconds,
                    finishWorkRemainingDurationMilliseconds = it.finishRestRemainingDuration.inWholeMilliseconds,
                    finishRestRemainingDurationMilliseconds = it.finishRestRemainingDuration.inWholeMilliseconds
                )
            }
        )

        fun fromByteArray(byteArray: ByteArray): WorkoutDataModel {
            val parcel = Parcel.obtain()
            parcel.unmarshall(byteArray, 0, byteArray.size)
            parcel.setDataPosition(0) // Important: Reset position to the beginning
            val parcelableObject = parcelableCreator<WorkoutDataModel>().createFromParcel(parcel)
            parcel.recycle()
            return parcelableObject
        }
    }
}

@Parcelize
data class ExerciseDataModel(
    val name: String,
    val numberOfSets: Int,
    val workDurationMilliseconds: Long,
    val restDurationMilliseconds: Long,
    val finishWorkRemainingDurationMilliseconds: Long = 0L,
    val finishRestRemainingDurationMilliseconds: Long = 0L
) : Parcelable


object ParcelableConverter {
    fun toByteArray(parcelable: Parcelable): ByteArray? {
        val parcel = Parcel.obtain()
        parcelable.writeToParcel(parcel, 0)
        val byteArray = parcel.marshall()
        parcel.recycle()
        return byteArray
    }

    // To convert back from byte array to Parcelable:
    fun <T : Parcelable?> fromByteArray(byteArray: ByteArray, creator: Parcelable.Creator<T?>): T? {
        val parcel = Parcel.obtain()
        parcel.unmarshall(byteArray, 0, byteArray.size)
        parcel.setDataPosition(0) // Important: Reset position to the beginning
        val parcelableObject = creator.createFromParcel(parcel)
        parcel.recycle()
        return parcelableObject
    }
}