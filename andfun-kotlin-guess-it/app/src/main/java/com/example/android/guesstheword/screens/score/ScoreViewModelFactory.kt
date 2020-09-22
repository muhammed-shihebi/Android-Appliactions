package com.example.android.guesstheword.screens.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ScoreViewModelFactory (private val finalScore: Int): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        // modelClass.isAssignableFrom() determines if the class or interface represented by this
        // Class object (modelClass) is either the same as, or is a superclass or superinterface of,
        // the class or interface represented by the specified Class parameter (ScoreViewModel::class.java).
        if (modelClass.isAssignableFrom(ScoreViewModel::class.java)){
            return ScoreViewModel(finalScore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}