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

/**
 * This file will hold the layer used by overviewViewModel to communicate with the network.
 * Retrofit creates a network api instead of communicating with the network directly.
 * It converts the data form row to usable objects in kotlin code.
 * To do so Retrofit need to be linked with a converter library that knows how to convert the data.
 * Retrofit can then take our interface and implemented for us. We can then call this implementation to talk to
 * our web server.
 * Retrofit will run the request in the background thread and not the ui thread
 * */

package com.example.android.marsrealestate.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "https://mars.udacity.com/"

/**
 * Moshi parse json string into kotlin objects.
 * */
private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()


/**
 * The ScalarsConverterFactory support converting json files to strings
 * */
private val retrofit = Retrofit.Builder()
        // this tells retrofit how to use moshi wo parse json file into kotlin objects.
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        // call adapter add the ability to create api other then the default one.
        // this allows us to use coroutine deferred instead of Call<>
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()

/**
 * This interface explain how the retrofit talk to the web server
 * */

interface MarsApiService {
    /**
     * The annotation @Get defines the location in the website, where to get the data.
     * The Call object is used to start the request.
     * */
    /**
     * Deferred form coroutines is a coroutine's jab that can directly return a result.
     * Deferred can cancel or determine the state of the coroutine.
     * Unlike Job deferred has await method which make the code wait without blocking until the value is ready
     * getPropertiesAsync() will be implemented automatically by retrofit */
    @GET("realestate")
    suspend fun getPropertiesAsync(@Query("filter") type: String): List<MarsProperty>
}

// This object will be used to implement the MarsApiService Interface
// Now we can use this object to communicate with the web server
object MarsApi {
    val retrofitService: MarsApiService by lazy {
        retrofit.create(MarsApiService::class.java)
    }
}

enum class MarsApiFilter(val value: String) {
    SHOW_RENT("rent"), SHOW_BUY("buy"), SHOW_ALL("all")
}