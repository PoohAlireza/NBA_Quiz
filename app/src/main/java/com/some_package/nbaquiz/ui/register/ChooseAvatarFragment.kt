package com.some_package.nbaquiz.ui.register

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.custom_view.BottomSheetRegister
import com.some_package.nbaquiz.custom_view.CustomLoading
import com.some_package.nbaquiz.interfaces.OnAvatarSelected
import com.some_package.nbaquiz.model.User
import com.some_package.nbaquiz.preferences.RegisterSharedPref
import com.some_package.nbaquiz.ui.main.MainActivity
import com.some_package.nbaquiz.util.DataState
import com.some_package.nbaquiz.util.StaticHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChooseAvatarFragment : Fragment(R.layout.fragment_choose_avatar) {
    private val TAG = "ChooseAvatarFragment"

    private lateinit var usernameTV: TextView
    private lateinit var avatarIV: ImageView
    private lateinit var changeAvatarBTN: Button
    private lateinit var nextBTN: Button
    private lateinit var backBtn: ImageView
    private lateinit var teamTV: TextView
    private lateinit var bottomSheetDialog: BottomSheetRegister
    private lateinit var progress:CustomLoading
    @Inject lateinit var registerSharedPref: RegisterSharedPref
    private val viewModel:RegisterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: avatar frag")
    }


    private fun init(view: View){
        usernameTV = view.findViewById(R.id.tv_name_choose_avatar_fragment)
        avatarIV = view.findViewById(R.id.iv_avatar_choose_avatar_fragment)
        changeAvatarBTN = view.findViewById(R.id.btn_change_avatar)
        nextBTN = view.findViewById(R.id.btn_next_choose_avatar)
        backBtn = view.findViewById(R.id.iv_back_choose_avatar)
        teamTV = view.findViewById(R.id.tv_team_choose_avatar)
        progress = view.findViewById(R.id.progress_choose_avatar_fragment)

        //setup
        val name = requireArguments().getString("username")
        usernameTV.text = name

        setupBottomSheet()
        setupChangeAvatar()
        observeProfile()
        observeRegisterState()
        setupBackButton()
        setupNextButton()

    }

    private fun setupChangeAvatar(){
        changeAvatarBTN.setOnClickListener {
            bottomSheetDialog.show(parentFragmentManager,null)
        }
    }

    private fun setupNextButton(){
        nextBTN.setOnClickListener{
            nextBTN.isClickable = false
            backBtn.isClickable = false
            changeAvatarBTN.isClickable = false
            val user = User(usernameTV.text.toString(),viewModel.avatar.value,viewModel.team.value,usernameTV.text.toString().toLowerCase())
            viewModel.registerUser(user)
        }
    }

    private fun observeRegisterState(){
        viewModel.dataStateRegister.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Loading -> {
                    displayProgress(true)
                }
                is DataState.Error -> {
                    displayProgress(false)
                    nextBTN.isClickable = true
                    backBtn.isClickable = true
                    changeAvatarBTN.isClickable = true
                }
                is DataState.Success -> {
                    displayProgress(false)
                    registerSharedPref.register(true)
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }
            }
        })
    }

    private fun setupBackButton(){
        backBtn.setOnClickListener {
            viewModel.backFromChooseAvatarToChooseName()
            Navigation.findNavController(backBtn).navigate(R.id.action_chooseAvatarFragment_to_chooseNameFragment)
        }
    }

    private fun displayProgress(state:Boolean){
        progress.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun setupBottomSheet(){
        bottomSheetDialog = BottomSheetRegister(
                object : OnAvatarSelected {
                    override fun onSelected(image: Drawable?, index: Int) {
                        viewModel.setAvatar(index)
                    }

                },
                object : OnAvatarSelected {
                    override fun onSelected(image: Drawable?, index: Int) {
                        viewModel.setTeam(index)
                    }

                })
    }

    private fun observeProfile(){
        viewModel.avatar.observe(viewLifecycleOwner, Observer {
            avatarIV.setImageDrawable(ContextCompat.getDrawable(requireContext(), StaticHolder.avatars[it]))
            bottomSheetDialog.avatarIndexChanged(it)
        })

        viewModel.team.observe(viewLifecycleOwner, Observer {
            teamTV.text = StaticHolder.teams_name[it]
            bottomSheetDialog.teamIndexChanged(it)
        })
    }

}