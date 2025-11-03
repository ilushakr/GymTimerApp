package com.example.gymtimerapp.presentation.navigationhost

import androidx.lifecycle.ViewModel
import com.example.gymtimerapp.navigation.BottomNavigationItem
import com.example.gymtimerapp.navigation.NavigationManager
import com.example.gymtimerapp.navigation.routeName

class NavigationHostViewModel(private val navigationManager: NavigationManager) : ViewModel() {
    val navigationEvent = navigationManager.navigationFlow
    val startDestination = navigationManager.startScreen.routeName
    val tabItemStartScreen = navigationManager.tabItemStartScreen.routeName

    fun navigate(item: BottomNavigationItem) {
        navigationManager.navigateToBottomItem(item)
    }
}