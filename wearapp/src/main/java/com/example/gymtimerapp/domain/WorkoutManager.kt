package com.example.gymtimerapp.domain

import com.example.gymtimer.GymTimer
import kotlin.properties.Delegates

class WorkoutManager {

    var currentWorkout: GymTimer.WorkoutModel by Delegates.notNull()

}