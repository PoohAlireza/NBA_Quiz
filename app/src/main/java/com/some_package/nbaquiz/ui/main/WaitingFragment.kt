package com.some_package.nbaquiz.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.some_package.nbaquiz.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WaitingFragment : Fragment(R.layout.fragment_waiting) {

    private lateinit var yourAvatarIV: ImageView
    private lateinit var rivalAvatarIV: ImageView
    private lateinit var yourNameTV: TextView
    private lateinit var rivalNameTV: TextView
    private lateinit var cancelBTN: Button

    private val viewModel:MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View){
        yourAvatarIV = view.findViewById(R.id.iv_avatar_you_waiting_fragment);
        yourNameTV = view.findViewById(R.id.tv_username_you_waiting_fragment);
        rivalAvatarIV = view.findViewById(R.id.iv_avatar_rival_waiting_fragment);
        rivalNameTV = view.findViewById(R.id.tv_username_rival_waiting_fragment);
        cancelBTN = view.findViewById(R.id.btn_cancel_waiting_fragment);
    }

    private fun findRival(){

    }

    private fun observe(){

    }

    private fun setupCancelButton(){

    }


}