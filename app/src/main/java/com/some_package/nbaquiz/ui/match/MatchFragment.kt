package com.some_package.nbaquiz.ui.match

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.some_package.nbaquiz.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MatchFragment : Fragment(R.layout.fragment_match) {

    private val TAG = "MatchFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireArguments()
        Log.i(TAG, "onViewCreated: $bundle")
    }

}