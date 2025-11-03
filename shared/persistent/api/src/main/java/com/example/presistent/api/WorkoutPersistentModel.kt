package com.example.presistent.api

data class WorkoutPersistentModel(
    val name: String,
    val exerciseList: List<ExercisePersistentModel>,
    val uuid: String,
)