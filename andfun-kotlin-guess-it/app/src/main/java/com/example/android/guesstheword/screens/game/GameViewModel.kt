package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

enum class BuzzType(val pattern: LongArray) {
    // you can use a pattern using: BuzzType.CORRECT.pattern
    // So this means BuzzType(val pattern: LongArray): the enum stuff should be defined this way.
    CORRECT(CORRECT_BUZZ_PATTERN),
    GAME_OVER(GAME_OVER_BUZZ_PATTERN),
    COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
    NO_BUZZ(NO_BUZZ_PATTERN)
}

class GameViewModel : ViewModel(){

    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 10000L
    }


    // encapsulating concept
    // Why not just use val _word and a privet set? Because the value of the LiveData is still mutable.
    private val _word = MutableLiveData<String>()
    // unlike mutableLiveData you can't set the value of it. So something like word.value = "cat" is
    // not allowed even from inside this class.
    val word : LiveData<String>
        get() = _word

    private val _score = MutableLiveData<Int>()
    val score : LiveData<Int>
        get() = _score

    // this will make the game finished event observable using this boolean
    // all events should be done this way
    private val _eventGameFinished = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinished

    // this val will track the buzz conditions
    private val _currentBuzzType = MutableLiveData<BuzzType>()
    val currentBuzzType : LiveData<BuzzType>
        get() = _currentBuzzType
    private val _shouldBuzz = MutableLiveData<Boolean>()
    val shouldBuzz : LiveData<Boolean>
        get() = _shouldBuzz


    private val timer: CountDownTimer
    private val _currentTime = MutableLiveData<Long>()
    val currentTime : LiveData<Long>
        get() = _currentTime

    // This will let the layout observe the current time but not the actual one but the transformed one
    // each time the actual one is changed the transformed one will also change.
    val currentTimeString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time/1000)
    }

    private lateinit var wordList: MutableList<String>

    init {
        Log.i("GameViewModel", "GameViewModel created")
        _score.value = 0
        _word.value = ""
        _eventGameFinished.value = false
        _currentTime.value = COUNTDOWN_TIME
        _currentBuzzType.value = BuzzType.NO_BUZZ
        _shouldBuzz.value = false
        resetList()
        nextWord()

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished
                if(millisUntilFinished <= ONE_SECOND * 5){
                    _currentBuzzType.value = BuzzType.COUNTDOWN_PANIC
                    _shouldBuzz.value = true
                }
            }
            override fun onFinish() {
                _currentTime.value = DONE
                _eventGameFinished.value = true
                _currentBuzzType.value = BuzzType.GAME_OVER
                _shouldBuzz.value = true
            }
        }
        timer.start()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        }
        _word.value = wordList.removeAt(0)
    }

    /** Methods for buttons presses **/
    fun onSkip() {
        _score.value = _score.value?.let { it - 1 }
        nextWord()
    }
    fun onCorrect() {
        _score.value = _score.value?.let { it + 1 }
        nextWord()
        _currentBuzzType.value = BuzzType.CORRECT
        _shouldBuzz.value = true
    }

    fun onGameFinished(){
        _eventGameFinished.value = false
    }

    fun onBuzzFinished(){
        _currentBuzzType.value = BuzzType.NO_BUZZ
        _shouldBuzz.value = false
    }

    // uncleared callback is called when the fragment or the activity that this view model is
    // associated with has got destroyed.
    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed")
        timer.cancel()
    }
}