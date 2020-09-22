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
 *
 */

package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import com.example.android.marsrealestate.overview.MarsApiStatus.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OverviewViewModel : ViewModel() {
    // ============================================
    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<MarsApiStatus>()

    // The external immutable LiveData for the request status String
    val status: LiveData<MarsApiStatus>
        get() = _status

    // ============================================

    // this will be used to track one property form the data
    private val _properties = MutableLiveData<List<MarsProperty>>()

    // This is live data and can be automatically observed in the layout file using data binding
    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    // ============================================
    private var viewModelJob = Job()

    // this scope uses the Ui thread. No need to use other thread since retrofit uses background thread.
    private var uiCoroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */

    // ============================================

    init {
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }

    // ============================================

    private val _selectedProperty = MutableLiveData<MarsProperty>()
    val selectedProperty: LiveData<MarsProperty>
        get() = _selectedProperty

    fun setSelectedProperty(property: MarsProperty) {
        _selectedProperty.value = property
    }

    fun unselectedSelectedProperty() {
        _selectedProperty.value = null
    }

    // ============================================
    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
        uiCoroutineScope.launch {
            try {
                _status.value = LOADING
                val result: List<MarsProperty> = MarsApi.retrofitService.getPropertiesAsync(filter.value)
                if (result.size > 0) {
                    // This will hold the values of one property including the image url
                    _properties.value = result
                    _status.value = DONE
                }
            } catch (t: Throwable) {
                _status.value = ERROR
                // this will clear the recycler view
                _properties.value = ArrayList()
            }
        }
    }

    // Loading data will stop if this function get called and it will if the viewModel get destroyed.
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun updateFilter(filter: MarsApiFilter) {
        getMarsRealEstateProperties(filter)
    }
}


enum class MarsApiStatus {
    LOADING, ERROR, DONE
}