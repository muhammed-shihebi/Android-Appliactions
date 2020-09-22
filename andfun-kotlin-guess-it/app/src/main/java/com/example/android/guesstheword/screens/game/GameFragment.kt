/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    lateinit var viewModel: GameViewModel

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_fragment,
                container,
                false
        )

        // Why use viewModelProvider? because is does not create the viewModel every time it get called
        // you don't want to create the viewModel again every time the onCreateView is called.
        // ViewModelProvider will just create the viewModel the first time and the next time you try to
        // call it, it will return the same created viewModel and thus survive the changes.
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // this means that the layout file now know about the viewModel and there is no need for the
        // controller to work as link between the two.
        binding.gameViewModel = viewModel

        // This call here allow us to ues the layout data using the life data objects.
        // After this call you can use the lifeDate objects of the viewModel in the layout file
        // to update ui views. So if the data in viewModel is updated then the data in the ui
        // will be updated as well.
        binding.setLifecycleOwner(this)

        viewModel.eventGameFinish.observe(viewLifecycleOwner, Observer {hasFinished ->
            // this will make sure that the gameFinished() wan't be called again after
            // the viewModel.onGameFinished() call.
            if(hasFinished){
                gameFinished()
                viewModel.onGameFinished()
            }
        })

        viewModel.shouldBuzz.observe(viewLifecycleOwner, Observer { shouldBuzz ->
            if(shouldBuzz){
                viewModel.currentBuzzType.value?.pattern?.let { buzz(it) }
                viewModel.onBuzzFinished()
            }
        })

        return binding.root
    }

    /**
     * Called when the game is finished
     */
    private fun gameFinished() {
        val action = GameFragmentDirections.actionGameToScore(viewModel.score.value ?: 0)
        findNavController(this).navigate(action)
    }

    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()
        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                //deprecated in API 26
                buzzer.vibrate(pattern, -1)
            }
        }
    }
}
