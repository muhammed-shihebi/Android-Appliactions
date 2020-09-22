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
 * Glide takes a url from an xml attribute that is associated with an image view and use it to load the image.
 * */
package com.example.android.marsrealestate

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.marsrealestate.network.MarsProperty
import com.example.android.marsrealestate.overview.MarsApiStatus
import com.example.android.marsrealestate.overview.MarsApiStatus.*
import com.example.android.marsrealestate.overview.PhotoGridAdapter

// By using a bindingAdapter to set the list of data to the recyclerView will cause the list of liveData
// of the MarsProperty to be observed automatically.

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<MarsProperty>?) {
    val photoGridAdapter = recyclerView.adapter as PhotoGridAdapter
    photoGridAdapter.submitList(data)
}

// This tells data binding to run this binding adapter when an xml item has the imageUrl attribute
@BindingAdapter("imageUrl")
// in order to make this adapter unusable for other views we pass imageView as a parameter
fun bindImage(imageView: ImageView, imageUrl: String?) {
    imageUrl?.let { url ->
        // converting the url to uri because glide don't accept urls
        val imageUri = url.toUri().buildUpon().scheme("https").build()
        Glide.with(imageView.context)
                .load(imageUri)
                // this is used to make a placeholder while waiting for an image to load or fail
                .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imageView)
    }
}

// note that the your function here should always accept nullable data.
@BindingAdapter("marsApiStatus")
fun bindStatus(statusImageView: ImageView, status: MarsApiStatus?) {
    when (status) {
        ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}


//=========================================

// instead of doing the following you should transformation.
// 1. make use of string resource to format the data.
// 2. the application.context will help you get the resources

//@BindingAdapter("doubleValueFormatted")
//fun bindDoubleValue(textView: TextView, property: MarsProperty?){
//    property?.let {
//        val price = "$${NumberFormat.getNumberInstance(Locale.US).format(it.price)}"
//        if(it.isForSale){
//            textView.text = price
//        }else{
//            textView.text = "$price/month"
//        }
//    }
//}
//
//@BindingAdapter("propertyTypeTextFormatted")
//fun bindPropertyTypeText(textView: TextView, property: MarsProperty?){
//    property?.let {
//        if (it.isForSale){
//            textView.text = "For Sale"
//        }else{
//            textView.text = "For Rent"
//        }
//    }
//}