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

package com.example.android.marsrealestate.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * https://www.youtube.com/watch?time_continue=1&v=bAZnrC41qvk&feature=emb_logo
 * This class is used to store the data parsed by moshi library.
 * The names of the properties in this class should match the names in the json file
 *
 * Parcel is way to share objects between process. It is similar to a json object. Notice that in order
 * to an object to be usable in safe nav arguments it must be parcelable.
 *
 * A Bundle is a map with a string as a key and another object as a value. The objects in the same bundle
 * can differ so the same bundle could store Integers and Strings at the same time. A Bundle is a parcelable
 * object (implements the parcelable interface) and the objects in it must be also parcelable.
 *
 * The callback onSaveInstanceState() will be called if the activity get killed by the OS while running in the BG.
 * Just Parcelable could be stored in the onSaveInstanceState because when the app will run again it will be a new
 * process and just parcelable objects could be passed between processes. Since Bundles are parcelables, they are
 * used to be passed between processes.
 * */

@Parcelize
data class MarsProperty(
        val price: Double,
        val id: String,
        val type: String,
        // The names in the json file could be confusing to change them use:
        // This will still map the img_src to imgSrcUrl
        @Json(name = "img_src") val imgSrcUrl: String
) : Parcelable {
    // property with no backing filed (it gets calculated every time)
    val isForSale
        get() = type == "buy"

}




