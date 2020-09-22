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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsrealestate.databinding.GridViewItemBinding
import com.example.android.marsrealestate.network.MarsProperty

//=========================================

class PhotoGridAdapter(val marsPhotoClickListener: MarsPhotoClickListener)
    : ListAdapter<MarsProperty, PhotoGridAdapter.ViewHolder>(MarsPropertyDiffCallback()) {

    //=========================================

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGridAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PhotoGridAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position), marsPhotoClickListener)
    }

    //=========================================

    class ViewHolder private constructor(val binding: GridViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MarsProperty, marsPhotoClickListener: MarsPhotoClickListener) {
            binding.property = item

            binding.marsPhotoClickListener = marsPhotoClickListener

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = GridViewItemBinding.inflate(LayoutInflater.from(parent.context))
                return ViewHolder(binding)
            }
        }
    }

    //=========================================
}

//=========================================

class MarsPropertyDiffCallback : DiffUtil.ItemCallback<MarsProperty>() {
    override fun areItemsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
        return oldItem.id == newItem.id
    }
}

//=========================================

class MarsPhotoClickListener(val clickListener: (marsProperty: MarsProperty) -> Unit) {
    fun onClick(marsProperty: MarsProperty) = clickListener(marsProperty)
}