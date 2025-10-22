package com.example.presistent.api

import kotlin.time.Duration

data class ExercisePersistentModel(
    val name: String,
    val numberOfSets: Int,
    val workDuration: Duration,
    val restDuration: Duration,
    val finishWorkRemainingDuration: Duration = Duration.ZERO,
    val finishRestRemainingDuration: Duration = Duration.ZERO,
    val uuid: String
)