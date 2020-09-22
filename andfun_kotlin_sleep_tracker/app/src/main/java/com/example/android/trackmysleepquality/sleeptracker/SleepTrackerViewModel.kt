/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {
//    Job: allow to cancel all coroutines that are started by this view model when the view model no longer
//    used
    private var viewModelJob = Job()
//    Scope: determine what thread the coroutine will run on. Our scope is in the ui because the CR will effect the ui
//    Dispatchers.Main: means that the coroutines will run on the ui thread.
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // without init this is null
    private var tonight = MutableLiveData<SleepNight>()

    // This will be null when there is no data Since the results of getAllNights is a LiveData,
    // Room will take care of update the data upon any changes in the database since allNights is LiveData.
    //
    private var allNights = database.getAllNights()

    // This code is executed every time allNights is updated
    val nightsString = Transformations.map(allNights){nights ->
        // application.resources in order to be able to access string resources in formatNights()
        formatNights(nights, application.resources)
    }

    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
    val navigateToSleepQuality : LiveData<SleepNight>
        get() = _navigateToSleepQuality

    fun doneNavigating(){
        _navigateToSleepQuality.value = null
    }

    // this will be called every time tonight is updated
    val startButtonVisible = Transformations.map(tonight){
        null == it
    }

    val stopButtonVisible = Transformations.map(tonight){
        null != it
    }

    val clearButtonVisible = Transformations.map(allNights){
        it.isNotEmpty()
    }

    private val _showSnackBar = MutableLiveData<Boolean>()
    val showSnackBar : LiveData<Boolean>
        get() = _showSnackBar

    fun showingSnackBarEnded(){
        _showSnackBar.value = false
    }


    // ============= init code =====================

    init {
        initializeToNight()
    }

    private fun initializeToNight() {
        // here we use uiScope because we want to update the ui after this call
        uiScope.launch {
            // calling this function is important so the work is done by other thread then the UI thread.
            tonight.value = getTonightFormDatabase()
        }
    }

    // this will return the night that is not ended yet.
    // returning null means there is no night that is started.
    // the suspend keyword means the the function is allowed to suspend the execution of its coroutine
    // until the code inside of it is done and while the coroutine is suspended, other sequential code
    // after the coroutine can rur.
    private suspend fun getTonightFormDatabase(): SleepNight? {
        // we are using th io dispatcher because we are getting data form the database (IO)
        // the thread of Io is optimized for such calls.
        return withContext(Dispatchers.IO){
            var night = database.getToNight()
            if(night?.endTimeMilli != night?.startTimeMilli){
                night = null
            }
            night
        }
    }

    // ======== onStart handler ================

    fun onStartTracking(){
        uiScope.launch {
            if(tonight.value == null){
                val newNight = SleepNight()
                insert(newNight)
            }
            tonight.value = getTonightFormDatabase()
        }
    }
    private suspend fun insert(newNight: SleepNight) {
        withContext(Dispatchers.IO){
            database.insert(newNight)
        }
    }

    // ========= onStop handler =============

    fun onStopTracking(){
        uiScope.launch {
            // oldNight will be null if there is no started night
            // return@launch returns form the inner fun (launch) and not the outer one (onStopTracking)
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            tonight.value = null
            _navigateToSleepQuality.value = oldNight
        }
    }

    private suspend fun update(oldNight: SleepNight) {
        withContext(Dispatchers.IO){
            database.update(oldNight)
        }
    }

    // =========== onClear ===============
    fun onClear(){
        uiScope.launch {
            clear()
            tonight.value = null
            _showSnackBar.value = true
        }
    }

    private suspend fun clear(){
        withContext(Dispatchers.IO){
            database.clear()
        }
    }

// This a pattern used to get stuff form the database without blocking the current thread.
/*
    fun foo(){
        uiScope.launch {
            foo2(newNight)
        }
    }

    private suspend fun foo2() {
        withContext(Dispatchers.IO){
            database.foo3()
        }
    }*/


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

