package com.courses.ecommerceapp.viewmodel.launchapp

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.courses.ecommerceapp.util.Constants.Companion.INTRODUCTION_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _navigateState = MutableStateFlow(0)
    val navigateState: StateFlow<Int> = _navigateState

    companion object {
        const val SHOPPING_ACTIVITY = 10
        const val ACCOUNT_OPTION_FRAGMENT = 20
    }

    init {
        val isButtonClicked = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser

        if (user != null) {
            //go to the intro to shopping
            viewModelScope.launch {
                _navigateState.emit(SHOPPING_ACTIVITY)
            }
        } else if (isButtonClicked) {
            //go to the intro to account option
            viewModelScope.launch {
                _navigateState.emit(ACCOUNT_OPTION_FRAGMENT)
            }

        } else {
            Unit
        }
    }

    fun startButtonClick() {
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY, true)
    }

}