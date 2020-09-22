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

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import com.example.android.trackmysleepquality.getImageId



// instead of using RecyclerView.Adapter<SleepNightAdapter.ViewHolder>() we use
// ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) because it keeps
// track of the list of data automatically.
// SleepNight is the type of data in the list.
// SleepNightDiffCallback() will be used figure out what has changed in the list.

class SleepNightAdapter : ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) {
    // because we extended the listAdapter class there is no need to define a list of data because it is already
    // there.
//    var data = listOf<SleepNight>()
//        // this is needed to tell the recyclerview when a item is changed
//        set(value) {
//            field = value
//
//            // this is used to notify the recycler view that the data has changed.
//            // after calling this every item in the list gets rebind and redraw which is very expensive.
//            notifyDataSetChanged()
//        }


    // this is also not needed.
//    override fun getItemCount(): Int {
//        return data.size
//    }

    /**
     * This function tells the recycler view how to replace the item in the holder.
     * holder object could be new and hold no view or old and hold a view.
     * what we do here is recycle the holder and reuse it to hold a new item of data
     * this method will be called just for items that are visible or just about to be visible.
     * */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // if we don't set this to black at the beginning, the views for the lower sleep quality will
        // be reused by the higher quality views and those will be red also.

        // this can't be used because we deleted the data var
//        val item = data[position]

        val item = getItem(position)
        holder.bind(item)
    }

    /**
     * This function is called when the recycler view needs to create a new view holder.
     * This happen if the recycler view just got started and needs a new view holder or
     * if the number of the items on the screen increased.
     * @param parent is the ViewGroup where the new view will be added before being displayed and
     * this will always be the RecyclerView.
     * @param viewType is used when there is more then one view type in the same recycler view.
     * */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }




    /**
     * A view holder can display complete layout with its view.
     * @param binding is the parent of the views in the layout. */

    // private constructor: prevent creating instances of viewHolder outside of form function.
    class ViewHolder private constructor(val binding: ListItemSleepNightBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: SleepNight) {
            // bind the view (binding sleep) with data (item)
            binding.sleep = item
            // this is called to accelerate the sizing of the views
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder {
                // this means we create a layout inflater based on the parent view.
                val layoutInflater = LayoutInflater.from(parent.context)
////                // the inflater is used to inflate text item view.
////                // false to not add this item to the parent now.
////                // text_item_view is just a layout file that has one view
////                val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false)

                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

// itemCallBack is used to figure out the changes in list of data.

class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>(){

    // called to check if 2 objects represent the same item. Used to check if item 1. edited 2. moved 3. removed
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    // this will be called if areItemsTheSame() returns true.
    // this equality should be based on the Ui, so if your item changed but its ui representation didn't
    // this method should return true.
    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }

}