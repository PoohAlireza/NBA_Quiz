package com.some_package.nbaquiz.ui.main

import android.animation.Animator
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.custom_view.BottomSheetEdit
import com.some_package.nbaquiz.interfaces.OnApplyProfileEditClicked
import com.some_package.nbaquiz.interfaces.OnAvatarSelected
import com.some_package.nbaquiz.util.DataState
import com.some_package.nbaquiz.util.StaticHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var cardView: CardView
    private lateinit var avatarIV: ImageView
    private lateinit var pointsTV: TextView
    private lateinit var gamesTV: TextView
    private lateinit var teamTV: TextView
    private lateinit var winsTV: TextView
    private lateinit var usernameTV: TextView
    private lateinit var editBTN: Button
    private lateinit var bottomSheetDialog: BottomSheetEdit
    private val viewModel:MainViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)

        setupBottomSheet()
        observeUserData()
        observeEditProfile()
        initProperties()
        setProfileInMiddle()
        setupEditButton()
    }

    private fun init(view: View){
        avatarIV = view.findViewById(R.id.iv_fragment_profile)
        avatarIV.alpha = 0f
        cardView = view.findViewById(R.id.cv_fragment_profile)
        pointsTV = view.findViewById(R.id.tv_points_profile)
        gamesTV = view.findViewById(R.id.tv_games_profile)
        winsTV = view.findViewById(R.id.tv_wins_profile)
        teamTV = view.findViewById(R.id.tv_team_profile)
        usernameTV = view.findViewById(R.id.tv_username_profile)
        editBTN = view.findViewById(R.id.btn_edit_avatar)
    }

    private fun initProperties(){
        val user = viewModel.userData.value
        usernameTV.text = user!!.username
        gamesTV.text = "${user.games}"
        pointsTV.text = "${user.points}"
        winsTV.text = "${user.wins}"
        teamTV.text = StaticHolder.teams_name[user.team!!]
        avatarIV.setImageDrawable(ResourcesCompat.getDrawable(resources, StaticHolder.avatars[user.avatar!!], null))
    }

    private fun observeUserData(){
        viewModel.userData.observe(viewLifecycleOwner, Observer {
            teamTV.text = StaticHolder.teams_name[it.team!!]
            avatarIV.setImageDrawable(ResourcesCompat.getDrawable(resources, StaticHolder.avatars[it.avatar!!], null))
            bottomSheetDialog.avatarIndexChanged(it.avatar!!)
            bottomSheetDialog.teamIndexChanged(it.team!!)
        })
    }

    private fun setProfileInMiddle() {
        avatarIV.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                avatarIV.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val xCenterCard = ((cardView.left + cardView.right) / 2).toFloat()
                val xCenterImage = ((avatarIV.left + avatarIV.right) / 2).toFloat()
                val yCenterImage = ((avatarIV.top + avatarIV.bottom) / 2).toFloat()
                val xPoint = xCenterCard - xCenterImage
                val yPoint = cardView.top - yCenterImage
                avatarIV.animate().translationX(xPoint).translationY(yPoint).setDuration(1)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {}
                        override fun onAnimationEnd(animation: Animator) {
                            avatarIV.animate().alpha(1f).duration = 500
                        }

                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                    })
            }
        })
    }

    private fun setupEditButton(){
        editBTN.setOnClickListener {
            bottomSheetDialog.show(parentFragmentManager,null)
            StaticHolder.fullScreen(activity)
        }
    }

    private fun setupBottomSheet(){
        bottomSheetDialog = BottomSheetEdit(object : OnApplyProfileEditClicked{
            override fun onClicked(avatarIndex: Int, teamIndex: Int) {
                val avatar = viewModel.userData.value!!.avatar
                val team = viewModel.userData.value!!.team
                if (avatarIndex == avatar && teamIndex == team){
                    bottomSheetDialog.dismiss()
                    return
                }

                viewModel.editProfile(avatarIndex,teamIndex)
                bottomSheetDialog.dismiss()

            }

        })
    }


    private fun observeEditProfile(){
        viewModel.dataStateEditProfile.observe(viewLifecycleOwner, Observer {
            when(it){
                is DataState.Success ->{
                    viewModel.initUserData()
                }
                is DataState.Loading ->{

                }
                is DataState.Error ->{

                }
            }
        })
    }




}