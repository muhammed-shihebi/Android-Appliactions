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

package com.example.android.trackmysleepquality.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*

class SleepQualityViewModel(val sleepNightKey: Long, val databaseDao: SleepDatabaseDao) : ViewModel(){
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var sleepNight = SleepNight()

    private val _navigateEvent = MutableLiveData<Boolean>()
    val navigationEvent : LiveData<Boolean>
        get() = _navigateEvent

    fun navigationEnded(){
        _navigateEvent.value = false
    }

    init {
        _navigateEvent.value = false
    }

    fun selectSleepQuality(id : Int){
        uiScope.launch {
            update(id)
            _navigateEvent.value = true
        }
    }

    private suspend fun update(id : Int) {
        withContext(Dispatchers.IO){
            sleepNight = databaseDao.get(sleepNightKey)
            sleepNight.sleepQuality = id
            databaseDao.update(sleepNight)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}