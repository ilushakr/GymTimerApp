package com.example.gymtimerapp.presentation.tabs.settingstabscreen

import androidx.lifecycle.ViewModel
import com.example.gymtimerapp.navigation.NavigationManager

class SettingsScreenViewModel(private val navigationManager: NavigationManager) : ViewModel() {

    fun showSnackBar() {
        navigationManager.showSnackBar("I've already told ya. Kitten is SAD!!")
    }

}