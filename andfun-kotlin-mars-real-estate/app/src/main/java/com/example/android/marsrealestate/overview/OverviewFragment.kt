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

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.databinding.FragmentOverviewBinding
import com.example.android.marsrealestate.network.MarsApiFilter

class OverviewFragment : Fragment() {
    private val viewModel: OverviewViewModel by lazy {
        ViewModelProvider(this).get(OverviewViewModel::class.java)
    }

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentOverviewBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        // If data get change the ui items will be updated automatically because we have bound the
        // data with the ui using dataBinding. Note that the bounded data must be LiveData.
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel


        //===================================================

        val marsPhotoClickListener = MarsPhotoClickListener {
            viewModel.setSelectedProperty(it)
        }

        viewModel.selectedProperty.observe(viewLifecycleOwner, Observer { marsProperty ->
            marsProperty?.let {
                this.findNavController().navigate(OverviewFragmentDirections.actionShowDetail(it))
                // because of the lifecycle of the fragment navigating away destroy the fragment but not the
                // viewModel, so when we recreate the fragment, it will begin to observer this property
                // and since this property is not null the observer will get the resent data which will trigger
                // this code. To prevent that form happening we set the selectedProperty to after finishing.
                viewModel.unselectedSelectedProperty()
            }
        })
        // =================================================

        /**
         * The following code will pass data to the adapter which will create a cell in the grid for each
         * item in the list.
         * This could also be done using an Binding adapter that will observe data list automatically*/
        binding.photosGrid.adapter = PhotoGridAdapter(marsPhotoClickListener)
        //
        //viewModel.properties.observe(viewLifecycleOwner, Observer {
        //    it?.let {
        //        adapter.submitList(it)
        //    }
        //})

        // =================================================

        setHasOptionsMenu(true)
        return binding.root
    }


    /**
     * Inflates the overflow menu that contains filtering options.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // This function will be called if a menu item is selected.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_all_menu -> {
                viewModel.updateFilter(MarsApiFilter.SHOW_ALL)
            }
            R.id.show_rent_menu -> {
                viewModel.updateFilter(MarsApiFilter.SHOW_RENT)
            }
            R.id.show_buy_menu -> {
                viewModel.updateFilter(MarsApiFilter.SHOW_BUY)
            }
        }
        return true
    }
}
