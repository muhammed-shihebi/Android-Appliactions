package com.example.android.navigation

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.navigation.databinding.FragmentTitleBinding

/**
 * A simple [Fragment] subclass.
 */

/*
The Navigation drawer: see https://classroom.udacity.com/courses/ud9012/lessons/7466f670-3d47-4b60-8f6a-0914ce58f9ad/concepts/ef601b10-c5c1-4878-9c67-3f1493da1697
Prevent the Navigation drawer form showing up everywhere:   https://classroom.udacity.com/courses/ud9012/lessons/7466f670-3d47-4b60-8f6a-0914ce58f9ad/concepts/5b69515a-6403-470b-b0f5-a3cc9d34312d
 */
class TitleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // note we don't use the setContentView() instead we use inflate()
        val binding : FragmentTitleBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_title, container,
                false)
        binding.playButton.setOnClickListener {
            it.findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToGameFragment())
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = this.findNavController()
        // this will make the navigation controller navigate to the fragment that has the same id as the item given
        // if the NavigationUI fail to handle the selection the super method will be called, which will handle
        // the item without navigating.
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item)
    }
}