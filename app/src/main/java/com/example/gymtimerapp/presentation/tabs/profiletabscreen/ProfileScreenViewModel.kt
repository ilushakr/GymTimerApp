package com.example.gymtimerapp.presentation.tabs.profiletabscreen

import androidx.lifecycle.ViewModel
import com.example.gymtimerapp.navigation.NavigationManager

class ProfileScreenViewModel(private val navigationManager: NavigationManager) : ViewModel() {
    fun showSnackBar() {
        navigationManager.showSnackBar("I've told ya. Kitten is SAD!!")
    }
}