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

package com.example.android.navigation

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentGameWonBinding


class GameWonFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentGameWonBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_game_won, container, false)
        binding.nextMatchButton.setOnClickListener {
            it.findNavController().navigate(GameWonFragmentDirections.actionGameWonFragmentToGameFragment())
        }
        val args = arguments?.let { GameWonFragmentArgs.fromBundle(it) }
        Toast.makeText(this.context, "Questions: ${args?.numQuestions} " +
                "Correct Answers: ${args?.numCorrect}", Toast.LENGTH_SHORT).show()
        // in order to have the share icon you have to specify that the fragment has an menu
        setHasOptionsMenu(true)
        return binding.root
    }
    // this function will inflate the menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.winner_menu, menu)

        // here we check first if the share menu can be used e.i. if there any apps that can handle that intent.
        if(null == activity?.packageManager?.let { getShareIntent()?.resolveActivity(it) }){
            // this will make the share icon invisible if there is no app that can handle the request.
            menu.findItem(R.id.share).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.share ->shareSucess()
        }
        return super.onOptionsItemSelected(item)
    }

    // This function will return the ready shareIntent object
    private fun getShareIntent(): Intent? {
        val args = arguments?.let { GameWonFragmentArgs.fromBundle(it) }

        // way 1
//        // here shareIntent will be handled just by the apps that can handle the action Send.
//        val shareIntent = Intent(Intent.ACTION_SEND)
//        // only the activities that can handle text can handle this action.
//        shareIntent.type = "text/plain"
//        if (args != null) {
//            // this will provide arguments for the intent.
//            // here text has predefined key
//            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_success_text, args.numCorrect, args.numQuestions))
//        }
//        return shareIntent

        // way 2
        return this.activity?.let {
            ShareCompat.IntentBuilder.from(it).setText(getString(R.string.share_success_text,args?.numCorrect, args?.numQuestions))
                .setType("text/plain")
                    .intent
        }
    }

    private fun shareSucess(){
        // this start the activity that can handle this intent.
        startActivity(getShareIntent())
    }

}
