package com.example.android.guesstheword.screens.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel

class ScoreViewModel (finalScore: Int): ViewModel() {

    // _finalScore at the moment is null
    private val _finalScore = MutableLiveData<Int>()
    val finalScore: LiveData<Int>
        get() = _finalScore

    private val _eventPlayAgain = MutableLiveData<Boolean>()
    val eventPlayAgain : LiveData<Boolean>
        get() = _eventPlayAgain

    init {
        _finalScore.value = finalScore
        _eventPlayAgain.value = false
    }

    fun playAgain(){
        _eventPlayAgain.value = true
    }

    fun onEventPlayAgain(){
        _eventPlayAgain.value = false
    }

}